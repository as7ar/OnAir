package kr.apo2073.api.chzzk4j.chat.event;

import kr.apo2073.api.chzzk4j.chat.DonationMessage;

public class NormalDonationEvent extends InternalChzzkMsgEvent<DonationMessage> {
    public NormalDonationEvent(DonationMessage msg) {
        super(msg);
    }
}
