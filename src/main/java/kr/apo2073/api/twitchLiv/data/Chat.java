package kr.apo2073.api.twitchLiv.data;

import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.common.util.ChatReply;

import java.util.Set;

public record Chat(EventUser user, EventChannel channel, ChatReply reply, Set<CommandPermission> permissions) { }
