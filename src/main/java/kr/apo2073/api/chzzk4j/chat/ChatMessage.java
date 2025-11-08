package kr.apo2073.api.chzzk4j.chat;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class ChatMessage {
    public enum OsType
    {
        PC,
        AOS,
        IOS
    }

    public static class Extras {

        String donationType;
        String osType;

        @Getter
        int payAmount = -1;

        // Subscription
        int month = 0;
        String tierName = "";

        // Mission
        int durationTime;
        String missionDonationId;
        String missionCreatedTime;
        String missionEndTime;
        String missionText;
        String status;
        boolean success;

        public OsType getOsType() {
            return OsType.valueOf(osType);
        }

        @Override
        public String toString() {
            return "Extras{" +
                    "osType='" + osType + '\'' +
                    ", payAmount=" + payAmount + '\'' +
                    ", month=" + month + '\'' +
                    ", tierName='" + tierName +
                    '}';
        }
    }

    public static class Profile {
        @Getter
        String nickname;
        @Getter
        String profileImageUrl;
        @Getter
        String userRoleCode;
        @Getter
        boolean verifiedMark;

        @Getter
        ActivityBadge[] activityBadges;
        StreamingProperty streamingProperty;

        public static class StreamingProperty {
            Subscription subscription;

            @Getter
            public static class Subscription {
                int accmulativeMonth;
                int tier;
                PartialBadge badge;

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Subscription that = (Subscription) o;
                    return accmulativeMonth == that.accmulativeMonth
                            && tier == that.tier
                            && Objects.equals(badge, that.badge);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(accmulativeMonth, tier, badge);
                }

                @Override
                public String toString() {
                    return "Subscription{" +
                            "accmulativeMonth=" + accmulativeMonth +
                            ", tier=" + tier +
                            ", badge=" + badge +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "StreamingProperty{" +
                        "subscription=" + subscription +
                        '}';
            }
        }

        @Getter
        public static class PartialBadge {
            String imageUrl;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PartialBadge that = (PartialBadge) o;
                return Objects.equals(imageUrl, that.imageUrl);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(imageUrl);
            }

            @Override
            public String toString() {
                return "PartialBadge{" +
                        "imageUrl='" + imageUrl + '\'' +
                        '}';
            }
        }

        @Getter
        public static class ActivityBadge extends PartialBadge {
            int badgeNo;
            String badgeId;
            boolean activated;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ActivityBadge that = (ActivityBadge) o;
                return badgeNo == that.badgeNo
                        && activated == that.activated
                        && Objects.equals(badgeId, that.badgeId)
                        && Objects.equals(imageUrl, that.imageUrl);
            }

            @Override
            public int hashCode() {
                return Objects.hash(badgeNo, badgeId, imageUrl, activated);
            }

            @Override
            public String toString() {
                return "ActivityBadge{" +
                        "badgeNo=" + badgeNo +
                        ", badgeId='" + badgeId + '\'' +
                        ", imageUrl='" + imageUrl + '\'' +
                        ", activated=" + activated +
                        '}';
            }
        }

        @Nullable
        public StreamingProperty.Subscription getSubscription() {
            return streamingProperty.subscription;
        }

        public boolean hasSubscription() {
            return streamingProperty.subscription != null;
        }

        @Override
        public String toString() {
            return "Profile{" +
                    "nickname='" + nickname + '\'' +
                    ", profileImageUrl='" + profileImageUrl + '\'' +
                    ", userRoleCode='" + userRoleCode + '\'' +
                    ", verifiedMark=" + verifiedMark +
                    ", activityBadges=" + Arrays.toString(activityBadges) +
                    ", streamingProperty=" + streamingProperty +
                    '}';
        }
    }

    int msgTypeCode = 0;
    @Getter
    public String userId;
    @Getter
    public String content;
    @Getter
    public Date createTime;
    @Getter
    public Extras extras = new Extras();
    public Profile profile = new Profile();

    @Getter
    public String rawJson;

    public int getChatTypeCode() {
        return msgTypeCode;
    }

    /**
     * Returns profile of sender of the message.
     * @return nullable {@link Profile}
     */
    @Nullable
    public Profile getProfile() {
        return profile;
    }

    public boolean hasProfile() {
        return profile != null;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "userId='" + userId + '\'' +
                ", msgTypeCode='" + msgTypeCode + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", extras=" + extras +
                ", profile=" + profile +
                '}';
    }
}
