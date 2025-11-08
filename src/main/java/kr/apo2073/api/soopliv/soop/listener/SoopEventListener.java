package kr.apo2073.api.soopliv.soop.listener;

import kr.apo2073.api.soopliv.data.Chat;
import kr.apo2073.api.soopliv.data.Donate;

public class SoopEventListener {
    public void onChat(Chat chat) {}
    public void onDonation(Donate donate) {}
    public void onError(Exception e) {}
}
