package kr.astar.api.chzzk4j.chat.event;

import lombok.Getter;

@Getter
public class ErrorEvent extends ChzzkEvent {
    private final Exception exception;

    public ErrorEvent(Exception exception) {
        this.exception = exception;
    }

}
