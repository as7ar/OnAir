package kr.astar.onair.api.soopliv;

import kr.astar.onair.api.soopliv.soop.SoopApi;
import kr.astar.onair.api.soopliv.soop.SoopLiveInfo;
import kr.astar.onair.api.soopliv.soop.SoopWebSocket;
import kr.astar.onair.api.soopliv.soop.listener.SoopEventListener;
import lombok.Getter;

import java.util.Map;

public class SoopBuilder {

    @Getter
    private SoopEventListener listener;
    @Getter
    private String bjid;
    @Getter
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

    public SoopWebSocket build(boolean debug) {
        SoopLiveInfo liveInfo = SoopApi.getPlayerLive(bjid);

        String wsUri = "wss://" + liveInfo.CHDOMAIN() + ":" +
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
