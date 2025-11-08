package kr.apo2073.api.chzzk4j.chat;

public class DonationMessage extends ChatMessage {
    public int getPayAmount() {
        return extras.payAmount;
    }
}
