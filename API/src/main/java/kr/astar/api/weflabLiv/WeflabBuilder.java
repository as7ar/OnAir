package kr.astar.api.weflabLiv;

import kr.astar.api.weflabLiv.listener.WeflabListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WeflabBuilder {
    @Getter
    private final String key;
    @Getter
    private List<WeflabListener> listeners;

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
}
