package kr.astar.onair.api.soopliv.data;

public record Donate(String streamerTag, User donator, int amount, String message) {
}
