package kr.astar.api.twitchLiv.listener;

import com.github.twitch4j.pubsub.domain.ChannelBitsData;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.SubscriptionData;
import kr.astar.api.twitchLiv.data.Chat;

public interface TwitchEventListener {
    default void onChat(Chat chat) {}
    default void onBitsEvent(ChannelBitsData data) {}
    default void onSubscribeEvent(SubscriptionData data) {}
    default void onPointsRedemptionEvent(ChannelPointsRedemption redemption) {}
    default void onError(Exception e) {}
}
