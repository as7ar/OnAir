package kr.apo2073.api.chzzk4j.session.message;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SessionNewSubscriberMessage {
    private String channelId;
    private String subscriberChannelId;
    private String subscriberNickname;
    private int tierNo;
    private String tierName;
    private int month;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionNewSubscriberMessage that = (SessionNewSubscriberMessage) o;
        return tierNo == that.tierNo && month == that.month && Objects.equals(channelId, that.channelId) && Objects.equals(subscriberChannelId, that.subscriberChannelId) && Objects.equals(subscriberNickname, that.subscriberNickname) && Objects.equals(tierName, that.tierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, subscriberChannelId, subscriberNickname, tierNo, tierName, month);
    }

    @Override
    public String toString() {
        return "SessionSubscriptionMessage{" +
                "channelId='" + channelId + '\'' +
                ", subscriberChannelId='" + subscriberChannelId + '\'' +
                ", subscriberNickname='" + subscriberNickname + '\'' +
                ", tierNo=" + tierNo +
                ", tierName='" + tierName + '\'' +
                ", month=" + month +
                '}';
    }
}
