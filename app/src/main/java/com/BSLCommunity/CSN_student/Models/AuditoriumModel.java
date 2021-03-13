package com.BSLCommunity.CSN_student.Models;

import android.content.Context;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.google.gson.Gson;

import java.io.IOException;

public class AuditoriumModel {
    public static class Auditorium {
        public String auditorium;
        public int building, floor, x, y, height, width;
    }

    private Auditorium[] auditoriums; // Список аудиторий

    /**
     * Конструктор модели. Тут идет загрузка файла JSON с аудиториями
     *
     * @param context - контекст приложения
     */
    public AuditoriumModel(Context context) {
        try {
            String auditoriumJSON = JSONHelper.loadJSONFromAsset(context, "auditoriums.json");
            auditoriums = (new Gson()).fromJson(auditoriumJSON, Auditorium[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Поиск аудитории по строке
     *
     * @param s - текст
     * @return обьект аудитории
     */
    public Auditorium findAuditoriumInfoByName(String s) {
        for (Auditorium auditorium : auditoriums)
            if (s.equals(auditorium.auditorium)) return auditorium;
        return null;
    }
}
