package kr.apo2073.api.chzzk4j.exception;

public class NotLoggedInException extends Exception {
    public NotLoggedInException(String reason) {
        super(reason);
    }
}
