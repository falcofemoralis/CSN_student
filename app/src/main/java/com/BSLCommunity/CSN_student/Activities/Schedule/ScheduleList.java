package com.BSLCommunity.CSN_student.Activities.Schedule;

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

    public int half, day, pair;
    public String subject;
    public String type;
    public String room;

    public ScheduleList(int half, int day, int pair, String subject, String type, String room) {
        this.half = half;
        this.day = day;
        this.pair = pair;
        this.room = room;
        this.subject = subject;
        this.type = type;
    }
}
