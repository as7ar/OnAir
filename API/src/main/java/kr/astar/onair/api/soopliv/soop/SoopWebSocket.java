package kr.astar.onair.api.soopliv.soop;

import kr.astar.onair.api.soopliv.data.Chat;
import kr.astar.onair.api.soopliv.data.Donate;
import kr.astar.onair.api.soopliv.data.User;
import kr.astar.onair.api.soopliv.soop.listener.SoopEventListener;
import kr.astar.onair.api.soopliv.utilities.Debugger;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SoopWebSocket extends WebSocketListener {

    private final SoopLiveInfo liveInfo;
    private final Map<String, String> soopUser;
    private final SoopEventListener listener;
    private final Debugger debugger;
    private final boolean poong;

    private OkHttpClient client;
    private WebSocket socket;

    private ScheduledExecutorService pingExecutor;
    private volatile boolean isAlive = true;

    private final Map<String, SoopPacket> packetMap = new ConcurrentHashMap<>();

    private static final String F = "\u000c";
    private static final String ESC = "\u001b\t";

    private static final String COMMAND_PING = "0000";
    private static final String COMMAND_CONNECT = "0001";
    private static final String COMMAND_JOIN = "0002";
    private static final String COMMAND_ENTER = "0004";
    private static final String COMMAND_ENTER_FAN = "0127";
    private static final String COMMAND_CHAT = "0005";
    private static final String COMMAND_DONE = "0018";

    private static final String CONNECT_PACKET =
            makePacket(COMMAND_CONNECT, F.repeat(3) + "16" + F);

    private static final String CONNECT_RES_PACKET =
            makePacket(COMMAND_CONNECT, F.repeat(2) + "16|0" + F);

    private static final String PING_PACKET =
            makePacket(COMMAND_PING, F);

    public SoopWebSocket(
            String serverUri,
            SoopLiveInfo liveInfo,
            Map<String, String> soopUser,
            boolean genBalloon,
            boolean isDebug,
            SoopEventListener listener
    ) {
        this.liveInfo = liveInfo;
        this.soopUser = soopUser;
        this.poong = genBalloon;
        this.listener = listener;

        this.debugger = new Debugger();
        this.debugger.isDebug = isDebug;

        this.client = new OkHttpClient.Builder()
                .pingInterval(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(serverUri)
                .build();

        this.socket = client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        debugger.log("[" + soopUser.get("nickname") + "] 숲 웹소켓 연결됨");
        isAlive = true;

        socket.send(CONNECT_PACKET);

        pingExecutor = Executors.newSingleThreadScheduledExecutor();
        pingExecutor.scheduleAtFixedRate(() -> {
            if (!isAlive) return;
            try {
                socket.send(PING_PACKET);

                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);
                packetMap.entrySet().removeIf(e ->
                        e.getValue().getReceivedTime().isBefore(cutoff)
                );
            } catch (Exception e) {
                debugger.log("Ping 오류: " + e.getMessage());
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String message) {
        if (CONNECT_RES_PACKET.equals(message)) {
            socket.send(makePacket(
                    COMMAND_JOIN,
                    F + liveInfo.CHATNO() + F.repeat(5)
            ));
            return;
        }

        try {
            SoopPacket packet = new SoopPacket(
                    message.replace(ESC, "").split(F)
            );

            String cmd = packet.getCommand();
            List<String> dataList = switch (cmd) {
                case COMMAND_ENTER, COMMAND_ENTER_FAN -> null;
                default -> packet.getDataList();
            };

            if (dataList == null) return;

            if (cmd.equals(COMMAND_DONE)) {
                handleDone(dataList, packet);
            } else if (cmd.equals(COMMAND_CHAT)) {
                handleChat(dataList);
            }
        } catch (Exception e) {
            debugger.log("숲 메시지 파싱 오류: " + e.getMessage());
        }
    }

    private void handleDone(List<String> dataList, SoopPacket packet) {
        String nickname = dataList.get(2);
        packetMap.put(nickname, packet);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                SoopPacket removed = packetMap.remove(nickname);
                if (removed == null) return;

                int temp = Integer.parseInt(dataList.get(3));
                int amount = poong ? temp : temp * 100;

                User user = new User(dataList.get(1), dataList.get(5));
                handleDone(user, amount, "");
            } catch (Exception ignored) {}
        });
    }

    private void handleChat(List<String> dataList) {
        String nick = dataList.get(5);
        SoopPacket donePacket = packetMap.remove(nick);

        if (donePacket == null) {
            handleChat(
                    new User(dataList.get(1), nick),
                    dataList.get(0)
            );
            return;
        }

        int temp = Integer.parseInt(donePacket.getDataList().get(3));
        int amount = poong ? temp : temp * 100;

        handleDone(
                new User(donePacket.getDataList().get(1), nick),
                amount,
                dataList.get(0)
        );
    }

    private void handleDone(User user, int payAmount, String msg) {
        listener.onDonation(new Donate(liveInfo.BJID(), user, payAmount, msg));
    }

    private void handleChat(User user, String msg) {
        listener.onChat(new Chat(liveInfo.BJID(), user, msg));
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        debugger.log("숲 웹소켓 오류: " + t.getMessage());
        listener.onError(t, response);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        debugger.log("숲 웹소켓 연결 종료");
        isAlive = false;
    }

    public void kill() {
        isAlive = false;

        if (pingExecutor != null) {
            pingExecutor.shutdownNow();
            pingExecutor = null;
        }

        try {
            if (socket != null) socket.close(1000, "Killed");
        } catch (Exception ignored) {}

        try {
            if (client != null) client.dispatcher().executorService().shutdown();
        } catch (Exception ignored) {}

        debugger.log("[" + soopUser.get("nickname") + "] 숲 웹소켓 Kill 완료");
    }

    private static String makePacket(String command, String data) {
        return ESC + command + makeLengthPacket(data) + data;
    }

    private static String makeLengthPacket(String data) {
        return String.format("%06d00", data.length());
    }
}
