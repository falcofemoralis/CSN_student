package com.BSLCommunity.CSN_student.Models;

import com.google.gson.annotations.SerializedName;

public class Subject {
    @SerializedName("teachers")
    public int[] idTeachers;
    @SerializedName("name")
    public String name;
    @SerializedName("img")
    public String imgPath;

    public Subject(int[] idTeachers, String name, String imgPath) {
        this.idTeachers = idTeachers;
        this.name = name;
        this.imgPath = imgPath;
    }

}