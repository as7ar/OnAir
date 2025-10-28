//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kr.apo2073.utubeLiv;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import kr.apo2073.utubeLiv.data.Chatting;
import kr.apo2073.utubeLiv.data.SuperChat;
import kr.apo2073.utubeLiv.data.SuperSticker;
import kr.apo2073.utubeLiv.enums.MessageType;
import kr.apo2073.utubeLiv.exception.InvalidApiKeyException;
import kr.apo2073.utubeLiv.exception.NullLiveChatId;
import kr.apo2073.utubeLiv.listener.YouTubeEventListener;
import kr.apo2073.utubeLiv.utilities.Debugger;
import lombok.Getter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;

public class Youtube {
    private final String api;
    private final String videoID;
    private final List<YouTubeEventListener> listeners;
    private final long pollingInterval;
    @Getter
    private final boolean isDebug;
    private int chat_length = 0;
    private Debugger debugger = new Debugger();
    private final YouTube youtube;
    @Getter
    private boolean isRunning = false;
    private Thread chatThread;

    public Youtube(YouTubeBuilder builder) throws GeneralSecurityException, IOException {
        this.api = builder.API_KEY;
        this.videoID = builder.VIDEO_ID;
        this.listeners = builder.listeners;
        this.isDebug = builder.isDebug;
        this.pollingInterval = builder.pollingInterval;
        this.chat_length = builder.chat_length;
        this.youtube = (new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), (HttpRequestInitializer)null)).setApplicationName("YouTubeLiv").build();
        this.debugger.log("new youtube listener created");
        this.validateApiKey();
        if (!this.isRunning) {
            this.isRunning = true;
            this.chatThread = new Thread(this::runChat);
            this.chatThread.start();
            this.debugger.log("new youtube listener started");
        }
    }

    private void validateApiKey() throws IOException {
        this.debugger.log("validate API key");

        try {
            YouTube.Videos.List request = this.youtube.videos().list(List.of("snippet")).setKey(this.api).setId(List.of("dQw4w9WgXcQ"));
            request.execute();
            this.debugger.log("validate successes");
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() != 400 && e.getStatusCode() != 403) {
                throw e;
            } else {
                this.debugger.log("validate fail");
                throw new InvalidApiKeyException();
            }
        }
    }

    public String getVideoId() {
        return this.videoID;
    }

    public void stop() {
        this.debugger.log("youtube listener stopped");
        this.isRunning = false;
        if (this.chatThread != null) {
            this.chatThread.interrupt();
        }
    }

    private void runChat() {
        try {
            String liveChatId = this.getListChatID();
            if (liveChatId == null) {
                throw new NullLiveChatId();
            }

            String lastMessageTimestamp = Instant.now().toString();
            String pageToken = null;

            while(this.isRunning) {
                try {
                    YouTube.LiveChatMessages.List liveChatRequest = this.youtube.liveChatMessages().list(liveChatId, List.of("snippet", "authorDetails")).setKey(this.api).setPageToken(pageToken);
                    LiveChatMessageListResponse liveChatResponse = (LiveChatMessageListResponse)liveChatRequest.execute();

                    for(LiveChatMessage message : liveChatResponse.getItems()) {
                        if (message.getSnippet().getPublishedAt().toString().compareTo(lastMessageTimestamp) > this.chat_length) {
                            this.processMessage(message);
                        }
                    }

                    pageToken = liveChatResponse.getNextPageToken();
                    Thread.sleep(liveChatResponse.getPollingIntervalMillis());
                } catch (Exception e) {
                    this.processError(e);
                    Thread.sleep(this.pollingInterval);
                    break;
                }
            }
        } catch (Exception e) {
            this.processError(e);
        }

    }

    private String getListChatID() throws IOException {
        YouTube.Videos.List request = this.youtube.videos().list(List.of("liveStreamingDetails")).setKey(this.api).setId(List.of(this.videoID));
        VideoListResponse response = (VideoListResponse)request.execute();
        return response.getItems() != null && ((Video)response.getItems().get(0)).getLiveStreamingDetails() != null ? ((Video)response.getItems().get(0)).getLiveStreamingDetails().getActiveLiveChatId() : null;
    }

    private void processMessage(LiveChatMessage message) {
        if (this.isRunning) {
            if (message.getSnippet().getType().equals(MessageType.SUPER_CHAT_MESSAGE.getType())) {
                this.debugger.log("new super chat process");
                SuperChat superChat = new SuperChat(message.getAuthorDetails().getDisplayName(), message.getSnippet().getSuperChatDetails().getAmountDisplayString(), message.getSnippet().getSuperChatDetails().getUserComment(), this.videoID, message.getSnippet().getPublishedAt().toString(), message);

                for(YouTubeEventListener listener : this.listeners) {
                    listener.onSuperChat(superChat);
                }
            } else if (message.getSnippet().getType().equals(MessageType.SUPER_STICKER_MESSAGE.getType())) {
                this.debugger.log("new super sticker process");
                SuperSticker superSticker = new SuperSticker(message.getAuthorDetails().getDisplayName(), message.getSnippet().getSuperStickerDetails().getAmountDisplayString(), message.getSnippet().getSuperStickerDetails().getSuperStickerMetadata().getStickerId(), this.videoID, message.getSnippet().getPublishedAt().toString(), message);

                for(YouTubeEventListener listener : this.listeners) {
                    listener.onSuperSticker(superSticker);
                }
            } else if (message.getSnippet().getType().equals(MessageType.TEXT_MESSAGE.getType())) {
                this.debugger.log("new message process");
                String contents = message.getSnippet().getDisplayMessage();

                for(YouTubeEventListener listener : this.listeners) {
                    listener.onChat(new Chatting(message.getAuthorDetails().getDisplayName(), contents, this.videoID, message));
                }
            }

        }
    }

    private void processError(Exception e) {
        this.debugger.log("process error");

        for(YouTubeEventListener listener : this.listeners) {
            listener.onError(e);
        }

    }

    public YouTubeInfo channelInfo() {
        return YouTubeInfo.from(this.videoID, this.api);
    }

    public void close() {

    }
}
