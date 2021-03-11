package com.BSLCommunity.CSN_student.Models;

import com.google.gson.annotations.SerializedName;

public class Subject {
    @SerializedName("Code_Discipline")
    public int id;
    @SerializedName("Code_Lector")
    public int idLecturer;
    @SerializedName("Code_Practice")
    public int idPractice;
    @SerializedName("Code_Assistant")
    public int idAssistant;
    @SerializedName("NameDiscipline")
    public String name;
    @SerializedName("Image")
    public String imgPath;
}