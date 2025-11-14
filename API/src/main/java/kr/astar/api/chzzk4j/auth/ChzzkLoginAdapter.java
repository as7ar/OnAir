package kr.astar.api.chzzk4j.auth;

import kr.astar.api.chzzk4j.ChzzkClient;

import java.util.concurrent.CompletableFuture;

public interface ChzzkLoginAdapter {
    CompletableFuture<ChzzkLoginResult> authorize(ChzzkClient client);
}
