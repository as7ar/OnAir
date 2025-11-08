package kr.apo2073.api.chzzk4j.types;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class ChzzkChatSettings {
    public enum ChatAvailableCondition {
        NONE,
        REAL_NAME
    }

    public enum ChatAvailableGroup {
        ALL,
        FOLLOWER,
        MANAGER,
        SUBSCRIBER
    }

    public enum MinFollowerMinute {
        M_0,
        M_5,
        M_10,
        M_30,
        M_60,
        M_1440,
        M_10080,
        M_43200
    }

    public enum ChatSlowModeSec {
        S_0,
        S_3,
        S_5,
        S_10,
        S_30,
        S_60,
        S_120,
        S_300
    }

    private String chatAvailableCondition;
    private String chatAvailableGroup;
    private int minFollowerMinute;
    @Setter
    @Getter
    private boolean allowSubscriberInFollowerMode;
    @Getter
    private int chatSlowModeSec;
    @Setter
    @Getter
    private boolean chatEmojiMode;

    public ChatAvailableCondition getChatAvailableCondition() {
        return ChatAvailableCondition.valueOf(chatAvailableCondition);
    }

    public ChatAvailableGroup getChatAvailableGroup() {
        return ChatAvailableGroup.valueOf(chatAvailableGroup);
    }

    public MinFollowerMinute getMinFollowerMinute() {
        return MinFollowerMinute.valueOf("M_" + minFollowerMinute);
    }

    public void setChatAvailableCondition(ChatAvailableCondition chatAvailableCondition) {
        this.chatAvailableCondition = chatAvailableCondition.toString();
    }

    public void setChatAvailableGroup(ChatAvailableGroup chatAvailableGroup) {
        this.chatAvailableGroup = chatAvailableGroup.toString();
    }

    public void setMinFollowerMinute(MinFollowerMinute minFollowerMinute) {
        this.minFollowerMinute = Integer.parseInt(minFollowerMinute.toString().replace("M_", ""));
    }

    public void setChatSlowModeSec(ChatSlowModeSec sec) {
        this.chatSlowModeSec = Integer.parseInt(sec.toString().replace("S_", ""));
    }

    @Override
    public String toString() {
        return "ChzzkChatSettings{" +
                "chatAvailableCondition='" + chatAvailableCondition + '\'' +
                ", chatAvailableGroup='" + chatAvailableGroup + '\'' +
                ", minFollowerMinute=" + minFollowerMinute +
                ", allowSubscriberInFollowerMode=" + allowSubscriberInFollowerMode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkChatSettings that = (ChzzkChatSettings) o;
        return minFollowerMinute == that.minFollowerMinute
                && allowSubscriberInFollowerMode == that.allowSubscriberInFollowerMode
                && Objects.equals(chatAvailableCondition, that.chatAvailableCondition)
                && Objects.equals(chatAvailableGroup, that.chatAvailableGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatAvailableCondition, chatAvailableGroup, minFollowerMinute, allowSubscriberInFollowerMode);
    }
}
