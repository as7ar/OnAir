package kr.apo2073.api.chzzk4j.types.channel;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ChzzkChannelPersonalData {
    /**
     * -- GETTER --
     *  Get following status of the logged user about the channel.
     */
    private ChzzkChannelFollowingData following;
    private boolean privateUserBlock;

    private ChzzkChannelPersonalData() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkChannelPersonalData that = (ChzzkChannelPersonalData) o;
        return privateUserBlock == that.privateUserBlock && Objects.equals(following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(following, privateUserBlock);
    }

    @Override
    public String toString() {
        return "ChzzkChannelPersonalData{" +
                "following=" + following +
                ", privateUserBlock=" + privateUserBlock +
                '}';
    }
}
