package kr.apo2073.api.chzzk4j.chat.event;

import lombok.Getter;

@Getter
public class ConnectionClosedEvent extends ChzzkEvent {
    private final int code;
    private final String reason;
    private final boolean remote;
    private final boolean shouldReconnect;

    public ConnectionClosedEvent(int code, String reason, boolean remote, boolean shouldReconnect) {
        this.code = code;
        this.reason = reason;
        this.remote = remote;
        this.shouldReconnect = shouldReconnect;
    }

}
