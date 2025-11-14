package kr.astar.api.chzzk4j.session.message.system;

import lombok.Getter;

@Getter
public class ClientboundSystemConnected {
    private String sessionKey;

    @Override
    public String toString() {
        return "ClientboundSystemConnected{" +
                "sessionKey='" + sessionKey + '\'' +
                '}';
    }
}
