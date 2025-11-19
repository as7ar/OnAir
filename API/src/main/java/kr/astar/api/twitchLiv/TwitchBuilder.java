package kr.astar.api.twitchLiv;

import kr.astar.api.twitchLiv.listener.TwitchEventListener;

import java.util.ArrayList;
import java.util.List;

public class TwitchBuilder {
    public String TOKEN;
    public boolean enableChat = true;
    public boolean enableHelix = true;
    public boolean enablePubsub = true;
    public String id;
    private String clientId;
    private String clientSecret;

    public List<TwitchEventListener> listeners = new ArrayList<>();

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public boolean isEnableChat() {
        return enableChat;
    }

    public void setEnableChat(boolean enableChat) {
        this.enableChat = enableChat;
    }

    public boolean isEnableHelix() {
        return enableHelix;
    }

    public void setEnableHelix(boolean enableHelix) {
        this.enableHelix = enableHelix;
    }

    public boolean isEnablePubsub() {
        return enablePubsub;
    }

    public void setEnablePubsub(boolean enablePubsub) {
        this.enablePubsub = enablePubsub;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public TwitchBuilder addListener(TwitchEventListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public Twitch build() {
        return new Twitch(this);
    }
}
