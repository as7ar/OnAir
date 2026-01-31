package kr.astar.api.utubeLiv;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import kr.astar.api.utubeLiv.data.Chatting;
import kr.astar.api.utubeLiv.data.SuperChat;
import kr.astar.api.utubeLiv.data.SuperSticker;
import kr.astar.api.utubeLiv.enums.MessageType;
import kr.astar.api.utubeLiv.exception.InvalidApiKeyException;
import kr.astar.api.utubeLiv.exception.NullLiveChatId;
import kr.astar.api.utubeLiv.listener.YouTubeEventListener;
import kr.astar.api.utubeLiv.utilities.Debugger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;

public class Youtube {

    private final String api;
    private final String videoID;
    private final List<YouTubeEventListener> listeners;
    private final long pollingInterval;
    private final boolean isDebug;

    private int chat_length = 0;
    private final Debugger debugger = new Debugger();
    private final YouTube youtube;

    private boolean isRunning = false;
    private Thread chatThread;

    public Youtube(YouTubeBuilder builder) throws GeneralSecurityException, IOException {
        this.api = builder.API_KEY;
        this.videoID = builder.VIDEO_ID;
        this.listeners = builder.listeners;
        this.isDebug = builder.isDebug;
        this.pollingInterval = builder.pollingInterval;
        this.chat_length = builder.chat_length;

        this.youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                (HttpRequestInitializer) null
        ).setApplicationName("YouTubeLiv").build();

        debugger.log("new youtube listener created");
        validateApiKey();

        if (!isRunning) {
            isRunning = true;
            chatThread = new Thread(this::runChat);
            chatThread.start();
            debugger.log("new youtube listener started");
        }
    }

    private void validateApiKey() throws IOException {
        debugger.log("validate API key");
        try {
            youtube.videos()
                    .list(List.of("snippet"))
                    .setKey(api)
                    .setId(List.of("dQw4w9WgXcQ"))
                    .execute();
            debugger.log("validate successes");
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 400 || e.getStatusCode() == 403) {
                debugger.log("validate fail");
                throw new InvalidApiKeyException();
            }
            throw e;
        }
    }

    public String getVideoId() {
        return videoID;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        debugger.log("youtube listener stopped");
        isRunning = false;
        if (chatThread != null) {
            chatThread.interrupt();
        }
    }

    private void runChat() {
        try {
            String liveChatId = getListChatID();
            if (liveChatId == null)
                throw new NullLiveChatId();

            String lastMessageTimestamp = Instant.now().toString();
            String pageToken = null;

            while (isRunning) {
                try {
                    YouTube.LiveChatMessages.List request =
                            youtube.liveChatMessages()
                                    .list(liveChatId, List.of("snippet", "authorDetails"))
                                    .setKey(api)
                                    .setPageToken(pageToken);

                    LiveChatMessageListResponse response = request.execute();

                    for (LiveChatMessage message : response.getItems()) {
                        if (message.getSnippet()
                                .getPublishedAt()
                                .toString()
                                .compareTo(lastMessageTimestamp) > chat_length) {
                            processMessage(message);
                        }
                    }

                    pageToken = response.getNextPageToken();
                    Thread.sleep(response.getPollingIntervalMillis());
                } catch (Exception e) {
                    processError(e);
                    Thread.sleep(pollingInterval);
                    break;
                }
            }
        } catch (Exception e) {
            processError(e);
        }
    }

    private String getListChatID() throws IOException {
        VideoListResponse response = youtube.videos()
                .list(List.of("liveStreamingDetails"))
                .setKey(api)
                .setId(List.of(videoID))
                .execute();

        if (response.getItems() == null) return null;

        Video video = response.getItems().get(0);
        return video.getLiveStreamingDetails() != null
                ? video.getLiveStreamingDetails().getActiveLiveChatId()
                : null;
    }

    private void processMessage(LiveChatMessage message) {
        if (!isRunning) return;

        String type = message.getSnippet().getType();

        if (type.equals(MessageType.SUPER_CHAT_MESSAGE.getType())) {
            SuperChat superChat = new SuperChat(
                    message.getAuthorDetails().getDisplayName(),
                    message.getSnippet().getSuperChatDetails().getAmountDisplayString(),
                    message.getSnippet().getSuperChatDetails().getUserComment(),
                    videoID,
                    message.getSnippet().getPublishedAt().toString(),
                    message
            );
            listeners.forEach(l -> l.onSuperChat(superChat));

        } else if (type.equals(MessageType.SUPER_STICKER_MESSAGE.getType())) {
            SuperSticker superSticker = new SuperSticker(
                    message.getAuthorDetails().getDisplayName(),
                    message.getSnippet().getSuperStickerDetails().getAmountDisplayString(),
                    message.getSnippet().getSuperStickerDetails()
                            .getSuperStickerMetadata().getStickerId(),
                    videoID,
                    message.getSnippet().getPublishedAt().toString(),
                    message
            );
            listeners.forEach(l -> l.onSuperSticker(superSticker));

        } else if (type.equals(MessageType.TEXT_MESSAGE.getType())) {
            Chatting chat = new Chatting(
                    message.getAuthorDetails().getDisplayName(),
                    message.getSnippet().getDisplayMessage(),
                    videoID,
                    message
            );
            listeners.forEach(l -> l.onChat(chat));
        }
    }

    private void processError(Exception e) {
        debugger.log("process error");
        listeners.forEach(l -> l.onError(e));
    }

    public YouTubeInfo channelInfo() {
        return YouTubeInfo.from(videoID, api);
    }

    public void close() {
        stop();
    }
}
