package kr.apo2073.tnliv.exception;

public class TokenNotFound extends RuntimeException {
    public TokenNotFound() {
        super("찾을 수 없는 토큰입니다");
    }
}
