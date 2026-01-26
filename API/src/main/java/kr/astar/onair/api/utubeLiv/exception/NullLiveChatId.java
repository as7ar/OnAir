package kr.astar.onair.api.utubeLiv.exception;

public class NullLiveChatId extends IllegalStateException {
    public NullLiveChatId() {
        super("Live chat ID not found");
    }
}
