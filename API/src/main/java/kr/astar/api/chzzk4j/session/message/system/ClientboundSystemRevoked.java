package kr.astar.api.chzzk4j.session.message.system;

import kr.astar.api.chzzk4j.session.ChzzkSessionSubscriptionType;
import lombok.Getter;

public class ClientboundSystemRevoked {
    private String eventType;
    @Getter
    private String channelId;

    public ChzzkSessionSubscriptionType getEventType() {
        return ChzzkSessionSubscriptionType.valueOf(eventType);
    }

    @Override
    public String toString() {
        return "ClientboundSystemRevoked{" +
                "eventType='" + eventType + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
