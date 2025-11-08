package kr.apo2073.api.toonLiv;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import kr.apo2073.api.toonLiv.data.Chatting;
import kr.apo2073.api.toonLiv.data.Donation;
import kr.apo2073.api.toonLiv.exception.TokenNotFound;
import kr.apo2073.api.toonLiv.listener.ToonationEventListener;
import kr.apo2073.api.toonLiv.utilities.Debugger;
import kr.apo2073.api.toonLiv.utilities.Streamer;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class Toonation extends WebSocketListener {
    private final String key;
    private boolean timeout;
    private final Debugger debugger;
    private final String payload;
    private WebSocket socket;
    private final List<ToonationEventListener> listeners;
    private final Subject<Donation> donationSubject;
    private final Subject<Chatting> chattingSubject;
    private volatile boolean closed=false;

    private static final ExecutorService seleniumExecutor = Executors.newSingleThreadExecutor();

    public static Future<Streamer> getStreamerAsync(String id) {
        return seleniumExecutor.submit(() -> getStreamer(id));
    }

    public static Streamer getStreamer(String id) {
        chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://toon.at/donate/" + id);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement el = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class*='DisplayCreatorName']"))
            );

            String nickname = el.getText();
            return new Streamer(id, nickname);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            driver.quit();
        }
    }

    public Toonation(ToonationBuilder builder) {
        this.key=builder.key;
        this.timeout= builder.timeout;
        this.listeners=builder.listeners;
        this.debugger=new Debugger(builder.debug);

        try {
            Document document= Jsoup
                    .connect("https://toon.at/widget/alertbox/"+key)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
            Elements el=document.getElementsByTag("script");
            String script=el.stream().filter(e-> !e.hasAttr("src"))
                    .map(Element::toString).collect(Collectors.joining());
            String payload=parsePayload(script);
            if (payload==null) throw new TokenNotFound();
            this.payload=payload;
//            System.out.println(payload);
            OkHttpClient client=new OkHttpClient().newBuilder()
                    .pingInterval(12, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request=new Request.Builder()
                    .url("wss://ws.toon.at/"+payload)
                    .build();
            socket =client.newWebSocket(request, this);

            donationSubject= PublishSubject.create();
            chattingSubject= PublishSubject.create();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject json= new Gson().fromJson(text, JsonObject.class);
//            System.out.println(text);
            Donation donation=getDonation(json);
            if (donation!=null) {
                for (ToonationEventListener listener: listeners)
                    listener.onDonation(donation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePayload(String script) {
        Pattern p = Pattern.compile("\\\\u0022payload\\\\u0022:\\\\u0022(.*?)\\\\u0022,");
        Matcher m = p.matcher(script);
        if (m.find()) return m.group(1);
        return null;
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        try {
            if(!timeout) {
                debugger.debug("투네이션에 연결되었습니다");
                for (ToonationEventListener listener : listeners) {
                    listener.onConnect();
                }
            } else {timeout=false;}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (closed) return;
        try {
            timeout = true;

            try {
                webSocket.close(1000, null);
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }

            debugger.debug("WebSocket connection failed: " + t.getMessage());
            t.printStackTrace();

            if (response != null) {
                debugger.debug("HTTP code: " + response.code());
                debugger.debug("HTTP message: " + response.message());
            } else {
                debugger.debug("No HTTP response received. Likely a network or handshake failure.");
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .pingInterval(12, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("wss://ws.toon.at/" + payload)
                    .build();
            socket = client.newWebSocket(request, this);

            for (ToonationEventListener listener : listeners) {
                listener.onFail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        debugger.debug("연결이 종료되었습니다");
        for (ToonationEventListener listener : listeners) {
            listener.onDisconnect();
        }
    }


    public void close() {
        try {
            if (donationSubject != null && !donationSubject.hasComplete()) {
                donationSubject.onComplete();
            }
            if (chattingSubject != null && !chattingSubject.hasComplete()) {
                chattingSubject.onComplete();
            }
            if (socket != null) {
                socket.close(1000, "Client closing");
                socket = null;
            }
            closed=true;
            debugger.debug("모든 연결이 정상적으로 종료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Donation getDonation(JsonObject json) {
        try {
            if (!json.has("content")) return null;
            json= json.get("content").getAsJsonObject();
            return new Donation(
                    json.get("account").toString(),
                    json.get("name").toString(),
                    json.get("message").toString(),
                    json.get("amount").getAsLong(),
                    this.key
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getKey() {
        return key;
    }

    public boolean isTimeout() {
        return timeout;
    }
}
