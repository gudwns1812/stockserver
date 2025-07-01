package stock.mainserver.global.error;

public class FxNotFoundException extends RuntimeException {
    public FxNotFoundException(String message) {
        super(message);
    }

    public FxNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
