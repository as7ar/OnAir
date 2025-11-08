package kr.apo2073.api.chzzk4j.types.channel;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ChzzkChannelManager {
    public enum Role {
        STREAMING_CHANNEL_OWNER,
        STREAMING_CHANNEL_MANAGER,
        STREAMING_CHAT_MANAGER,
        STREAMING_SETTLEMENT_MANAGER
    }

    private String managerChannelId;
    private String managerChannelName;
    private Role role;
    private String createdDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkChannelManager that = (ChzzkChannelManager) o;
        return Objects.equals(managerChannelId, that.managerChannelId)
                && Objects.equals(managerChannelName, that.managerChannelName)
                && role == that.role
                && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerChannelId, managerChannelName, role, createdDate);
    }

    @Override
    public String toString() {
        return "ChzzkChannelManager{" +
                "managerChannelId='" + managerChannelId + '\'' +
                ", managerChannelName='" + managerChannelName + '\'' +
                ", role=" + role +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
