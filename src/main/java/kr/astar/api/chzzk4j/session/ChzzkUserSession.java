package kr.astar.api.chzzk4j.session;

import kr.astar.api.chzzk4j.ChzzkClient;

public class ChzzkUserSession extends ChzzkSession {

    ChzzkUserSession(ChzzkClient chzzk, boolean autoRecreate) {
        super(chzzk, autoRecreate);

        sessionListUrl = "/open/v1/sessions";
        sessionCreateUrl = "/open/v1/sessions/auth";
        userSession = true;
    }
}
