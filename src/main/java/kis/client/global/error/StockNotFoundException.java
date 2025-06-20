package kis.client.global.error;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException() {
        super();
    }

    public StockNotFoundException(String message) {
        super(message);
    }

    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
