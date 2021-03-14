package com.BSLCommunity.CSN_student.Views;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Constants.WorkStatus;
import com.BSLCommunity.CSN_student.Constants.WorkType;
import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Presenters.SubjectEditorPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.SubjectEditorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectEditorActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, SubjectEditorView {
    LinearLayout labsLL, ihwLL, otherLL;   // Выпадающие списки работ
    TableLayout labsListTL, ihwListTL, othersListTL; // Списки работ
    LinearLayout rootContainer; //элемент в котором находятся все объекты

    // Тексты и цвета кнопок статусов в спиннере
    String[] workStatuses;
    final int[] wordStatusColors = {
            R.color.not_passed,
            R.color.in_process,
            R.color.done_without_report,
            R.color.done_with_report,
            R.color.waiting_acceptation,
            R.color.passed_without_report,
            R.color.passed_with_report};;

    TableRow focusedRow = null;
    SubjectEditorPresenter subjectEditorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_subject_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Контейнеры которые содержат списки работ и кнопки для отображения/добавления работ
        labsLL = findViewById(R.id.activity_subject_info_ll_labs);
        ihwLL = findViewById(R.id.activity_subject_info_ll_ihw);
        otherLL = findViewById(R.id.activity_subject_info_ll_other);

        // Списки работ
        labsListTL = findViewById(R.id.activity_subject_info_tl_labs_data);
        ihwListTL = findViewById(R.id.activity_subject_info_tl_ihw_data);
        othersListTL = findViewById(R.id.activity_subject_info_tl_other_data);

        // Главный контейнер для анимации
        rootContainer = findViewById(R.id.activity_subject_info_ll_main);

        // Константы статусов и цветов бекграунда для них
        workStatuses = getResources().getStringArray(R.array.work_statuses);

        Spinner valueSpinner = findViewById(R.id.activity_subject_info_sp_values);
        createSpinnerAdapter(valueSpinner, R.array.subject_values, SubjectValue.EXAM.ordinal());

        String intentDataSubject = getIntent().getStringExtra("Subject");
        this.subjectEditorPresenter = new SubjectEditorPresenter(this, intentDataSubject);
    }

    @Override
    protected void onPause() {
        if (focusedRow != null)
            saveChanges(focusedRow);

        this.subjectEditorPresenter.finishEdit();
        super.onPause();
    }

    /**
     * Создание адаптера для спиннера
     * @param spinner - объект спиннера
     * @param resArray - id ресурса массиво из res
     * @param defSelection - номер значения по умолчанию
     */
    private void createSpinnerAdapter(Spinner spinner, int resArray, int defSelection) {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                resArray,
                R.layout.spinner_subjectinfo_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(adapter);

        try {
            spinner.setSelection(defSelection);
        } catch (Exception e) {
            spinner.setSelection(0);
        }

        spinner.setOnItemSelectedListener(this);
    }

    /**
     * Установка информации о редактируемой дисциплине
     * @param editableSubject - редактируемая дисциплина
     */
    @Override
    public void setSubjectData(EditableSubject editableSubject) {
        int[] idTeachers = {R.id.activity_subject_info_bt_lector, R.id.activity_subject_info_bt_practice, R.id.activity_subject_info_bt_assistant};

        // Установка имени дисциплины
        try {
            String subjectName = new JSONObject(editableSubject.name).getString(LocaleHelper.getLanguage(this));
            ((TextView)findViewById(R.id.activity_subject_info_bt_subjectName)).setText(subjectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Установка ФИО преподавателей, пустые поля (где нету преподавателей) удаляются
        for (int i = 0; i < idTeachers.length; ++i) {
            if (i < editableSubject.idTeachers.length) {
                // TODO получение преподов по id
                try {
                    String teacherName = new JSONObject(Integer.toString(editableSubject.idTeachers[i])).getString(LocaleHelper.getLanguage(this));
                    ((Button)findViewById(idTeachers[i])).setText(teacherName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                ((Button)findViewById(idTeachers[i])).setVisibility(View.GONE);
            }
        }

        // Отрисовка текущих работ в дисциплине
        ArrayList<EditableSubject.Work> labs = editableSubject.allWorks.get(WorkType.LABS);
        ArrayList<EditableSubject.Work> ihw = editableSubject.allWorks.get(WorkType.IHW);
        ArrayList<EditableSubject.Work> others = editableSubject.allWorks.get(WorkType.OTHERS);

        for (int i = 0; i < labs.size(); ++i)
            drawElementWork(labsListTL, labs.get(i));
        for (int i = 0; i < ihw.size(); ++i)
            drawElementWork(ihwListTL, ihw.get(i));
        for (int i = 0; i < others.size(); ++i)
            drawElementWork(othersListTL, others.get(i));
    }

    /**
     * Установка прогресса для все дисциплины
     * @param progress - прогресс в процентах
     */
    @Override
    public void setWorkProgress(int progress) {
        ((Button) findViewById(R.id.activity_subject_info_bt_progress)).setText(progress + "%");
    }

    /**
     * Отрисовка строки работы в блоке работ
     * @param infoTL - блок работ в который добавляется элемент
     * @param work - работа пользователя
     */
    private void drawElementWork(TableLayout infoTL, EditableSubject.Work work) {
        TableRow elementWork = (TableRow) getLayoutInflater().inflate(R.layout.inflate_work_element, infoTL, false); // Строка работы

        EditText nameWorkEt = elementWork.findViewById(R.id.inflate_work_element_et_name_work);
        nameWorkEt.setText(work.name);
        nameWorkEt.setOnFocusChangeListener(this);

        Spinner spinner = elementWork.findViewById(R.id.inflate_work_element_spin_work_status);
        createSpinnerAdapter(spinner, R.array.work_statuses, WorkStatus.NOT_PASSED.ordinal());
        spinner.setSelection(work.workStatus.ordinal());
        spinner.setOnItemSelectedListener(this);

        EditText markEt = elementWork.findViewById(R.id.inflate_work_element_et_mark);
        markEt.setText(work.mark);
        markEt.setOnFocusChangeListener(this);

        ((Button)elementWork.findViewById(R.id.inflate_work_element_bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteElementWork(v);
            }
        });

        infoTL.addView(elementWork);
    }

    /**
     * Обработчик нажатия на кнопки создания новой работы в списке работ
     * @param view - кнопка добавления
     */
    public void addElementWork(View view) {

        WorkType workType = null;
        TableLayout infoTL = null;

        int id = view.getId();
        if (id == R.id.activity_subject_info_bt_add_lab) {
            workType = WorkType.LABS;
            infoTL = findViewById(R.id.activity_subject_info_tl_labs_data);
        }
        else if (id == R.id.activity_subject_info_bt_add_ihw) {
            workType = WorkType.IHW;
            infoTL = findViewById(R.id.activity_subject_info_tl_ihw_data);
        }
        else if (id == R.id.activity_subject_info_bt_add_other) {
            workType = WorkType.OTHERS;
            infoTL = findViewById(R.id.activity_subject_info_tl_other_data);
        }

        this.subjectEditorPresenter.addWork(workType);
        this.drawElementWork(infoTL, new EditableSubject.Work());
    }

    /**
     * Обработчик нажатия на кнопки категории работ для скрытия или отображения работ
     * @param view - нажатый елемент вью
     */
    public void openWork(View view) {
        int id = view.getId();

        if (id == R.id.activity_subject_info_bt_labs) {
            changeVisibleWork((Button) findViewById(R.id.activity_subject_info_bt_add_lab), labsListTL);
        } else if (id == R.id.activity_subject_info_bt_ihw) {
            changeVisibleWork((Button) findViewById(R.id.activity_subject_info_bt_add_ihw), ihwListTL);
        } else if (id == R.id.activity_subject_info_bt_other) {
            changeVisibleWork((Button) findViewById(R.id.activity_subject_info_bt_add_other), othersListTL);
        }
    }

    /**
     * Управление видимостью списка (открытие и скрытие). Скрывает если открыто и наоборот
     * @param addBt - кнопка которая находится в блоке для добавления работ
     * @param infoListTL - список с работами
     */
    private void changeVisibleWork(final Button addBt, final TableLayout infoListTL) {
        int visibility = infoListTL.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;

        TransitionManager.beginDelayedTransition(rootContainer);
        infoListTL.setVisibility(visibility);
        TransitionManager.beginDelayedTransition(rootContainer);
        addBt.setVisibility(visibility);
    }

    /**
     * ОБработчик нажатия на кнопку удаления
     * @param view - кнопка
     */
    public void deleteElementWork(View view) {
        final TableRow workRow = (TableRow)(view.getParent()); // Строчка работы в списке
        final TableLayout infoTL = ((TableLayout)(workRow.getParent())); // Группа

        WorkType workType = getWorkType(infoTL.getId());
        int index = infoTL.indexOfChild(workRow);

        this.subjectEditorPresenter.deleteWork(workType, index);
        TransitionManager.beginDelayedTransition(rootContainer);
        infoTL.removeView(workRow);
    }

    /**
     * Получение всех полей из строки работы с дальнейшем сохранение через презентер
     * @param elementWork - строка предмета (TableRow)
     */
    public void saveChanges(TableRow elementWork) {
        TableLayout parentTL = (TableLayout) elementWork.getParent();

        WorkType workType = getWorkType(parentTL.getId());
        int index = parentTL.indexOfChild(elementWork);
        String name = ((TextView)elementWork.findViewById(R.id.inflate_work_element_et_name_work)).getText().toString();
        String mark = ((TextView)elementWork.findViewById(R.id.inflate_work_element_et_mark)).getText().toString();
        long id = ((Spinner)elementWork.findViewById(R.id.inflate_work_element_spin_work_status)).getSelectedItemId();

        this.subjectEditorPresenter.changeWork(workType, index, name, WorkStatus.values()[(int)id], mark);
    }

    /**
     * Обработчик нажатия на спиннеры (всей дисциплины и всех предметов)
     * @see android.widget.AdapterView.OnItemSelectedListener
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Вызов был инициирован спиннером выбора статуса предмета - устаналиваем статус (экз, зачет, диф зачет)
        if (parent.getId() == R.id.activity_subject_info_sp_values) {
            this.subjectEditorPresenter.changeSubjectValue(SubjectValue.values()[(int)id]);
            return;
        }

        // Вызов был инициирован спиннером выбора статуса какой либо из работы
        TableRow elementWork = (TableRow) parent.getParent(); // Получаем сам элемент
        parent.setBackgroundResource(wordStatusColors[(int)id]); // Устанавка цвета относительно выбранного варианта
        saveChanges(elementWork);
    }

    /**
     * Нужен для реализации интерфейса AdapterView.OnItemSelectedListener
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Определения типа работы по id TableLayout
     * @param resId - id TableLayout
     * @return - константа типа работы
     */
    private WorkType getWorkType(int resId) {
        if (resId == R.id.activity_subject_info_tl_labs_data) {
            return WorkType.LABS;
        }
        else if (resId == R.id.activity_subject_info_tl_ihw_data) {
            return WorkType.IHW;
        }
        else if (resId == R.id.activity_subject_info_tl_other_data) {
            return  WorkType.OTHERS;
        }

        return null;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            saveChanges(focusedRow);
            focusedRow = null;
        } else  {
            focusedRow = (TableRow) v.getParent();
        }
    }
}
