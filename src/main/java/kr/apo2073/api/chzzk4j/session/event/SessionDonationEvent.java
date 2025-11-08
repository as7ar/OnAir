package kr.apo2073.api.chzzk4j.session.event;

import kr.apo2073.api.chzzk4j.session.message.SessionDonationMessage;
import lombok.Getter;

@Getter
public class SessionDonationEvent extends SessionEvent {
    private final SessionDonationMessage message;

    public SessionDonationEvent(SessionDonationMessage message) {
        this.message = message;
    }

}
