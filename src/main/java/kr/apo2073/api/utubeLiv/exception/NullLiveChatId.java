package kr.apo2073.api.utubeLiv.exception;

public class NullLiveChatId extends IllegalStateException {
    public NullLiveChatId() {
        super("Live chat ID not found");
    }
}
