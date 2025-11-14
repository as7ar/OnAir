package kr.astar.api.chzzk4j.chat.event;

import kr.astar.api.chzzk4j.chat.SubscriptionMessage;

public class SubscriptionMessageEvent extends InternalChzzkMsgEvent<SubscriptionMessage> {
    public SubscriptionMessageEvent(SubscriptionMessage msg) {
        super(msg);
    }
}
