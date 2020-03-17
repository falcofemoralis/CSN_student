package com.example.ksm_2_course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Disciplines extends AppCompatActivity
{
    String FILE_NAME = "data_disc_";
    final int FALSE = 0xFFF56D6D, TRUE = 0xFFDFFFBF; // FALSE(Не сдано) - красный, TRUE(Сдано) - светозеленый

    RequestQueue requestQueue;
    Button res; // Кнопка результата
    Button buts[][] = new Button[7][2]; // Кнопки "Сдано" и "Защита"
    int complete = 0, Labs; // complete - подсчет сданих лаб, Labs - хранит количество лабораторних
    ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    Discipline current; // текущая дисциплина
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplines);
        Intent intent = getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pref = PreferenceManager.getDefaultSharedPreferences(Disciplines.this);
        FILE_NAME += pref.getString(SettingsActivity.KEY_NICKNAME, "") + ".json";

        // Достать объект Дисциплина с json, возвращает массив дисциплин
        int num = GetCode(intent.getStringExtra("Name")); // индекс для выбора дисциплины из массива дисциплин
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {}.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);
        current = discs.get(num);


        Labs = current.getLabs(); // количество лабораторных
        // Получение полей лабораторных работ
        String table = "Line_", stringId;
        int id;
        LinearLayout Main_view = (LinearLayout)findViewById(R.id.Main_view);
        LinearLayout[] lines = new LinearLayout[Labs];
        for (int i = 0; i < 7; ++i)
        {
            stringId = table + Integer.toString(i);
            id = getResources().getIdentifier(stringId, "id", getApplicationContext().getPackageName());
            if (i < Labs)
                lines[i] = (LinearLayout)findViewById(id);
            else
                Main_view.removeView((LinearLayout)findViewById(id));
        }

        // Получение кнопок
        table = "B_";
        for(int i=0;i<Labs; ++i)
        {
            stringId = table + Integer.toString(i) + "_" + Integer.toString(0);
            id = getResources().getIdentifier(stringId, "id", getApplicationContext().getPackageName());
            buts[i][0] = (Button)(findViewById(id));

            stringId = table + Integer.toString(i) + "_" + Integer.toString(1);
            id = getResources().getIdentifier(stringId, "id", getApplicationContext().getPackageName());
            buts[i][1] = (Button)(findViewById(id));
        }
        res = (Button) findViewById(R.id.res); // результат в процентах

        RestoreAll(discs.get(num)); // Загрузка данных
    }

    // Загрузка информации о дисциплине
    protected void RestoreAll(Discipline current)
    {
        boolean[][] compl_but = current.getComplete();
        // Установка состояний кнопок в зависимости от прогресса по текущей дисциплине
        for (int i = 0, size = Labs; i < size; ++i)
        {
            if (compl_but[i][0]) {
                buts[i][0].setBackgroundColor(TRUE);
                ++complete;
            }
            else
                buts[i][0].setBackgroundColor(FALSE);

            if (compl_but[i][1]) {
                buts[i][1].setBackgroundColor(TRUE);
                ++complete;
            }
            else
                buts[i][1].setBackgroundColor(FALSE);
        }

        ((Button)(findViewById(R.id.Disc))).setText(current.getName()); // Установка имени дисциплины
        ((Button)(findViewById(R.id.val))).setText(current.getValue()); // Установка стоимости дисциплины
        ((Button)(findViewById(R.id.teach))).setText(current.getTeacher()); // Установка ФИО преподавателя

        res.setText(Integer.toString(complete * 50 / Labs) + "%"); // Установка среднего прогресса по дисциплине
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean[][] compl_but = new boolean[Labs][2];

        // Сохранение состояния кнопок Сдано и Защита
        for (int i = 0; i < Labs; ++i)
        {
            if (((ColorDrawable)buts[i][0].getBackground()).getColor() == 0xFFDFFFBF)
                compl_but[i][0] = true;
            else
                compl_but[i][0] = false;

            if (((ColorDrawable)buts[i][1].getBackground()).getColor() == 0xFFDFFFBF)
                compl_but[i][1] = true;
            else
                compl_but[i][1] = false;
        }

        // Обновить содержимое текущей дисциплины
        current.setComplete(compl_but);
        current.setProgress(complete);

        // Сохранение данных о дисциплинах с json
        Gson gson = new Gson();
        String jsonString = gson.toJson(discs);
        JSONHelper.create(this, FILE_NAME, jsonString);

        updateRating(pref.getString(SettingsActivity.KEY_NICKNAME, ""), current.getName(), gson.toJson(compl_but));
    }

    //Смена статуса полей Сдано и Защита
    public void OnClick(View v)
    {
        Button but = (Button) v;

        //Смена статуса после нажатия TRUE - сдано, FALSE - не сдано
        if (((ColorDrawable) but.getBackground()).getColor() == FALSE)
        {
            ++complete;
            but.setBackgroundColor(TRUE);
        } else
        {
            --complete;
            but.setBackgroundColor(FALSE);
        }

        res.setText(Integer.toString(complete * 50 / Labs) + "%"); // Установка поля среднего прогресса по дисциплине
    }

    // Функция получения кода текущей дисциплины
    private int GetCode(String name)
    {
        switch(name)
        {
            case "Алгоритми та методи обчислень":
                return 0;
            case "Архітектура комп'ютерів":
                return 1;
            case "Комп'ютерна схемотехніка":
                return 2;
            case "Організація баз данних":
                return 3;
            case "Основи безпеки життєдіяльності":
                return 4;
            case "Сучасні методи програмування":
                return 5;
            default:
                return -1;
        }
    }

    protected void updateRating( final String NickName, final String NameDiscp, final String status)
    {
        String url = MainActivity.MAIN_URL + "updateRating.php";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Toast.makeText(Disciplines.this, "data saved successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(Disciplines.this, "No connection with server, data saved locally", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}
