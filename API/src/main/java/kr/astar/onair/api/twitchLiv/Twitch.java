package kr.astar.onair.api.twitchLiv;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent;
import kr.astar.onair.api.twitchLiv.data.Chat;
import kr.astar.onair.api.twitchLiv.listener.TwitchEventListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Twitch {
    private TwitchClient client;
    private final TwitchBuilder builder;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Twitch(TwitchBuilder builder) {
        this.builder = builder;
        startClient();
    }

    public String fetchChannelName() {
        return client.getHelix()
                .getUsers(null, List.of(builder.getId()), null)
                .execute().getUsers()
                .getFirst().getLogin();
    }

    public long fetchChannelFollowers() {
        return client.getHelix()
                .getChannelFollowers(null,
                        builder.getId(),
                        null, null, null
                ).execute().getTotal();
    }

    public boolean isLive() {
        return !client.getHelix()
                .getStreams(null, null, null,
                        Integer.valueOf(this.builder.getId()),
                        null, null, null, null
                ).execute().getStreams().isEmpty();
    }

    private void startClient() {
        CompletableFuture.runAsync(() -> {
            OAuth2Credential credential = new OAuth2Credential("twitch", builder.TOKEN);

            TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder()
                    .withDefaultAuthToken(new OAuth2Credential("twitch", ""))
                    .withEnableChat(builder.enableChat)
                    .withEnableHelix(builder.enableHelix)
                    .withEnablePubSub(builder.enablePubsub)
                    .withChatAccount(credential);



            client = clientBuilder.build();

            if (builder.enableChat) {
                client.getChat().joinChannel(builder.id);
            }

            registerDefaultEvents();
        }, executor);
    }


    private void registerDefaultEvents() {
        if (builder.enableChat) {
            client.getEventManager().onEvent(ChannelMessageEvent.class, e -> {
                Chat chat= new Chat(e.getUser(), e.getChannel(), e.getReplyInfo(), e.getPermissions());
                for (TwitchEventListener listener : builder.listeners) listener.onChat(chat);
            });
        }

        if (builder.enablePubsub) {
            client.getEventManager().onEvent(ChannelBitsEvent.class, e -> {
                for (TwitchEventListener listener : builder.listeners) listener.onBitsEvent(e.getData());
            });

            client.getEventManager().onEvent(ChannelSubscribeEvent.class, e -> {
                for (TwitchEventListener listener: builder.listeners) listener.onSubscribeEvent(e.getData());
            });

            client.getEventManager().onEvent(ChannelPointsRedemptionEvent.class, e -> {
                for (TwitchEventListener listener : builder.listeners) listener.onPointsRedemptionEvent(e.getRedemption());
            });
        }
    }

    public void shutdown() {
        if (client != null) client.close();
        else executor.shutdown();
    }
}
