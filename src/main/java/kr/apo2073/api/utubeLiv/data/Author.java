package kr.apo2073.api.utubeLiv.data;

import com.google.api.services.youtube.model.LiveChatMessage;

public record Author(String name, LiveChatMessage message) {}
