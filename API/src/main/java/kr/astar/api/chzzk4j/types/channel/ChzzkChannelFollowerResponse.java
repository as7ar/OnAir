package kr.astar.api.chzzk4j.types.channel;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public class ChzzkChannelFollowerResponse {
    @Getter
    private int page;
    @Getter
    private int totalCount;
    @Getter
    private int totalPages;
    private ChzzkChannelFollower[] data;

    public ChzzkChannelFollower[] getFollowers() {
        return data;
    }

    @Override
    public String toString() {
        return "ChzzkChannelFollowerResponse{" +
                "page=" + page +
                ", totalCount=" + totalCount +
                ", totalPages=" + totalPages +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkChannelFollowerResponse that = (ChzzkChannelFollowerResponse) o;
        return page == that.page && totalCount == that.totalCount && totalPages == that.totalPages && Objects.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, totalCount, totalPages, Arrays.hashCode(data));
    }
}
