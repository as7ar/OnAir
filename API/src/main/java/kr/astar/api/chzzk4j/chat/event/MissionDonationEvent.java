package kr.astar.api.chzzk4j.chat.event;

import kr.astar.api.chzzk4j.chat.MissionDonationMessage;

public class MissionDonationEvent extends InternalChzzkMsgEvent<MissionDonationMessage> {
    public MissionDonationEvent(MissionDonationMessage msg) {
        super(msg);
    }
}
