package kr.apo2073.api.chzzk4j.auth.oauth;

public record TokenResponseBody(
        String accessToken,
        String refreshToken,
        String tokenType,
        int expiresIn
) {
}
