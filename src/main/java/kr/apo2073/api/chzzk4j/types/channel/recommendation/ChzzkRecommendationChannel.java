package kr.apo2073.api.chzzk4j.types.channel.recommendation;

import kr.apo2073.api.chzzk4j.types.channel.ChzzkPartialChannel;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ChzzkRecommendationChannel {
    private ChzzkPartialChannel channel;

    @Override
    public String toString() {
        return "ChzzkRecommendationChannel{" +
                "channel=" + channel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkRecommendationChannel that = (ChzzkRecommendationChannel) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel);
    }
}
