package kr.apo2073.api.chzzk4j.types.channel;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ChzzkChannelRules {
    /**
     * -- GETTER --
     *  Get the user is agreed to the rules of channel.
     */
    private boolean agree;
    /**
     * -- GETTER --
     *  Get the id of channel.
     */
    private String channelId;
    /**
     * -- GETTER --
     *  Get the rule string of channel.
     */
    private String rule;
    /**
     * -- GETTER --
     *  Get when the rule updated in yyyy-mm-dd HH:mm:ss format.
     */
    private String updatedDate;
    /**
     * -- GETTER --
     *  Get the user is agreed to the rules of channel.
     */
    private boolean serviceAgree;

    private ChzzkChannelRules() {}

    @Override
    public String toString() {
        return "ChzzkChannelRules{" +
                "agree=" + agree +
                ", channelId='" + channelId + '\'' +
                ", rule='" + rule + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", serviceAgree=" + serviceAgree +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChzzkChannelRules that = (ChzzkChannelRules) o;
        return agree == that.agree && serviceAgree == that.serviceAgree && Objects.equals(channelId, that.channelId) && Objects.equals(rule, that.rule) && Objects.equals(updatedDate, that.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agree, channelId, rule, updatedDate, serviceAgree);
    }
}
