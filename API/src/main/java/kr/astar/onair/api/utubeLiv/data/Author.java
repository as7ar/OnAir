package kr.astar.onair.api.utubeLiv.data;

import com.google.api.services.youtube.model.LiveChatMessage;

public record Author(String name, LiveChatMessage message) {}
