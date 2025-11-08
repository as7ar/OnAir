package kr.apo2073.api.chzzk4j.session.event;

import kr.apo2073.api.chzzk4j.session.message.SessionChatMessage;
import lombok.Getter;

@Getter
public class SessionChatMessageEvent extends SessionEvent {
    private final SessionChatMessage message;

    public SessionChatMessageEvent(SessionChatMessage message) {
        this.message = message;
    }

}
