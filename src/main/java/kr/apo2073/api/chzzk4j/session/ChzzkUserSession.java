package kr.apo2073.api.chzzk4j.session;

import kr.apo2073.api.chzzk4j.ChzzkClient;

public class ChzzkUserSession extends ChzzkSession {

    ChzzkUserSession(ChzzkClient chzzk, boolean autoRecreate) {
        super(chzzk, autoRecreate);

        sessionListUrl = "/open/v1/sessions";
        sessionCreateUrl = "/open/v1/sessions/auth";
        userSession = true;
    }
}
