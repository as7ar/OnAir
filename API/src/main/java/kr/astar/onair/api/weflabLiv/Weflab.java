package kr.astar.onair.api.weflabLiv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.astar.onair.api.weflabLiv.data.alert.Donation;
import kr.astar.onair.api.weflabLiv.data.alert.DonationData;
import kr.astar.onair.api.weflabLiv.data.alert.PlatformData;
import kr.astar.onair.api.weflabLiv.data.alert.User;
import kr.astar.onair.api.weflabLiv.data.streamer.StreamerData;
import kr.astar.onair.api.weflabLiv.listener.WeflabListener;
import lombok.Getter;
import lombok.NonNull;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Weflab extends WebSocketListener {
    private final String key;
    private final List<WeflabListener> listeners;

    private String idx;
    private StreamerData streamerData;

    private WebSocket socket;
    private boolean closed = true;

    public Weflab(WeflabBuilder weflabBuilder) {
        this.key = weflabBuilder.getKey();
        this.listeners= weflabBuilder.getListeners();

        try {
            Document document= Jsoup
                    .connect("https://weflab.com/page/"+ key)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:146.0) Gecko/20100101 Firefox/146.0")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .cookies(Map.of())
                    .get();

            Elements element = document.getElementsByTag("script");
            String script = element.stream()
                    .filter(e->
                            !e.hasAttr("src")
                            && e.data().contains("loginData = {")
                    )
                    .map(Element::toString)
                    .collect(Collectors.joining());

            JsonObject loginDataJson= parseLoginData(script);

//            System.out.println(loginDataJson.toString());

            String idx1 = loginDataJson.get("idx").getAsString();
            String streamerName= loginDataJson.get("user").getAsJsonObject().get("name").getAsString();
            String streamerEmail= loginDataJson.get("user").getAsJsonObject().get("email").getAsString();

            if (idx1==null) throw new Exception("Idx not found");
            this.idx = idx1;

            this.streamerData = new StreamerData(
                    loginDataJson.get("login_type").getAsString(),
                    idx1,
                    loginDataJson.get("userid").getAsString(),
                    streamerName, streamerEmail,
                    loginDataJson.get("mobile").getAsBoolean(),
                    loginDataJson.get("ios").getAsBoolean()
            );

            OkHttpClient client=new OkHttpClient().newBuilder()
                    .pingInterval(12, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
            Request request=new Request.Builder()
                    .url(
                            "wss://ssmain.weflab.com/socket.io/?idx=" + idx
                                    + "&type=page&page="
                                    + "chat"
                                    + "&EIO=4&transport=websocket"
                    )
                    .build();
            socket = client.newWebSocket(request, this);
            socket.send("40");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIdx() {
        return this.idx;
    }

    public StreamerData getStreamerData() {
        return this.streamerData;
    }

    private JsonObject parseLoginData(String script) {
        Pattern p = Pattern.compile("loginData\\s*=\\s*(\\{.*?})\\s*;", Pattern.DOTALL);
        Matcher m = p.matcher(script);

        if (!m.find())
            throw new IllegalStateException("loginData not found");

        String json = m.group(1);
//        System.out.println(json);
        return new Gson().fromJson(json, JsonObject.class);
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        for (WeflabListener listener: listeners)
            listener.onConnect(response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        if (text.equals("2")) { // Websocket Client Ping for checking Connected
            webSocket.send("3");
            return;
        }

        if (text.startsWith("40")) { // Websocket Register
            // text: 40{"sid":"RKvSWkbKA8timc0JCl4G"}
            webSocket.send("42[\"msg\",{\"type\":\"join\",\"page\":\"page\",\"idx\":\""
                    + this.idx + "\",\"pageid\":\"chat\",\"preset\":\"0\"}]");
            return;
        }

        if (text.startsWith("42")) {
            // text
            String rawJson = text.startsWith("42")
                    ? text.substring(2)
                    : text;
            try {
                JsonArray json = JsonParser.parseString(rawJson).getAsJsonArray();
                String receive = json.get(0).getAsString();
                JsonObject receiveData = json.get(1).getAsJsonObject();

                if (Objects.equals(receive, "pong")) {
                    String platform= receiveData.get("platform").getAsString();
                    return;
                }

                if (Objects.equals(receive, "msg")) {
                    String type= receiveData.get("type").getAsString(); // test_donation
                    JsonObject data= receiveData.get("data").getAsJsonObject();
                    boolean isTestDonation= Objects.equals(type, "test_donation");

                    String page= receiveData.get("page").getAsString();
                    String idx= receiveData.get("idx").getAsString();
                    String pageid= receiveData.get("pageid").getAsString();
                    String preset= receiveData.get("preset").getAsString();

                    DonationData donationData= new DonationData(
                            page, idx,
                            pageid, preset
                    );

                    Donation donation= new Donation(
                            new User(
                                    data.get("uname").getAsString(),
                                    data.get("uid").getAsString()
                            ),
                            data.get("msg").getAsString(),
                            data.get("value").getAsLong(),
                            isTestDonation,
                            data.get("time").getAsLong(),
                            donationData,
                            new PlatformData(
                                    data.get("platform").getAsString(),
                                    data.get("type").getAsString()
                            )
                    );

                    for (WeflabListener listener: listeners)
                        listener.onDonation(donation);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean close() {
        try {
            if (socket != null) {
                socket.close(1000, "Client closing");
                socket = null;
            }
            closed=true;
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        for (WeflabListener listener: listeners)
            listener.onDisconnect(code, reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        if (closed) return;
        webSocket.close(1000, "Error occurred");
        for (WeflabListener listener: listeners)
            listener.onFail(t, response);
    }
}
