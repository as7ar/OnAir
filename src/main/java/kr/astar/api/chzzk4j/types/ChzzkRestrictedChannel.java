package kr.astar.api.chzzk4j.types;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ChzzkRestrictedChannel {
    private String restrictedChannelId;
    private String restrictedChannelName;
    private String createdDate;
    private String releaseDate;

    @Override
    public String toString() {
        return "ChzzkRestrictedChannel{" +
                "restrictedChannelId='" + restrictedChannelId + '\'' +
                ", restrictedChannelName='" + restrictedChannelName + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkRestrictedChannel that = (ChzzkRestrictedChannel) o;
        return Objects.equals(restrictedChannelId, that.restrictedChannelId)
                && Objects.equals(restrictedChannelName, that.restrictedChannelName)
                && Objects.equals(createdDate, that.createdDate)
                && Objects.equals(releaseDate, that.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restrictedChannelId, restrictedChannelName, createdDate, releaseDate);
    }
}
