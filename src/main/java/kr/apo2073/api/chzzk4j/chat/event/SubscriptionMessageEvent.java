package kr.apo2073.api.chzzk4j.chat.event;

import kr.apo2073.api.chzzk4j.chat.SubscriptionMessage;

public class SubscriptionMessageEvent extends InternalChzzkMsgEvent<SubscriptionMessage> {
    public SubscriptionMessageEvent(SubscriptionMessage msg) {
        super(msg);
    }
}
