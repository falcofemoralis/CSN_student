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

    private Auditorium[] auditoriums;

    public AuditoriumModel(Context context) {
        try {
            String auditoriumJSON = JSONHelper.loadJSONFromAsset(context, "auditoriums.json");
            Gson gson = new Gson();
            auditoriums = gson.fromJson(auditoriumJSON, Auditorium[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Auditorium getInfo(String selectedAuditorium) {
        return findAuditoriumInfoByName(selectedAuditorium);
    }

    private Auditorium findAuditoriumInfoByName(String selectedAuditorium) {
        for (Auditorium auditorium : auditoriums)
            if (selectedAuditorium.equals(auditorium.auditorium)) return auditorium;
        return null;
    }
}
