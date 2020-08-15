package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;;
import android.widget.Toast;
import com.BSLCommunity.CSN_student.Activities.Main;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class SubjectsInfo {
    public static SubjectsInfo instance = null;

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
            String JSONstring = JSONHelper.read(context, "subjectsInfo");
            if (JSONstring != null) {
                Gson gson = new Gson();
                instance.subjectInfo = gson.fromJson(JSONstring, SubjectInfo[].class);
            }
            return instance;
        } catch (Exception e) {
            // В случае неудачи, если данные к примеру повреждены или их просто нету - возвращает null
            return null;
        }
    }

    public static void deleteSubjects() {
        instance = null;
    }

    public static class SubjectInfo {
        public int labsCount, ihwCount, otherCount;
        public int subjectValue;
        public int[] labValue, ihwValue, otherValue;
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

    public void saveValues(int subjectId,
                           ArrayList<Integer> labValues, int labsCount,
                           ArrayList<Integer> ihwValues, int ihwCount,
                           ArrayList<Integer> otherValues, int otherCount){
        instance.subjectInfo[subjectId].labValue = new int[labsCount];
        for (int i=0;i<labsCount;++i)
            instance.subjectInfo[subjectId].labValue[i] = labValues.get(i);

        instance.subjectInfo[subjectId].ihwValue = new int[ihwCount];
        for (int i=0;i<ihwCount;++i)
            instance.subjectInfo[subjectId].ihwValue[i] = ihwValues.get(i);

        instance.subjectInfo[subjectId].otherValue = new int[otherCount];
        for (int i=0;i<otherCount;++i)
            instance.subjectInfo[subjectId].otherValue[i] = otherValues.get(i);
    }

    //сохраням данный в JSON файл
    public void saveSubject(Context context) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectInfo);
        JSONHelper.create(context, "subjectsInfo", jsonString);
    }
}
