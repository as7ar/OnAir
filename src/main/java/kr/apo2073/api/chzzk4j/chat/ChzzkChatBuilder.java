package kr.apo2073.api.chzzk4j.chat;

import kr.apo2073.api.chzzk4j.ChzzkClient;

import java.io.IOException;

public class ChzzkChatBuilder {

    private final String channelId;
    private final ChzzkClient chzzk;
    private boolean autoReconnect = true;

    public ChzzkChatBuilder(ChzzkClient chzzk, String channelId) {
        this.chzzk = chzzk;
        this.channelId = channelId;
    }

    public ChzzkChatBuilder withAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;

        return this;
    }

    public ChzzkChat build() throws IOException {
        return new ChzzkChat(chzzk, channelId, autoReconnect);
    }

}
