package com.BSLCommunity.CSN_student.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.ScheduleList;
import com.BSLCommunity.CSN_student.Models.TeachersModel;
import com.BSLCommunity.CSN_student.Models.UserModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


//форма расписание предметов группы
public class ScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    final int MAX_PAIR = 5; //кол-во пар в активити
    final int MAX_DAYS = 5; //кол-во дней в активити

    TextView[][] scheduleTextView = new TextView[MAX_DAYS][MAX_PAIR]; //массив из элементов TextView в активити
    TextView type_week; //тип недели
    Spinner spinner; //спинер выбора группы
    ScheduleList[][][] scheduleList;  //сохраненое расписание
    String entity; //тип расписания

    int[] idElements; // id сущностей в спиннере (в порядке расположения их в спиннере)
    int selectedItemId; // ID выбранного элемента в спиннере

    ProgressBar progressBar; //анимация загрузки в спиннере

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_lessons_schedule, container, false);
        entity = getArguments().getString("typeSchedule");

        progressBar = currentFragment.findViewById(R.id.activity_lessons_schedule_pb_main);
        Sprite iIndeterminateDrawable = new ThreeBounce();
        iIndeterminateDrawable.setColor(getActivity().getColor(R.color.white));
        progressBar.setIndeterminateDrawable(iIndeterminateDrawable);

        spinner = currentFragment.findViewById(R.id.activity_lessons_schedule_sp_main);
        spinner.setEnabled(false);

        createSpinner();
        getScheduleElements();
        return currentFragment;
    }


    //создание спиннера групп
    protected void createSpinner() {
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
                        String[] fioStrs = FIOJson.getString(LocaleHelper.getLanguage(getContext())).split(" ");
                        listAdapter.add(fioStrs[2] + " " + fioStrs[0].charAt(0) + ". " + fioStrs[1].charAt(0));
                    }
                    catch (Exception e) {}
                    idElements[j] = TeachersModel.teacherLists.get(j).id;
                }
                spinner.setPrompt(getString(R.string.teachers_prompt));
                spinner.setEnabled(true);
                dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_schedule_layout, listAdapter);
                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
                spinner.setAdapter(dataAdapter);
                // Выбор первого учителя не имеет смысла
                selectFirst = 0;
            } else {
                //в том случае если групп по курсу нету
              //  listAdapter.add("No teachers");
            }

        }
        else {
            //создаем лист групп
            if (GroupModel.groups.size() != 0) {
                progressBar.setVisibility(View.GONE);

                //добавляем в массив из класса Groups группы
                idElements = new int[GroupModel.groups.size()];
                for (int j = 0; j < GroupModel.groups.size(); ++j) {
                    listAdapter.add(GroupModel.groups.get(j).groupName);
                    idElements[j] = GroupModel.groups.get(j).id;
                    // Если id группы совпадает с id группы пользователя - эта группа и будет показана первой
                    if (0== idElements[j])
                        selectFirst = j;
                }
                spinner.setPrompt(getString(R.string.group_prompt));
                spinner.setEnabled(true);
                dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_schedule_groups_layout, listAdapter);
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
    }

    //если в спинере была выбрана группа
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItemId = (int)id;
        try {
            setSchedule(idElements[selectedItemId]);
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    //нужен для реализации интерфейса AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //меняем тип недели
    public void changeTypeWeek(View v) {
        TransitionManager.beginDelayedTransition((LinearLayout) currentFragment.findViewById(R.id.activity_lessons_schedule_ll_main));

        if (type_week.getText().equals(getResources().getString(R.string.denominator)))
            type_week.setText(getResources().getString(R.string.numerator));
        else
            type_week.setText(getResources().getString(R.string.denominator));
        try {
            setSchedule(idElements[selectedItemId]);
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    //получение необходимых полей с активити расписание
    protected void getScheduleElements() {
        type_week = currentFragment.findViewById(R.id.activity_lessons_schedule_bt_weekType);

        //получеам id текстовых полей с activity_lessons_schedule и сохраняем их в массиве schedule[][]
        //i - пары, j - дни
        for (int i = 0; i < MAX_DAYS; ++i) {
            for (int j = 0; j < MAX_PAIR; ++j) {
                scheduleTextView[i][j] = currentFragment.findViewById(getResources().getIdentifier("text_" + i + "_" + j, "id", getActivity().getPackageName()));
            }
        }
    }

    //устанавливаем расписание
    protected void setSchedule(int id) {
        ArrayList<ScheduleList> scheduleList = entity.equals("teachers") ? TeachersModel.findById(id).scheduleList : GroupModel.getGroupModel().findById(id).scheduleList;
        clearSchedule();

        //получаем неделю в зависимости от выбранной недели
        int numType = type_week.getText().equals(getResources().getString(R.string.denominator)) ? 1 : 0;
        final int BOTH_HALF = 2;

        for (int i = 0; i < scheduleList.size(); ++i) {
            ScheduleList list = scheduleList.get(i); // Чтобы не вызывать постоянно метод get (Код будет выглядеть короче)

            if (list.half == numType || list.half == BOTH_HALF)
                try {
                    //парсим предмет по установленому языку в приложении
                    JSONObject subjectJSONObject = new JSONObject(list.subject);
                    String subject = subjectJSONObject.getString(LocaleHelper.getLanguage(getContext()));

                    JSONObject typeJSONObject = new JSONObject(list.type);
                    String type = typeJSONObject.getString(LocaleHelper.getLanguage(getContext()));

                    scheduleTextView[list.day][list.pair].setText(subject + " " + type + " (" + list.room + ")");
                } catch (Exception e) {
                    // Не смогло преобразовать текстовое поле
                    scheduleTextView[list.day][list.pair].setText("JSON parse Error");
                }
        }
    }

    protected void clearSchedule() {
        for (int i = 0; i < MAX_DAYS; ++i)
            for (int j = 0; j < MAX_PAIR; ++j)
                scheduleTextView[i][j].setText("");
    }
}

