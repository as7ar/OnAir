package kr.astar.api.chzzk4j.chat.event;

import kr.astar.api.chzzk4j.chat.DonationMessage;

public class NormalDonationEvent extends InternalChzzkMsgEvent<DonationMessage> {
    public NormalDonationEvent(DonationMessage msg) {
        super(msg);
    }
}
