package kr.astar.api.weflabLiv.data.alert;

public record Donation(
        User user,
        String content,
        long amount,
        boolean isTest,
        long timestamp,
        DonationData donationData,
        PlatformData platformData
) {
}

