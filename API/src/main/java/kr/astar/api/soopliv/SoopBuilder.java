package kr.astar.api.soopliv;

import kr.astar.api.soopliv.soop.SoopApi;
import kr.astar.api.soopliv.soop.SoopLiveInfo;
import kr.astar.api.soopliv.soop.SoopWebSocket;
import kr.astar.api.soopliv.soop.listener.SoopEventListener;

import java.util.Map;

public class SoopBuilder {

    private SoopEventListener listener;
    private String bjid;
    private boolean balloon;
    private Map<String, String> user;

    public SoopBuilder setID(String id) {
        this.bjid = id;
        return this;
    }

    public SoopBuilder setListener(SoopEventListener listener) {
        this.listener = listener;
        return this;
    }

    public SoopBuilder enableBalloon(boolean enable) {
        this.balloon = enable;
        return this;
    }

    public SoopBuilder setUser(Map<String, String> user) {
        this.user = user;
        return this;
    }

    // ===== getters (Lombok 제거) =====

    public SoopEventListener getListener() {
        return listener;
    }

    public String getBjid() {
        return bjid;
    }

    public boolean isBalloon() {
        return balloon;
    }

    // ================================

    public SoopWebSocket build(boolean debug) {
        SoopLiveInfo liveInfo = SoopApi.getPlayerLive(bjid);

        String wsUri =
                "wss://" + liveInfo.CHDOMAIN() + ":" +
                        liveInfo.CHPT() + "/Websocket/" + liveInfo.BJID();

        return new SoopWebSocket(
                wsUri,
                liveInfo,
                user,
                balloon,
                debug,
                listener
        );
    }
}
