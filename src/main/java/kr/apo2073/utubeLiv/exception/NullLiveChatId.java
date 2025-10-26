package kr.apo2073.utubeLiv.exception;

public class NullLiveChatId extends IllegalStateException {
    public NullLiveChatId() {
        super("Live chat ID not found");
    }
}
