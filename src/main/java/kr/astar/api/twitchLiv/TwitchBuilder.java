package kr.astar.api.twitchLiv;

import kr.astar.api.twitchLiv.listener.TwitchEventListener;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TwitchBuilder {
    @Setter
    @Getter
    public String TOKEN;

    @Setter
    @Getter
    public boolean enableChat=true;

    @Setter
    @Getter
    public boolean enableHelix=true;

    @Setter
    @Getter
    public boolean enablePubsub=true;

    public List<TwitchEventListener> listeners=new ArrayList<>();

    @Setter
    @Getter
    public String id;

    @Setter
    @Getter
    public String clientId;

    @Setter
    @Getter
    public String clientSecret;

    public TwitchBuilder addListener(TwitchEventListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public Twitch build() {
        return new Twitch(this);
    }
}
