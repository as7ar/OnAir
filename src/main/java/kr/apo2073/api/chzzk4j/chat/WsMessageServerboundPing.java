package kr.apo2073.api.chzzk4j.chat;

class WsMessageServerboundPing {
    public int cmd = WsMessageTypes.Commands.PING;
    public String ver = "3";
}