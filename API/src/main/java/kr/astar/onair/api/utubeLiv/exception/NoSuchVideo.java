package kr.astar.onair.api.utubeLiv.exception;

public class NoSuchVideo extends RuntimeException {
    public NoSuchVideo() {
        super("해당 비디오가 존재하지 않습니다");
    }
}
