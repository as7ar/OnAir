package kr.astar.api.chzzk4j.session.message;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class SessionDonationMessage {
    public enum DonationType {
        CHAT,
        VIDEO
    }

    private String donationType;
    private String channelId;
    @Getter
    private String donatorChannelId;
    @Getter
    private String donatorNickname;
    @Getter
    private int payAmount;
    @Getter
    private String donationText;
    @Getter
    public Map<String, String> emojis;

    public DonationType getDonationType() {
        return DonationType.valueOf(donationType);
    }

    public String getReceivedChannelId() {
        return channelId;
    }

    @Nullable
    public String getEmojiImgUrl(String emojiId) {
        return emojis.get(emojiId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDonationMessage that = (SessionDonationMessage) o;
        return payAmount == that.payAmount && Objects.equals(donationType, that.donationType) && Objects.equals(channelId, that.channelId) && Objects.equals(donatorChannelId, that.donatorChannelId) && Objects.equals(donatorNickname, that.donatorNickname) && Objects.equals(donationText, that.donationText) && Objects.equals(emojis, that.emojis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donationType, channelId, donatorChannelId, donatorNickname, payAmount, donationText, emojis);
    }

    @Override
    public String toString() {
        return "SessionDonationMessage{" +
                "donationType='" + donationType + '\'' +
                ", channelId='" + channelId + '\'' +
                ", donatorChannelId='" + donatorChannelId + '\'' +
                ", donatorNickname='" + donatorNickname + '\'' +
                ", payAmount=" + payAmount +
                ", donationText='" + donationText + '\'' +
                ", emojis=" + emojis +
                '}';
    }
}
