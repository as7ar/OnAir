package kr.astar.api.weflabLiv.data.streamer;

public record StreamerData(
        String loginType,
        String idx,
        String userid,
        String id, String email,
        boolean isMobile,
        boolean isIOS
) {
}
