package kr.astar.api.soopliv.soop.listener;

import kr.astar.api.soopliv.data.Chat;
import kr.astar.api.soopliv.data.Donate;
import okhttp3.Response;

public class SoopEventListener {
    public void onChat(Chat chat) {}
    public void onDonation(Donate donate) {}
    public void onError(Throwable t, Response response) {}
}
