package kr.astar.api.chzzk4j.chat.event;

import kr.astar.api.chzzk4j.chat.ChzzkChat;
import lombok.Getter;

@Getter
public class ConnectEvent extends ChzzkEvent {
    private final ChzzkChat chat;
    private final boolean reconnecting;

    public ConnectEvent(ChzzkChat chat, boolean reconnecting) {
        this.chat = chat;
        this.reconnecting = reconnecting;
    }

}
