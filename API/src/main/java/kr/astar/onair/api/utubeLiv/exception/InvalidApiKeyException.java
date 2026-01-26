package kr.astar.onair.api.utubeLiv.exception;

public class InvalidApiKeyException extends RuntimeException {
    public InvalidApiKeyException() {
        super("Invalid API Key");
    }
}
