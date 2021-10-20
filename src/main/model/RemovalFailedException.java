package model;

import java.util.ArrayList;

public class RemovalFailedException extends Exception {
    ArrayList<RemovalTag> failed;

    public RemovalFailedException(ArrayList<RemovalTag> failed, String description) {
        super(description);
        this.failed = failed;
    }

    public ArrayList<RemovalTag> getFailedList() {
        return failed;
    }

}
