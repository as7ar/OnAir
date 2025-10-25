package kr.apo2073.tnliv;

import kr.apo2073.tnliv.listener.ToonationEventListener;

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

    public kr.apo2073.tnliv.Toonation build() {
        if (key == null) return null;
        return new Toonation(this);
    }
}
