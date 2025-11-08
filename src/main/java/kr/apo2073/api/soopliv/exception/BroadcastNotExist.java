package kr.apo2073.api.soopliv.exception;

public class BroadcastNotExist extends RuntimeException {
    public BroadcastNotExist() {
        super("Broadcast not exist");
    }
}
