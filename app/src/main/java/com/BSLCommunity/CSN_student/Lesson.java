package com.BSLCommunity.CSN_student;

import java.util.List;

class ItemType {
    private String room;
    private String subject;
    private String type;

    public String getRoom(){
        return room;
    }

    String getSubject(){
        return subject;
    }

    String getType(){
        return type;
    }
}

public class Lesson {
    public List<ItemType> types;

}




