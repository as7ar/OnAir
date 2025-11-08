package kr.apo2073.api.chzzk4j.types.channel;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public class ChzzkChannelSubscriberResponse {
    @Getter
    private int page;
    @Getter
    private int totalCount;
    @Getter
    private int totalPages;
    private ChzzkChannelSubscriber[] data;

    public ChzzkChannelSubscriber[] getFollowers() {
        return data;
    }

    @Override
    public String toString() {
        return "ChzzkChannelSubscriberResponse{" +
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
        ChzzkChannelSubscriberResponse that = (ChzzkChannelSubscriberResponse) o;
        return page == that.page && totalCount == that.totalCount && totalPages == that.totalPages
                && Objects.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, totalCount, totalPages, Arrays.hashCode(data));
    }
}
