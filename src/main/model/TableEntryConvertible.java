package model;

public interface TableEntryConvertible {

    //EFFECTS: return an array of column names for table entry
    Object[] getColumnNames();

    //EFFECTS: return an array of info segments for table entry
    Object[] convertToTableEntry();

}
