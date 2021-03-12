package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.AppData;
import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.ViewInterfaces.ScheduleView;
import com.BSLCommunity.CSN_student.Views.ScheduleActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class SchedulePresenter {
    private final ScheduleView scheduleView; // View расписания

    public enum EntityTypes {
        GROUPS,
        TEACHERS
    }

    private final EntityTypes type; // Тип расписания
    private final GroupModel groupModel;
    private final AppData appData;
    private String selectedGroup;
    private int selectedHalf;
    private ArrayList<ScheduleList> scheduleList;

    public SchedulePresenter(ScheduleView scheduleView, EntityTypes type) {
        this.scheduleView = scheduleView;
        this.type = type;
        this.groupModel = GroupModel.getGroupModel();
        this.appData = AppData.getAppData();
    }

    public void initSpinnerData() {
        ArrayList<String> groups = groupModel.getGroupsOnCourse(appData.userData.getCourse());
        int currentGroup = 0;
        for (int i = 0; i < groups.size(); ++i) {
            if (groups.get(i).equals(appData.userData.getGroupName())) {
                currentGroup = i;
                break;
            }
        }
        scheduleView.setSpinnerData(groups, currentGroup);
    }

    public void initSchedule(int half) {
        scheduleList = groupModel.findById(appData.userData.getGroupId()).scheduleList;
        if(scheduleList != null){
            scheduleView.setSchedule(scheduleList, half);
        } else{
            //showError();
        }
    }

    public void changeGroup(String group){
        this.selectedGroup = group;
        scheduleList = groupModel.findByName(selectedGroup).scheduleList;
        scheduleView.setSchedule(scheduleList, selectedHalf);
    }

    public void changeHalf(int half){
        this.selectedHalf = half;
        scheduleView.setSchedule(scheduleList, selectedHalf);
    }

/*
    UserModel user = UserModel.getUserModel(); // Данные пользователя
    int selectFirst = 0; // Для выбора расписания которое будет показано при загрузке самого окна
    List<String> listAdapter = new ArrayList<>(); // Список строк в спиннере
    ArrayAdapter<String> dataAdapter = null;

        if (entity.equals("teachers")) {

        //создаем лист преподов
        if (TeachersModel.teacherLists.size() != 0) {
            progressBar.setVisibility(View.GONE);

            // int listSize = Teachers.teacherLists.size();
            int listSize = 28; // кол-во преподов на кафедре

            //добавляем в массив из класса Teachers преподы
            idElements = new int[listSize];
            for (int j = 0; j < listSize; ++j) {
                try {
                    JSONObject FIOJson = new JSONObject(TeachersModel.teacherLists.get(j).FIO);

                    // Разбиение ФИО на составные и установка с инициалами
                    String[] fioStrs = FIOJson.getString(LocaleHelper.getLanguage(this)).split(" ");
                    listAdapter.add(fioStrs[2] + " " + fioStrs[0].charAt(0) + ". " + fioStrs[1].charAt(0));
                } catch (Exception e) {
                }
                idElements[j] = TeachersModel.teacherLists.get(j).id;
            }
            spinner.setPrompt(getString(R.string.teachers_prompt));
            spinner.setEnabled(true);
            dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_layout, listAdapter);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
            spinner.setAdapter(dataAdapter);
            // Выбор первого учителя не имеет смысла
            selectFirst = 0;
        } else {
            //в том случае если групп по курсу нету
            //  listAdapter.add("No teachers");
        }

    } else {
        //создаем лист групп
        if (GroupModel.groups.size() != 0) {
            progressBar.setVisibility(View.GONE);

            //добавляем в массив из класса Groups группы
            idElements = new int[GroupModel.groups.size()];
            for (int j = 0; j < GroupModel.groups.size(); ++j) {
                listAdapter.add(GroupModel.groups.get(j).groupName);
                idElements[j] = GroupModel.groups.get(j).id;
                // Если id группы совпадает с id группы пользователя - эта группа и будет показана первой
                if (0 == idElements[j])
                    selectFirst = j;
            }
            spinner.setPrompt(getString(R.string.group_prompt));
            spinner.setEnabled(true);
            dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_schedule_groups_layout, listAdapter);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
            spinner.setAdapter(dataAdapter);
        } else {
            //в том случае если групп по курсу нету
            // listAdapter.add("No groups");
        }
    }

    //устанавливаем спинер
        spinner.setSelection(selectFirst);
        spinner.setOnItemSelectedListener(this);
*/

}

