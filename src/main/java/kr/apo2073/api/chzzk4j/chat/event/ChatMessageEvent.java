package kr.apo2073.api.chzzk4j.chat.event;

import kr.apo2073.api.chzzk4j.chat.ChatMessage;

public class ChatMessageEvent extends InternalChzzkMsgEvent<ChatMessage> {
    public ChatMessageEvent(ChatMessage msg) {
        super(msg);
    }
}
