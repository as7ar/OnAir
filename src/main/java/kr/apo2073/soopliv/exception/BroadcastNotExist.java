package kr.apo2073.soopliv.exception;

public class BroadcastNotExist extends RuntimeException {
    public BroadcastNotExist() {
        super("Broadcast not exist");
    }
}
