package kr.apo2073.api.utubeLiv.exception;

public class InvalidApiKeyException extends RuntimeException {
    public InvalidApiKeyException() {
        super("Invalid API Key");
    }
}
