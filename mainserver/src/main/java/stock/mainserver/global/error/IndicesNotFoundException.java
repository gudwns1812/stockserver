package stock.mainserver.global.error;

public class IndicesNotFoundException extends RuntimeException {

    public IndicesNotFoundException() {}
    public IndicesNotFoundException(String message) {
        super(message);
    }

    public IndicesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndicesNotFoundException(Throwable cause) {
        super(cause);
    }
}
