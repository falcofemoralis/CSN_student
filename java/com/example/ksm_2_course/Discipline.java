package com.example.ksm_2_course;

public class Discipline
{
    private String name, teacher, value;
    private boolean[][] complete;
    private int labs, progress;

    public Discipline()
    { }

    public Discipline(String name, String teacher, String value, int labs)
    {
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
}