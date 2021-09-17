package com.BSLCommunity.CSN_student.Models.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Класс расписание
 * half - числитель/знаменатель, 0 - числитель, 1 - знаменатель
 * day - день недели (отсчет начиная с понедельника)
 * pair - пара
 * subject - предмет (строка JSON)
 * type - тип предмета (строка JSON)
 * room - номер аудитории
 * groups - группы которые находятся на предмете в расписании у преподавателя
 * */
public class ScheduleList {
    @SerializedName("Half")
    public int half;
    @SerializedName("Day")
    public int day;
    @SerializedName("Pair")
    public int pair;
    @SerializedName("SubjectName")
    public String subject;
    @SerializedName("Room")
    public String room;
    @SerializedName("SubjectType")
    public String type;
    @SerializedName("Groups")
    public String[] groups;
    @SerializedName("Code_Subject")
    public int idSubject;
}
