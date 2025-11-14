package kr.astar.api.chzzk4j.types.channel.live;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ChzzkLiveSettings {
    public static class ModifyRequest {
        private final String defaultLiveTitle;
        private final String categoryType;
        private final String categoryId;
        private final String[] tags;

        public ModifyRequest(ChzzkLiveSettings settings) {
            this.defaultLiveTitle = settings.defaultLiveTitle;
            this.categoryId = settings.category.getCategoryId();
            this.categoryType = settings.category.getCategoryType().toString();
            this.tags = settings.tags.toArray(new String[0]);
        }
    }

    @Setter
    private String defaultLiveTitle;
    @Setter
    private ChzzkLiveCategory category;
    private final List<String> tags = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkLiveSettings that = (ChzzkLiveSettings) o;
        return Objects.equals(defaultLiveTitle, that.defaultLiveTitle) && Objects.equals(category, that.category) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultLiveTitle, category, tags);
    }

    @Override
    public String toString() {
        return "ChzzkLiveSettings{" +
                "defaultLiveTitle='" + defaultLiveTitle + '\'' +
                ", category=" + category +
                ", tags=" + tags +
                '}';
    }
}
