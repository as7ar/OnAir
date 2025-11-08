package kr.apo2073.api.chzzk4j.session.event;

import kr.apo2073.api.chzzk4j.session.message.SessionNewSubscriberMessage;
import lombok.Getter;

@Getter
public class SessionNewSubscriberEvent extends SessionEvent {
    private final SessionNewSubscriberMessage message;

    public SessionNewSubscriberEvent(SessionNewSubscriberMessage message) {
        this.message = message;
    }

}
