package kr.astar.onair.api.toonLiv;

import kr.astar.onair.api.toonLiv.listener.ToonationEventListener;

import java.util.ArrayList;
import java.util.List;

public class ToonationBuilder {
    public String key;
    public boolean timeout=true;
    public List<ToonationEventListener> listeners=new ArrayList<>();
    public boolean debug=false;

    public ToonationBuilder setKey(String key) {
        this.key =key;
        return this;
    }
    public ToonationBuilder setTimeout(boolean timeout) {
        this.timeout=timeout;
        return this;
    }

    public ToonationBuilder setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public ToonationBuilder addListener(ToonationEventListener listener) {
        listeners.add(listener);
        return this;
    }

    public Toonation build() {
        if (key == null) return null;
        return new Toonation(this);
    }
}
