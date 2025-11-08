package kr.apo2073.api.soopliv.data;

public record Donate(String streamerTag, User donator, int amount, String message) {
}
