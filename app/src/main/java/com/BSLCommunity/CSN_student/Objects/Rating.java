package com.BSLCommunity.CSN_student.Objects;


import android.content.Context;
import com.BSLCommunity.CSN_student.R;

public class Rating {
    private String name, teacher, value;// name - Название дисциплины, teacher - ФИО преподавателя, value - ценность предмета
    private boolean[][] complete; // состояние сдачи лабораторных работ
    private int labs;// labs - количество лабораторных
    private byte IDZ; // 0 - не сдано, 1 - сдано , -1 - не сдано

    public Rating() {
    }

    public Rating(String name, String teacher, String value, int labs, byte IDZ) {
        this.name = name;
        this.teacher = teacher;
        this.value = value;
        this.labs = labs;
        this.IDZ = IDZ;
        complete = new boolean[labs][2];
        for (int i = 0; i < labs; ++i)
            complete[i][0] = complete[i][1] = false;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getValue() {
        return value;
    }

    public byte getIDZ() {
        return IDZ;
    }

    public void setIDZ(byte IDZ) {
        this.IDZ = IDZ;
    }

    public int getLabs() {
        return labs;
    }

    public void setComplete(boolean arr[][]) {
        if (arr.length > complete.length)
            return;

        for (int i = 0; i < complete.length; ++i) {
            complete[i][0] = arr[i][0];
            complete[i][1] = arr[i][1];
        }
    }

    public boolean[][] getComplete() {
        return complete;
    }

    public String getStringName(Context context) {
        switch (this.name) {
            case "Алгоритми та методи обчислень":
                return context.getResources().getString(R.string.Alg_and_metod);
            case "Архітектура комп᾿ютерів":
                return context.getResources().getString(R.string.Arch_Comp);
            case "Комп᾿ютерна схемотехніка":
                return context.getResources().getString(R.string.CompScheme);
            case "Організація баз данних":
                return context.getResources().getString(R.string.DataBase);
            case "Основи безпеки життєдіяльності":
                return context.getResources().getString(R.string.OBG);
            case "Сучасні методи програмування":
                return context.getResources().getString(R.string.S_metod_prog);
            default:
                return "";
        }
    }

    public String getStringTeacher(Context context) {
        switch (this.teacher) {
            case "Кудерметов Равіль Камілович":
                return context.getResources().getString(R.string.teacher_AMO);
            case "Скрупський Степан Юрійович":
                return context.getResources().getString(R.string.teacher_CA);
            case "Сгадов Сергій Олександрович":
                return context.getResources().getString(R.string.teacher_CS);
            case "Паромова Тетяна Олександрівна":
                return context.getResources().getString(R.string.teacher_OBD);
            case "Скуйбіда Олена Леонідівна":
                return context.getResources().getString(R.string.teacher_OBG);
            default:
                return "";
        }
    }

    public String getStringValue(Context context) {
        switch (this.value) {
            case "Іспит":
                return context.getResources().getString(R.string.Exam);
            case "Залік":
                return context.getResources().getString(R.string.test);
            case "Залік/Іспит":
                return context.getResources().getString(R.string.test_exam);
            case "Диф. Залік":
                return context.getResources().getString(R.string.diff_exam);
            default:
                return "";
        }
    }
}
