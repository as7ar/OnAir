package kr.apo2073.tnliv.utilities;

public class Streamer {
    private final String id;
    private final String nickname;
    public Streamer(String id, String nickname) {
        this.id=id;
        this.nickname=nickname;
    }

    public String getNickname() {
        return nickname;
    }
    public String getId() {
        return id;
    }
}
