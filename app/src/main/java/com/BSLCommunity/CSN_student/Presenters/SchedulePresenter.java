package com.BSLCommunity.CSN_student.Presenters;

import android.util.Log;

import com.BSLCommunity.CSN_student.Constants.LogType;
import com.BSLCommunity.CSN_student.Constants.ScheduleType;
import com.BSLCommunity.CSN_student.Managers.LogsManager;
import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.Entity.ScheduleList;
import com.BSLCommunity.CSN_student.Models.TeacherModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.ScheduleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SchedulePresenter {
    private final ScheduleType type; // Выбранный тип расписания
    private final GroupModel groupModel; // Модель групп, нужна для получения групп по курсу и их расписания
    private final TeacherModel teacherModel; // Модель преподов, нужна для получения всех преподов и их расписания
    private final UserData userData; // Доступ к данным пользователя
    private final String lang; // Текущий язык в приложении
    private final ScheduleView scheduleView;
    private int selectedHalf; // Выбранная тип недели (0-Числитель\1-Знаменатель)
    private ArrayList<ScheduleList> scheduleList; // Текущее расписание
    private final LogsManager logsManager;
    private boolean isInit;

    /**
     * Конструток для презентера расписаний
     *
     * @param scheduleView - вью формы
     * @param type         - тип расписания
     * @param lang         - язык в приложении
     */
    public SchedulePresenter(ScheduleView scheduleView, ScheduleType type, String lang) {
        this.scheduleView = scheduleView;
        this.type = type;
        this.groupModel = GroupModel.getGroupModel();
        this.userData = UserData.getUserData();
        this.teacherModel = TeacherModel.getTeacherModel();
        this.lang = lang;
        this.logsManager = LogsManager.getInstance();
        this.isInit = true;
    }

    /**
     * Инициализация данных в спинере
     */
    public void initSpinnerData() {
        int defaultItem = 0;
        ArrayList<String> entities;

        if (type == ScheduleType.GROUPS) {
            if (userData.isGuest) {
                entities = groupModel.getAllGroupsName();
                defaultItem = 0;
            } else {
                entities = groupModel.getGroupsOnCourse(userData.user.getCourse());
                for (int i = 0; i < entities.size(); ++i) {
                    if (entities.get(i).equals(userData.user.getGroupName())) {
                        defaultItem = i;
                        break;
                    }
                }
            }
        } else {
            entities = teacherModel.getTeachersNames();
            for (int i = 0; i < entities.size(); ++i) {
                try {
                    entities.set(i, (new JSONObject(entities.get(i))).getString(lang));
                } catch (Exception e) {
                    Log.d("PARSE_ERROR", "No translation for " + entities.get(i));
                }
            }
        }

        // Если расписание по каким-то причинм не загрузилось
        if (entities.size() != 0) {
            scheduleView.setSpinnerData(entities, defaultItem, type);
        } else {
            scheduleView.showToastError();
        }
    }

    /**
     * Инициализация расписания при загрузке
     */
    public void initSchedule() {
        if (type == ScheduleType.GROUPS) {
            if (userData.isGuest) {
                scheduleList = groupModel.findById(GroupModel.groups.get(0).id).scheduleList;
            } else {
                scheduleList = groupModel.findById(userData.user.getGroupId()).scheduleList;
            }
        }
        else {
            scheduleList = teacherModel.findById(1).scheduleList;
        }

        // Установка числителя по стандарту
        // TODO сделать определение числителя и знаменателя
        if (scheduleList != null)
            updateSchedule(scheduleList, 0);
        else
            scheduleView.showToastError();
    }

    /**
     * Обработчик смены расписания в зависимости от выбранного элемента в спинере
     *
     * @param item - выбранный элемент в спинере
     */
    public void changeItem(String item) {
        if (!isInit) {
            logsManager.updateLogs(LogType.CHANGED_SCHEDULE, item);
        } else {
            isInit = false;
        }

        if (type == ScheduleType.GROUPS) {
            scheduleList = groupModel.findByName(item).scheduleList;
        } else {
            try {
                scheduleList = teacherModel.findByName(item, lang).scheduleList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateSchedule(scheduleList, selectedHalf);
    }

    /**
     * Обработчик смены типа недели
     *
     * @param half - новый тип недели (0\1)
     */
    public void changeHalf(int half) {
        logsManager.updateLogs(LogType.CHANGED_HALF, String.valueOf(type.ordinal()));
        this.selectedHalf = half;
        scheduleView.setSchedule(scheduleList, selectedHalf, type);
    }

    /**
     * Обновление данных в зависимости от выбраного языка
     *
     * @param scheduleList - данные расписания
     * @param half         - тип недели
     */
    public void updateSchedule(ArrayList<ScheduleList> scheduleList, int half) {
        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i);

            try {
                list.subject = (new JSONObject(list.subject)).getString(lang);
                list.type = (new JSONObject(list.type)).getString(lang);
            } catch (Exception e) {
                Log.d("PARSE_ERROR", "No translation for " + list.subject);
            }
        }

        scheduleView.setSchedule(scheduleList, half, type);
    }
}

