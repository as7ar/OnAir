package kr.astar.onair.api.toonLiv.listener;

import kr.astar.onair.api.toonLiv.data.Donation;

public interface ToonationEventListener {
//    default void onChat(Chatting chatting) {}
    default void onDonation(Donation donation) {}
    default void onConnect() {}
    default void onDisconnect() {}
    default void onFail() {}
}
