package com.BSLCommunity.CSN_student.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
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

import static com.BSLCommunity.CSN_student.Activities.Settings.encryptedSharedPreferences;

class Discipline {
    private String name, teacher, value;// name - Название дисциплины, teacher - ФИО преподавателя, value - ценность предмета
    private boolean[][] complete; // состояние сдачи лабораторных работ
    private int labs;// labs - количество лабораторных
    private byte IDZ; // 0 - не сдано, 1 - сдано , -1 - не сдано

    public Discipline() {
    }

    public Discipline(String name, String teacher, String value, int labs, byte IDZ) {
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

public class Disciplines extends AppCompatActivity {
    String FILE_NAME = "data_disc_";
    final int BUTTON_TEXT_SIZE = 10, TEXT_SIZE = 13;
    int FALSE, TRUE, TEXT_WHITE; // FALSE(Не сдано) - красный, TRUE(Сдано) - светозеленый
    Drawable FALSE_2, TRUE_2, LAB_STYLE;

    RequestQueue requestQueue;
    Button res; // Кнопка результата
    Button buts[][], IDZ; // Кнопки "Сдано" и "Защита"
    int complete = 0, Labs, count_idz = 0; // complete - подсчет сданих лаб, Labs - хранит количество лабораторних
    public static ArrayList<Discipline> discs = new ArrayList<Discipline>(); //Дисциплины
    Discipline current; // текущая дисциплина
    LinearLayout mainView;
    static boolean whole = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplines);
        Intent intent = getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadRating();
        FILE_NAME += encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, "") + ".json";

        res = (Button) findViewById(R.id.res);

        //Cмена статуса для кнопок сдачи
        View.OnClickListener ButtonChangeStatus = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button but = (Button) v;
                //Смена статуса после нажатия TRUE - сдано, FALSE - не сдано
                if (((ColorDrawable) but.getBackground()).getColor() == FALSE) {
                    ++complete;
                    but.setBackgroundColor(TRUE);
                } else {
                    --complete;
                    but.setBackgroundColor(FALSE);
                }

                res.setText(Integer.toString(complete * 100 / (Labs * 2 + count_idz)) + "%"); // Установка поля среднего прогресса по дисциплине
            }
        };

        //Смена статуса для кнопок защиты (с закругленными углами)
        View.OnClickListener CornerButtonChangeStatus = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button but = (Button) v;
                //Смена статуса после нажатия TRUE - сдано, FALSE - не сдано
                if (((Drawable) but.getBackground() == FALSE_2)) {
                    ++complete;
                    but.setBackground(TRUE_2);
                } else {
                    --complete;
                    but.setBackground(FALSE_2);
                }

                res.setText(Integer.toString(complete * 100 / (Labs * 2 + count_idz)) + "%"); // Установка поля среднего прогресса по дисциплине
            }
        };


        FILE_NAME += encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, "") + ".json";

        FALSE = getResources().getColor(R.color.mb_red);
        TRUE = getResources().getColor(R.color.mb_green);
        TEXT_WHITE = getResources().getColor(R.color.white);

        FALSE_2 = getResources().getDrawable(R.drawable.lab_choose);
        TRUE_2 = getResources().getDrawable(R.drawable.lab_choose_accepted);
        LAB_STYLE = getResources().getDrawable(R.drawable.lab_style);

        // Достать объект Дисциплина с json, возвращает массив дисциплин
        int num = GetCode(intent.getIntExtra("button_id", 0)); // индекс для выбора дисциплины из массива дисциплин
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);
        current = discs.get(num);
        Labs = current.getLabs(); // количество лабораторных

        mainView = findViewById(R.id.Discp_main);

        if (current.getIDZ() == -1)
            buts = new Button[Labs][2];
        else {
            count_idz = 1;
            buts = new Button[Labs + 1][2];
        }
        for (int i = 0; i < Labs; ++i) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newLine.setOrientation(LinearLayout.HORIZONTAL);

            TextView lab = new TextView(this);
            lab.setText(getResources().getString(R.string.Lab) + (i + 1));
            lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lab.setTextSize(TEXT_SIZE);
            lab.setTextColor(TEXT_WHITE);
            lab.setGravity(Gravity.CENTER);
            lab.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * this.getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
            lab.setBackground(LAB_STYLE);
            newLine.addView(lab);

            buts[i][0] = new Button(this);
            buts[i][0].setText(getResources().getString(R.string.Passed));
            buts[i][0].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            buts[i][0].setTextSize(BUTTON_TEXT_SIZE);
            buts[i][0].setTextColor(TEXT_WHITE);
            buts[i][0].setGravity(Gravity.CENTER);
            buts[i][0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            buts[i][0].setOnClickListener(ButtonChangeStatus);
            newLine.addView(buts[i][0]);

            buts[i][1] = new Button(this);
            buts[i][1].setText(getResources().getString(R.string.Protection));
            buts[i][1].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            buts[i][1].setTextSize(BUTTON_TEXT_SIZE);
            buts[i][1].setTextColor(TEXT_WHITE);
            buts[i][1].setGravity(Gravity.CENTER);
            buts[i][1].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            buts[i][1].setOnClickListener(CornerButtonChangeStatus);
            newLine.addView(buts[i][1]);

            mainView.addView(newLine);
        }

        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (24 * this.getResources().getDisplayMetrics().density)));
        mainView.addView(space);

        if (current.getIDZ() != -1) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newLine.setOrientation(LinearLayout.HORIZONTAL);

            TextView IDZText = new TextView(this);
            IDZText.setText(getResources().getString(R.string.IHW));
            IDZText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            IDZText.setTextSize(TEXT_SIZE);
            IDZText.setTextColor(TEXT_WHITE);
            IDZText.setGravity(Gravity.CENTER);
            IDZText.setLayoutParams(new LinearLayout.LayoutParams((int) (150 * this.getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
            IDZText.setBackground(LAB_STYLE);
            newLine.addView(IDZText);

            IDZ = new Button(this);
            IDZ.setText(getResources().getString(R.string.Passed));
            IDZ.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            IDZ.setTextSize(BUTTON_TEXT_SIZE);
            IDZ.setTextColor(TEXT_WHITE);
            IDZ.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            IDZ.setOnClickListener(CornerButtonChangeStatus);
            newLine.addView(IDZ);

            mainView.addView(newLine);
        }

        res = findViewById(R.id.res);

        RestoreAll(discs.get(num)); // Загрузка данных


    }

    // Функция получения кода текущей дисциплины
    private int GetCode(int button_id) {
        switch (button_id) {
            case R.id.Alg:
                return 0;
            case R.id.Arch:
                return 1;
            case R.id.CS:
                return 2;
            case R.id.OBG:
                return 3;
            case R.id.OBD:
                return 4;
            case R.id.SMP:
                return 5;
            default:
                return -1;
        }
    }

    // Загрузка информации о дисциплине
    protected void RestoreAll(Discipline current) {
        boolean[][] compl_but = current.getComplete();
        // Установка состояний кнопок в зависимости от прогресса по текущей дисциплине
        for (int i = 0, size = Labs; i < size; ++i) {
            if (compl_but[i][0]) {
                buts[i][0].setBackgroundColor(TRUE);
                ++complete;
            } else
                buts[i][0].setBackgroundColor(FALSE);

            if (compl_but[i][1]) {
                buts[i][1].setBackground(TRUE_2);
                ++complete;
            } else
                buts[i][1].setBackground(FALSE_2);
        }

        if (current.getIDZ() == 1) {
            IDZ.setBackground(TRUE_2);
            ++complete;
        } else if (current.getIDZ() == 0)
            IDZ.setBackground(FALSE_2);


        ((Button) (findViewById(R.id.Disc))).setText(current.getStringName(this)); // Установка имени дисциплины
        ((Button) (findViewById(R.id.val))).setText(current.getStringValue(this)); // Установка стоимости дисциплины
        ((Button) (findViewById(R.id.teach))).setText(current.getStringTeacher(this)); // Установка ФИО преподавателя
        res.setText(Integer.toString(complete * 100 / (Labs * 2 + count_idz)) + "%"); // Установка среднего прогресса по дисциплине
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean[][] compl_but = new boolean[Labs][2];

        // Сохранение состояния кнопок Сдано и Защита
        for (int i = 0; i < Labs; ++i) {
            if (((ColorDrawable) buts[i][0].getBackground()).getColor() == TRUE)
                compl_but[i][0] = true;
            else
                compl_but[i][0] = false;

            if ((buts[i][1].getBackground() == TRUE_2))
                compl_but[i][1] = true;
            else
                compl_but[i][1] = false;
        }

        // Обновить содержимое текущей дисциплины
        current.setComplete(compl_but);

        if (current.getIDZ() != -1)
            if (IDZ.getBackground() == TRUE_2)
                current.setIDZ((byte) 1);
            else
                current.setIDZ((byte) 0);

        // Сохранение данных о дисциплинах с json
        Gson gson = new Gson();
        String jsonString = gson.toJson(discs);
        JSONHelper.create(this, FILE_NAME, jsonString);

        updateRating(encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""), current.getName(), gson.toJson(compl_but), current.getIDZ(), this);
    }

    public static void updateRating(final String NickName, final String NameDiscp, final String status, final byte IDZ, final Context context) {
        String url = Main.MAIN_URL + "updateRating.php";

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(Disciplines.this, "data saved successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences.Editor prefEditor = encryptedSharedPreferences.edit();
                prefEditor.putBoolean(Settings.KEY_OFFLINE_DATA, true);
                prefEditor.apply();
                Toast.makeText(context, "No connection with server, data saved locally", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", NameDiscp);
                parameters.put("Status", status);
                parameters.put("IDZ", Byte.toString(IDZ));
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
    }

    public static void saveJSON(Context context) {
        SharedPreferences.Editor prefEditor = encryptedSharedPreferences.edit();
        prefEditor.putBoolean(Settings.KEY_OFFLINE_DATA, false);
        prefEditor.apply();
        Gson gson = new Gson();
        String jsonString = gson.toJson(discs);
        JSONHelper.create(context, Main.FILE_NAME, jsonString);
        whole = true;
    }

    public static void getStatus(final String NickName, final int i, final Context context) {
        String url = Main.MAIN_URL + "getStatus.php";

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int count = 0;
                if (!response.equals("null")) {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(response);
                        byte IDZ = (byte) obj.getInt("IDZ");
                        Gson gson = new Gson();
                        boolean[][] compl = gson.fromJson(obj.getString("status"), boolean[][].class);
                        discs.get(i).setComplete(compl);
                        discs.get(i).setIDZ(IDZ);

                        compl = discs.get(0).getComplete();
                        compl[0][0] = true;

                        ++count;
                        if (count == discs.size())
                            saveJSON(context);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (whole) {
                    Toast.makeText(context, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                    whole = false;
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NickName", NickName);
                parameters.put("NameDiscp", discs.get(i).getName());
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public void loadRating() {
        ArrayList<Discipline> discs = new ArrayList<Discipline>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        discs = gson.fromJson(JSONHelper.read(this, FILE_NAME), listType);

        for (int i = 0; i < discs.size(); ++i) {
            Discipline temp = discs.get(i);
            updateRating(encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""), temp.getName(), gson.toJson(temp.getComplete()), temp.getIDZ(), this);
        }
    }
}

