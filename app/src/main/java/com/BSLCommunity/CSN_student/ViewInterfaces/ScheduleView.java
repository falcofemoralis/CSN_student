package com.BSLCommunity.CSN_student.ViewInterfaces;

import com.BSLCommunity.CSN_student.Constants.ScheduleType;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Presenters.SchedulePresenter;

import java.util.ArrayList;

public interface ScheduleView {
    /**
     * Установка данных в спинеры
     *
     * @param entities    - данные, которые нужно установить
     * @param defaultItem - элемент, который будет показан при заход на фрагмент (в группах изначально будет показана группа юзера)
     * @param type        - тип расписания
     */
    void setSpinnerData(ArrayList<String> entities, int defaultItem, ScheduleType type); // установка групп\учителей в спинере

    /**
     * Установка расписания
     *
     * @param scheduleList - данные с расписанием
     * @param half         - тип неделя (числитель\знаменатель)
     * @param type         - тип расписания
     */
    void setSchedule(ArrayList<ScheduleList> scheduleList, int half, ScheduleType type);

    /**
     * Очистка данных в таблице расписания
     */
    void clearSchedule();

    /**
     * Демонстрация отсуствия данных в тосте
     */
    void showToastError();
}