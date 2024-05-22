public class GameException extends Exception {
    private final ErrorCode errorCode;

    public GameException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GameException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return errorCode + ": " + getMessage();
    }

    public enum ErrorCode {
        DATABASE_ERROR,
        SAVE_GAME_ERROR,
        LOAD_GAME_ERROR,
        DELETE_GAME_ERROR,
        INITIALIZATION_ERROR
    }
}
