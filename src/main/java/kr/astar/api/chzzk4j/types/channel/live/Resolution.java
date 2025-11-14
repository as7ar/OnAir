package kr.astar.api.chzzk4j.types.channel.live;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum Resolution {

    R_1080(1080),
    R_720(720),
    R_480(480),
    R_360(360),
    R_270(270),
    R_144(144);

    private final int raw;

    Resolution(int raw) {
        this.raw = raw;
    }

    public @NotNull String getRawAsString() {
        return Integer.toString(raw);
    }

}
