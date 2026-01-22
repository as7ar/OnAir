package kr.astar.api.weflabLiv.listener;

import kr.astar.api.weflabLiv.data.alert.Donation;
import lombok.NonNull;
import okhttp3.Response;

public interface WeflabListener {
//    default void onChat(Chat chat) {}
    default void onDonation(Donation donation) {}
    default void onConnect(@NonNull Response response) {}
    default void onDisconnect(int code, String reason) {}
    default void onFail(Throwable t, Response response) {}
}
