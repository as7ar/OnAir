package kr.apo2073.api.chzzk4j.chat.event;

import kr.apo2073.api.chzzk4j.chat.ChatMessage;

class InternalChzzkMsgEvent<T extends ChatMessage> extends ChzzkEvent {
    private final T msg;

    public InternalChzzkMsgEvent(T msg) {
        this.msg = msg;
    }

    public T getMessage() {
        return msg;
    }
}
