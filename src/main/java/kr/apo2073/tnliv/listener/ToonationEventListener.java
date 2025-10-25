package kr.apo2073.tnliv.listener;

import kr.apo2073.tnliv.data.Donation;

public interface ToonationEventListener {
//    default void onChat(Chatting chatting) {}
    default void onDonation(Donation donation) {}
    default void onConnect() {}
    default void onDisconnect() {}
    default void onFail() {}
}
