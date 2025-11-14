package kr.astar.api.utubeLiv.listener;

import kr.astar.api.utubeLiv.data.Chatting;
import kr.astar.api.utubeLiv.data.SuperChat;
import kr.astar.api.utubeLiv.data.SuperSticker;

public interface YouTubeEventListener {
    default void onChat(Chatting chat) {}
    default void onSuperChat(SuperChat superChat) {}
    default void onSuperSticker(SuperSticker superSticker) {}
    default void onError(Exception e) {}
}
