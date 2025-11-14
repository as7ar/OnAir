package kr.astar.api.chzzk4j.exception;

public class NotLoggedInException extends Exception {
    public NotLoggedInException(String reason) {
        super(reason);
    }
}
