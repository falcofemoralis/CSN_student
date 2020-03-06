package com.example.ksm_2_course;

public class Setting {
    private int isRegistered;
    private int defaultGroup;

    public Setting()
    { }

    void setDefaultGroup(int defaultGroup){
        this.defaultGroup = defaultGroup;
    }

    void setIsRegistered(int isRegistered){
        this.isRegistered = isRegistered;
    }
    int getIsRegistered(){
        return isRegistered;
    }
    int getDefaultGroup(){
        return defaultGroup;
    }
}
