package kr.apo2073.api.chzzk4j.session.event;

import kr.apo2073.api.chzzk4j.session.ChzzkSessionSubscriptionType;
import lombok.Getter;

@Getter
public class SessionSubscribedEvent extends SessionEvent {
    private final ChzzkSessionSubscriptionType eventType;
    private final String channelId;

    public SessionSubscribedEvent(ChzzkSessionSubscriptionType eventType, String channelId) {
        this.eventType = eventType;
        this.channelId = channelId;
    }

}
