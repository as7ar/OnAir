package kr.astar.onair.api.weflabLiv;

import kr.astar.onair.api.weflabLiv.listener.WeflabListener;

import java.util.ArrayList;
import java.util.List;

public class WeflabBuilder {
    private final String key;
    private final List<WeflabListener> listeners;

    // Getter 에러남..
    public String getKey() {
        return this.key;
    }

    public List<WeflabListener> getListeners() {
        return this.listeners;
    }

    public WeflabBuilder(String key) {
        this.key = key;
        listeners = new ArrayList<>();
    }

    public WeflabBuilder addListener(WeflabListener listener) {
        listeners.add(listener);
        return this;
    }

    public Weflab build() {
        return new Weflab(this);
    }
}
