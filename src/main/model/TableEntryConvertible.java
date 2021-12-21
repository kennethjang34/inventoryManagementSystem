package model;

public interface TableEntryConvertible {


    //EFFECTS: return an array of info segments for table entry
    Object[] convertToTableEntry();

    //EFFECTS: return a String array of types(names) of the data returned by convertToTableEntry()
    String[] getColumnNames();



}
