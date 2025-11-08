package kr.apo2073.api.chzzk4j.chat.event;

import kr.apo2073.api.chzzk4j.chat.MissionDonationMessage;

public class MissionDonationEvent extends InternalChzzkMsgEvent<MissionDonationMessage> {
    public MissionDonationEvent(MissionDonationMessage msg) {
        super(msg);
    }
}
