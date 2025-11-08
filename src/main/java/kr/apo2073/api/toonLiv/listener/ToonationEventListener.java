package kr.apo2073.api.toonLiv.listener;

import kr.apo2073.api.toonLiv.data.Donation;

public interface ToonationEventListener {
//    default void onChat(Chatting chatting) {}
    default void onDonation(Donation donation) {}
    default void onConnect() {}
    default void onDisconnect() {}
    default void onFail() {}
}
