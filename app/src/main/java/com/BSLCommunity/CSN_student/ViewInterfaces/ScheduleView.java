package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Models.ScheduleList;

import java.util.ArrayList;

public interface ScheduleView {
    void setSpinnerData(ArrayList<String> entities, int defaultGroup); // установка групп\учителей в спинере

    void setSchedule(ArrayList<ScheduleList> scheduleList, int half);

    void clearSchedule();
}