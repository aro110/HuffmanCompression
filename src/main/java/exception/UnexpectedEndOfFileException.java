package exception;

public class UnexpectedEndOfFileException extends RuntimeException {
    public UnexpectedEndOfFileException(String message) {
        super(message);
    }
}
