package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.google.gson.Gson;

import java.io.IOException;

public class AuditoriumsList {
    public class Auditorium {
        public String auditorium;
        public int building, floor, x, y, height, width;
    }

    private Auditorium[] auditoriums;

    // Инциализация
    public AuditoriumsList(Context context) {
        try {
            String audsJSON = JSONHelper.loadJSONFromAsset(context, "auditoriums.json");
            Gson gson = new Gson();
            auditoriums = gson.fromJson(audsJSON, Auditorium[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Auditorium findAuditoriumInfoByName(String selectedAuditorium) {
        for (int i = 0; i < auditoriums.length; ++i)
            if (selectedAuditorium.equals(auditoriums[i].auditorium)) return auditoriums[i];
        return null;
    }

    public Auditorium getInfo(String selectedAuditorium) {
        Auditorium auditorium = findAuditoriumInfoByName(selectedAuditorium);
        if (auditorium == null) return null;
        return auditorium;
    }
}
