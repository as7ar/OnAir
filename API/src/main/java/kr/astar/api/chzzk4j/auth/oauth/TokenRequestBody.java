package kr.astar.api.chzzk4j.auth.oauth;

public record TokenRequestBody(
        String grantType,
        String clientId,
        String clientSecret,
        String code,
        String state
) {
}
