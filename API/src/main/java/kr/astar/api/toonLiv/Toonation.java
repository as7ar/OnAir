package kr.astar.api.toonLiv;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kr.astar.api.toonLiv.data.Donation;
import kr.astar.api.toonLiv.exception.TokenNotFound;
import kr.astar.api.toonLiv.listener.ToonationEventListener;
import kr.astar.api.toonLiv.utilities.Debugger;
import kr.astar.api.toonLiv.utilities.Streamer;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class Toonation extends WebSocketListener {

    private final String key;
    private volatile boolean timeout;
//    private final Debugger debugger;
    private final String payload;
    private volatile WebSocket socket;
    private final List<ToonationEventListener> listeners;
    private volatile boolean closed = false;

    private static final ExecutorService seleniumExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService wsExecutor = Executors.newSingleThreadExecutor();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .pingInterval(12, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    public static Future<Streamer> getStreamerAsync(String id) {
        return seleniumExecutor.submit(() -> getStreamer(id));
    }

    public static Streamer getStreamer(String id) {
        chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get("https://toon.at/donate/" + id);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement el = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("[class*='DisplayCreatorName']")
                    )
            );
            return new Streamer(id, el.getText());
        } catch (Exception e) {
            return null;
        } finally {
            driver.quit();
        }
    }

    public Toonation(ToonationBuilder builder) {
        this.key = builder.key;
        this.timeout = builder.timeout;
        this.listeners = builder.listeners;
//        this.debugger = new Debugger(builder.debug);

        this.payload = loadPayload();
        if (payload == null) throw new RuntimeException(new TokenNotFound());

        connectAsync();
    }

    private String loadPayload() {
        try {
            Document document = Jsoup
                    .connect("https://toon.at/widget/alertbox/" + key)
                    .userAgent("Mozilla/5.0")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();

            String script = document.getElementsByTag("script").stream()
                    .filter(e -> !e.hasAttr("src"))
                    .map(Element::toString)
                    .collect(Collectors.joining());

            return parsePayload(script);
        } catch (Exception e) {
            return null;
        }
    }

    private String parsePayload(String script) {
        Matcher m = Pattern
                .compile("\\\\u0022payload\\\\u0022:\\\\u0022(.*?)\\\\u0022,")
                .matcher(script);
        return m.find() ? m.group(1) : null;
    }

    private void connectAsync() {
        wsExecutor.execute(this::connect);
    }

    private void connect() {
        if (closed) return;

        Request request = new Request.Builder()
                .url("wss://ws.toon.at/" + payload)
                .build();

        socket = client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        if (!timeout) {
            Debugger.debug("투네이션에 연결되었습니다");
            listeners.forEach(ToonationEventListener::onConnect);
        }
        timeout = false;
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        try {
            JsonObject json = new Gson().fromJson(text, JsonObject.class);
            Donation donation = getDonation(json);
            if (donation == null) return;

            listeners.forEach(l -> l.onDonation(donation));
        } catch (Exception ignored) {}
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        if (closed) return;

        timeout = true;
        Debugger.debug("WebSocket 오류: " + t.getMessage());

        wsExecutor.execute(() -> {
            try {
                webSocket.cancel();
            } catch (Exception ignored) {}

            if (!closed) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}

                connect();
            }
        });

        listeners.forEach(ToonationEventListener::onFail);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Debugger.debug("연결이 종료되었습니다");
        listeners.forEach(ToonationEventListener::onDisconnect);
    }

    public void close() {
        wsExecutor.execute(() -> {
            try {
                closed = true;

                if (socket != null) {
                    socket.close(1000, "Client closing");
                    socket.cancel();
                }

                Debugger.debug("모든 연결이 정상적으로 종료되었습니다.");
            } catch (Exception ignored) {}
        });
    }

    private Donation getDonation(JsonObject json) {
        if (!json.has("content")) return null;
        JsonObject c = json.getAsJsonObject("content");

        return new Donation(
                c.get("account").getAsString(),
                c.get("name").getAsString(),
                c.get("message").getAsString(),
                c.get("amount").getAsLong(),
                key
        );
    }

    public String getKey() {
        return key;
    }

    public boolean isTimeout() {
        return timeout;
    }
}