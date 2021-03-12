package com.BSLCommunity.CSN_student.Models;

import com.google.gson.annotations.SerializedName;

/*
 * Класс расписание
 * half - числитель/знаменатель, 0 - числитель, 1 - знаменатель
 * day - день недели (отсчет начиная с понедельника)
 * pair - пара
 * subject - предмет (строка JSON)
 * type - тип предмета (строка JSON)
 * room - номер аудитории
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


    public ScheduleList(int half, int day, int pair, String subject, String type, String room) {
        this.half = half;
        this.day = day;
        this.pair = pair;
        this.subject = subject;
        this.room = room;
        this.type = type;
    }
}
