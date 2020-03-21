package com.example.ksm_2_course;

import android.app.Application;
import android.content.Context;

public class Discipline extends Application
{
    private String name;
    private String teacher;
    private String value; // name - Название дисциплины, teacher - ФИО преподавателя, value - ценность предмета
    private boolean[][] complete; // состояние сдачи лабораторных работ
    private int labs, progress; // labs - количество лабораторных , progress - суммарное количество сданных и защищенных работ

    public Discipline()
    { }

    public Discipline(String name, String teacher, String value, int labs) {
        this.name = name;
        this.teacher = teacher;
        this.value = value;
        this.labs = labs;
        complete = new boolean[labs][2];
        for (int i = 0; i < labs; ++i)
            complete[i][0] = complete[i][1] = false;
    }

   public String getName()
    {
        return name;
    }

    public String getTeacher() {return teacher;}

    public String getValue() {return value;}

    public int getProgress() {return progress;}

    public int getLabs(){return labs;}

    public void setComplete(boolean arr[][])
    {
        if (arr.length > complete.length)
            return;

        for (int i = 0; i < complete.length; ++i)
        {
            complete[i][0] = arr[i][0];
            complete[i][1] = arr[i][1];
        }
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }

    public boolean[][] getComplete()
    {
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

    public String getStringTeacher(Context context){
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

    public String getStringValue(Context context){
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
