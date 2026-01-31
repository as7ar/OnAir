package kr.astar.api.soopliv.exception;

public class DoneException extends RuntimeException {

    private final String code;
    private final String message;

    public DoneException(ExceptionCode code) {
        super(code.getMessage());
        this.message = code.getMessage();
        this.code = code.getCode();
    }

    public DoneException(String code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
