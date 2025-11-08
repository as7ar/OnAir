package kr.apo2073.api.chzzk4j.exception;

public class NoAccessTokenOnlySupported extends Exception {
    public NoAccessTokenOnlySupported(String reason) {
        super(reason);
    }
}
