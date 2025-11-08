package kr.apo2073.api.chzzk4j.auth.oauth;

public record TokenRefreshRequestBody(
        String grantType,
        String refreshToken,
        String clientId,
        String clientSecret
) {
}
