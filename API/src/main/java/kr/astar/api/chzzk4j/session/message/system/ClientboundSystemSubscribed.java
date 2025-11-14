package kr.astar.api.chzzk4j.session.message.system;

import kr.astar.api.chzzk4j.session.ChzzkSessionSubscriptionType;
import lombok.Getter;

public class ClientboundSystemSubscribed {
    private String eventType;
    @Getter
    private String channelId;

    public ChzzkSessionSubscriptionType getEventType() {
        return ChzzkSessionSubscriptionType.valueOf(eventType);
    }

    @Override
    public String toString() {
        return "ClientboundSystemSubscribed{" +
                "eventType='" + eventType + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
