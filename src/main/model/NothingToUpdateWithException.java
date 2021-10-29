package model;

public class NothingToUpdateWithException extends RuntimeException {
    public NothingToUpdateWithException() {
        super("There has been no change at all");
    }
}
