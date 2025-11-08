package kr.apo2073.api.chzzk4j.chat;

class WsMessageBase {
    public String cid;
    public String svcid = "game";
    public String ver = "3";
    /**
     * * Note: only used in serverbound messages
     */
    public int cmd = -1;

    public WsMessageBase(int cmd) {
        this.cmd = cmd;
    }

    public WsMessageBase() {
    }
}
