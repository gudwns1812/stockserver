package kis.client.global.error;

public class RedisTypeException extends RuntimeException {

    public RedisTypeException() {
        super();
    }

    public RedisTypeException(String message) {
        super(message);
    }

    public RedisTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
