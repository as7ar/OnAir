package kr.astar.api.chzzk4j.session;

import kr.astar.api.chzzk4j.ChzzkClient;

public class ChzzkClientSession extends ChzzkSession {

    ChzzkClientSession(ChzzkClient chzzk, boolean autoRecreate) {
        super(chzzk, autoRecreate);

        sessionListUrl = "/open/v1/sessions/client";
        sessionCreateUrl = "/open/v1/sessions/auth/client";
    }
}
