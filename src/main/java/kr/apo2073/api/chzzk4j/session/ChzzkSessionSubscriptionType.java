package kr.apo2073.api.chzzk4j.session;

import lombok.Getter;

@Getter
public enum ChzzkSessionSubscriptionType {
    CHAT("/open/v1/sessions/events/subscribe/chat",
            "/open/v1/sessions/events/unsubscribe/chat"),
    DONATION("/open/v1/sessions/events/subscribe/donation",
            "/open/v1/sessions/events/unsubscribe/donation"),
    CHANNEL_SUBSCRIBE("/open/v1/sessions/events/subscribe/subscription",
            "/open/v1/sessions/events/unsubscribe/subscription");

    ChzzkSessionSubscriptionType(final String subscribeUrl, final String unsubscribeUrl) {
        this.subscribeUrl = subscribeUrl;
        this.unsubscribeUrl = unsubscribeUrl;
    }

    private final String subscribeUrl;
    private final String unsubscribeUrl;

}
