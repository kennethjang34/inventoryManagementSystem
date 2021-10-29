package model;

public class LocationFormatException extends NumberFormatException {

    public LocationFormatException() {
        super("Format of the given location code is inappropriate");
    }
}
