package kr.apo2073.api.chzzk4j.auth;

import kr.apo2073.api.chzzk4j.ChzzkClient;

import java.util.concurrent.CompletableFuture;

public interface ChzzkLoginAdapter {
    CompletableFuture<ChzzkLoginResult> authorize(ChzzkClient client);
}
