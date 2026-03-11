package exception;

public class InvalidCompressedFileException extends RuntimeException {

    public InvalidCompressedFileException(String message) {
        super(message);
    }
}