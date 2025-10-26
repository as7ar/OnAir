package kr.apo2073.soopliv;

import kr.apo2073.soopliv.soop.SoopApi;
import kr.apo2073.soopliv.soop.SoopLiveInfo;
import kr.apo2073.soopliv.soop.SoopWebSocket;
import kr.apo2073.soopliv.soop.listener.SoopEventListener;
import lombok.Getter;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.Protocol;

import java.util.Collections;
import java.util.List;
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
        this.bjid= id;
        return this;
    }

    public SoopBuilder setListener(SoopEventListener listener) {
        this.listener= listener;
        return this;
    }

    public SoopBuilder enableBalloon(boolean enable) {
        this.balloon= enable;
        return this;
    }

    public SoopBuilder setUser(Map<String, String> user) {
        this.user= user;
        return this;
    }

    public SoopWebSocket build(boolean debug) {
        SoopLiveInfo liveInfo= SoopApi.getPlayerLive(bjid);
        String wsUri="wss://"+liveInfo.CHDOMAIN()+":"+liveInfo.CHPT()+"/Websocket/"+liveInfo.BJID();

        return new SoopWebSocket(
                wsUri,
                new Draft_6455(
                        Collections.emptyList(),
                        List.of(new Protocol("chat"))
                ), liveInfo,
                user, balloon, debug,
                listener
        );
    }
}
