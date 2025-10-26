package kr.apo2073.utubeLiv.listener;

import kr.apo2073.utubeLiv.data.Chatting;
import kr.apo2073.utubeLiv.data.SuperChat;
import kr.apo2073.utubeLiv.data.SuperSticker;

public interface YouTubeEventListener {
    default void onChat(Chatting chat) {}
    default void onSuperChat(SuperChat superChat) {}
    default void onSuperSticker(SuperSticker superSticker) {}
    default void onError(Exception e) {}
}
