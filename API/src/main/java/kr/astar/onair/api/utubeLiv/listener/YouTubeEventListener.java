package kr.astar.onair.api.utubeLiv.listener;

import kr.astar.onair.api.utubeLiv.data.Chatting;
import kr.astar.onair.api.utubeLiv.data.SuperChat;
import kr.astar.onair.api.utubeLiv.data.SuperSticker;

public interface YouTubeEventListener {
    default void onChat(Chatting chat) {}
    default void onSuperChat(SuperChat superChat) {}
    default void onSuperSticker(SuperSticker superSticker) {}
    default void onError(Exception e) {}
}
