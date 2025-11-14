package kr.astar.api.chzzk4j.chat.event;

import kr.astar.api.chzzk4j.chat.ChatMessage;

public class ChatMessageEvent extends InternalChzzkMsgEvent<ChatMessage> {
    public ChatMessageEvent(ChatMessage msg) {
        super(msg);
    }
}
