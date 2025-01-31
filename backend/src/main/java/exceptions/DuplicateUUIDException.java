package exceptions;

public class DuplicateUUIDException extends RuntimeException {
    public DuplicateUUIDException(String message) {
        super(message);
    }
}
