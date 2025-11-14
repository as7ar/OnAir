package kr.astar.api.chzzk4j.types.channel.live;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class ChzzkLiveCategory {
    @Getter
    public static class SearchResponse {
        private ChzzkLiveCategory[] data;

    }

    public enum Type {
        GAME,
        SPORTS,
        ETC
    }

    private String categoryType;
    @Setter
    @Getter
    private String categoryId;
    @Getter
    private String categoryValue;
    @Getter
    private String posterImageUrl;

    public Type getCategoryType() {
        return Type.valueOf(categoryType);
    }

    public void setCategoryType(Type categoryType) {
        this.categoryType = categoryType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkLiveCategory that = (ChzzkLiveCategory) o;
        return Objects.equals(categoryType, that.categoryType)
                && Objects.equals(categoryId, that.categoryId)
                && Objects.equals(categoryValue, that.categoryValue)
                && Objects.equals(posterImageUrl, that.posterImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryType, categoryId, categoryValue, posterImageUrl);
    }

    @Override
    public String toString() {
        return "ChzzkLiveCategory{" +
                "categoryType=" + categoryType +
                ", categoryId='" + categoryId + '\'' +
                ", categoryValue='" + categoryValue + '\'' +
                ", posterImageUrl='" + posterImageUrl + '\'' +
                '}';
    }
}
