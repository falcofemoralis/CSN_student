package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
public class SubjectsInfo {
    public static SubjectsInfo instance = null;
    public static final String FILE_NAME  = "subjectsInfo";

    public static SubjectsInfo getInstance(Context context) {
        if (instance == null)
            return init(context);
        return instance;
    }

    private static SubjectsInfo init(Context context) {
        try {
            // Извлечение локальных данных пользователя
            instance = new SubjectsInfo();

            instance.subjectInfo = new SubjectInfo[Subjects.subjectsList.length];
            for(int i=0; i<Subjects.subjectsList.length;++i)
                instance.subjectInfo[i] = new SubjectInfo();

            //загрузка данных
            String JSONString = JSONHelper.read(context, FILE_NAME);
            if (!JSONString.equals("NOT FOUND")) {
                Gson gson = new Gson();
                instance.subjectInfo = gson.fromJson(JSONString, SubjectInfo[].class);
            }

            return instance;
        } catch (Exception e) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    public static void deleteSubjects(Context context) {
        JSONHelper.delete(context, FILE_NAME);
        instance = null;
    }

    public static class SubjectInfo {
        public int subjectValue;
        public int labsCount, ihwCount, otherCount;
        public ArrayList<Integer> labValues, ihwValues, otherValues;
        public ArrayList<String> labName, ihwName, otherName;

        public SubjectInfo() {
            labValues = new ArrayList<Integer>();
            ihwValues = new ArrayList<Integer>();
            otherValues = new ArrayList<Integer>();

            labName = new ArrayList<String>();
            ihwName = new ArrayList<String>();
            otherName = new ArrayList<String>();
        }

        public void addNewLab() {
            ++labsCount;
            labValues.add(0);
            labName.add("");
        }

        public void addNewIHW() {
            ++ihwCount;
            ihwValues.add(0);
            ihwName.add("");
        }

        public void addNewOther() {
            ++otherCount;
            otherValues.add(0);
            otherName.add("");
        }
    }
    public SubjectInfo[] subjectInfo;

    //сохраняем кол-во лаб по дисциплине
    public void saveCount(int subjectId, int labsCount, int ihwCount, int otherCount) {
        instance.subjectInfo[subjectId].labsCount = labsCount;
        instance.subjectInfo[subjectId].ihwCount = ihwCount;
        instance.subjectInfo[subjectId].otherCount = otherCount;
    }

    //сохраняем ценность предмета
    public void saveSubjectValue(int subjectId, int id) {
        instance.subjectInfo[subjectId].subjectValue = id;
    }

    //сохраням данный в JSON файл
    public void saveSubject(Context context) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectInfo);
        JSONHelper.create(context, FILE_NAME, jsonString);
    }
}
