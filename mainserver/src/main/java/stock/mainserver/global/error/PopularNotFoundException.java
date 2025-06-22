package stock.mainserver.global.error;

public class PopularNotFoundException extends RuntimeException {
    public PopularNotFoundException(String message) {
        super(message);
    }
    public PopularNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
