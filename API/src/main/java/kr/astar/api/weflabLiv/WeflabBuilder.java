package kr.astar.api.weflabLiv;

import kr.astar.api.weflabLiv.listener.WeflabListener;

import java.util.ArrayList;
import java.util.List;

public class WeflabBuilder {
    private final String key;
    private final List<WeflabListener> listeners;

    public WeflabBuilder(String key) {
        this.key = key;
        listeners=new ArrayList<>();
    }

    public WeflabBuilder addListener(WeflabListener listener) {
        listeners.add(listener);
        return this;
    }

    public Weflab build() {
        return new Weflab(this);
    }

    public String getKey() {
        return key;
    }

    public List<WeflabListener> getListeners() {
        return listeners;
    }
}
