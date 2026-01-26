package kr.astar.onair.api.soopliv.exception;

public enum ExceptionCode {
    API_CHAT_CHANNEL_ID_ERROR("API_CHAT_CHANNEL_ID_ERROR", "채널 아이디 조회를 실패했습니다."),
    API_ACCESS_TOKEN_ERROR("API_ACCESS_TOKEN_ERROR", "액세스 토큰을 발급받는 중 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // ===== Getter 직접 구현 =====
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
