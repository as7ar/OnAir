package kr.apo2073.soopliv.soop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.apo2073.soopliv.exception.DoneException;
import kr.apo2073.soopliv.exception.ExceptionCode;
import kr.apo2073.soopliv.utilities.Debugger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SoopApi {
    public static SoopLiveInfo getPlayerLive(String bjid) {
        return getPlayerLive(bjid, false);
    }

    public static SoopLiveInfo getPlayerLive(String bjid, boolean isDebug) {
        Debugger debugger = new Debugger();
        debugger.isDebug = isDebug;
        String requestURL = String.format("https://live.sooplive.co.kr/afreeca/player_live_api.php?bjid=%s", bjid);

        try {
            HttpClient client = HttpClient.newHttpClient();

            Map<Object, Object> bodyMap = Map.of(
                    "bid", bjid,
                    "type", "live",
                    "pwd", "",
                    "player_type", "html5",
                    "stream_type", "common",
                    "quality", "HD",
                    "mode", "landing",
                    "is_revive", "false",
                    "from_api", "0"
            );

            debugger.log("Request URL: " + requestURL + "\nRequest Body: " + bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestURL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(ofFormData(bodyMap))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new DoneException(ExceptionCode.API_CHAT_CHANNEL_ID_ERROR);
            }

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject channel = jsonObject.getAsJsonObject("CHANNEL");

            if (channel == null) {
                throw new DoneException(ExceptionCode.API_CHAT_CHANNEL_ID_ERROR);
            }

            SoopLiveInfo soopLiveInfo = new SoopLiveInfo(
                    channel.get("CHDOMAIN").getAsString(),
                    channel.get("CHATNO").getAsString(),
                    channel.get("FTK").getAsString(),
                    channel.get("TITLE").getAsString(),
                    channel.get("BJID").getAsString(),
                    channel.get("BNO").getAsString(),
                    channel.get("CHIP").getAsString(),
                    String.valueOf(Integer.parseInt(channel.get("CHPT").getAsString()) + 1),
                    channel.get("CTIP").getAsString(),
                    channel.get("CTPT").getAsString(),
                    channel.get("GWIP").getAsString(),
                    channel.get("GWPT").getAsString()
            );

            debugger.log(soopLiveInfo.toString());
            return soopLiveInfo;

        } catch (DoneException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) builder.append("&");

            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }

        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
