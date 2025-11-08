package kr.apo2073.api.chzzk4j.chat;

class WsMessageServerboundSendChat extends WsMessageBase {
    static class Body {
        static class Extras {
            String chatType = "STREAMING";
            String osType = "PC";
            String streamingChannelId = "";
            String emojis = "";
        }

        String extras;
        String msg;
        long msgTime = System.currentTimeMillis();
        int msgTypeCode = WsMessageTypes.ChatTypes.TEXT;
    }

    public WsMessageServerboundSendChat() {
        super(WsMessageTypes.Commands.SEND_CHAT);
    }

    Body bdy = new Body();

    int tid = 3;
    String sid;
}
