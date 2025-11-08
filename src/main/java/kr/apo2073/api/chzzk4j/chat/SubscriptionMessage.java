package kr.apo2073.api.chzzk4j.chat;

public class SubscriptionMessage extends ChatMessage {
    public int getSubscriptionMonth() {
        return extras.month;
    }

    public String getSubscriptionTierName() {
        return extras.tierName;
    }
}
