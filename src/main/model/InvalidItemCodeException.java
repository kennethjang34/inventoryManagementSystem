package model;

public class InvalidItemCodeException extends IllegalArgumentException {
    public InvalidItemCodeException() {
        super("The item code given is invalid");
    }
}
