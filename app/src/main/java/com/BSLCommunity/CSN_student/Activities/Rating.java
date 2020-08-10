package com.BSLCommunity.CSN_student.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Rating extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int COLOR_WHITE = 0xFFFFFFFF;
    final int COLOR_BACK = 0xFF2D2D61;
    Drawable STYLE_CELL, STYLE_BACK, STYLE_GREEN, STYLE_RED;

    LinearLayout mainLayout;
    TableLayout table;
    String URL = Main.MAIN_URL + "getRating.php";
    Spinner sub_spin, gr_spin;

    class User implements Comparable<User> {
        public String nickName;
        public String nameGroup;
        public byte idz;
        public boolean[] status;
        byte sumPositiveStatus = 0;

        User(String nickName, String nameGroup, boolean[] status, byte idz) {
            this.nickName = nickName;
            this.nameGroup = nameGroup;
            this.status = status;
            this.idz = idz;

            for (int i = 0; i < status.length; ++i)
                sumPositiveStatus += status[i] ? 1 : 0;
        }

        @Override
        public int compareTo(User compareUser) {
            return compareUser.sumPositiveStatus - this.sumPositiveStatus;
        }
    }

    ArrayList<User> users = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        STYLE_CELL = getResources().getDrawable(R.drawable.text_but_schedule_style2);
        STYLE_BACK = getResources().getDrawable(R.drawable.text_but_schedule_style3);
        STYLE_GREEN = getResources().getDrawable(R.drawable.text_but_rating_style_green);
        STYLE_RED = getResources().getDrawable(R.drawable.text_but_rating_style_red);

        mainLayout = findViewById(R.id.mainLayout);
        table = findViewById(R.id.table);

        //тут береться предмет со спиннера
        //setData() -> getRating(subject) -> если успешно setTable(users)
        createSpinner();
        getRating(sub_spin.getSelectedItemPosition(), fromLocal());
    }

    public void getRating(final int subjectNumber, final String group) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String tempSubject;
        switch (subjectNumber) {
            case 0:
                tempSubject = "Алгоритми та методи обчислень";
                break;
            case 1:
                tempSubject = "Архітектура комп᾿ютерів";
                break;
            case 2:
                tempSubject = "Комп᾿ютерна схемотехніка";
                break;
            case 3:
                tempSubject = "Організація баз данних";
                break;
            case 4:
                tempSubject = "Основи безпеки життєдіяльності";
                break;
            case 5:
                tempSubject = "Сучасні методи програмування";
                break;
            default:
                tempSubject = "";
                break;
        }

        final String subject = tempSubject;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject users = null;
                try {
                    users = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    setUsers(users);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Rating.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NameGroup", group);
                parameters.put("NameDiscp", subject);
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void setUsers(JSONObject jsonUsers) throws JSONException {
        users.clear();
        JSONArray rating = jsonUsers.getJSONArray("Rating");

        for (int j = 0; j < rating.length(); j++) {
            JSONObject user = rating.getJSONObject(j);
            String status = user.getString("status");
            Gson g = new Gson();
            boolean st[][] = g.fromJson(status, boolean[][].class);
            boolean newSt[] = new boolean[st.length];
            for (int i = 0; i < st.length; ++i)
                newSt[i] = st[i][0] && st[i][1];
            String idzST = user.getString("IDZ");
            byte idz = g.fromJson(idzST, byte.class);
            users.add(new User(user.getString("NickName"), user.getString("NameGroup"), newSt, idz));

        }

        Collections.sort(users);
        setTable(users);
    }

    public void setTable(ArrayList<User> users) {
        int TextSizeHeader = 26;
        int TextSize = 22;
        if (table.getParent() != null) {
            ((ViewGroup) table.getParent()).removeView(table); // <- fix
        }
        //удаляем предыдущию таблицу
        mainLayout.removeAllViews();
        table.removeAllViews();

        //получаем весь рейтинг

        for (int i = 0; i <= users.size(); i++) {
            TableRow row = new TableRow(Rating.this);
            if (i == 0) {
                //эти переменные нужны для определения количества лаб в заголовке, да да, небольшой костыль
                User userL = users.get(0);

                //заголовок таблицы
                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);

                name.setText("  " + getResources().getString(R.string.nickname) + "  ");
                name.setTextColor(COLOR_WHITE);
                name.setTextSize(TextSizeHeader);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackground(STYLE_BACK);
                row.addView(name);

                group.setText("  " + getResources().getString(R.string.group) + "  ");
                group.setTextColor(COLOR_WHITE);
                group.setTextSize(TextSizeHeader);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackground(STYLE_BACK);
                row.addView(group);

                //количество лаб
                for (int k = 0; k < userL.status.length; k++) {
                    TextView lab = new TextView(Rating.this);
                    lab.setText(String.valueOf(k + 1));
                    lab.setTextColor(COLOR_WHITE);
                    lab.setTextSize(TextSizeHeader);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    lab.setBackground(STYLE_BACK);
                    row.addView(lab);
                }

                if (userL.idz >= 0) {
                    idz.setText("  " + getResources().getString(R.string.IHW) + "  ");
                    idz.setTextColor(COLOR_WHITE);
                    idz.setTextSize(TextSizeHeader);
                    idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    idz.setBackground(STYLE_BACK);
                    row.addView(idz);
                }

            } else {
                //row.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                //row.setBackground(ContextCompat.getDrawable(this,R.drawable.borders));
                User user = users.get(i - 1);

                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);

                name.setText(user.nickName);
                name.setTextColor(COLOR_BACK);
                name.setTextSize(TextSize);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackground(STYLE_CELL);
                row.addView(name);

                group.setText(user.nameGroup);
                group.setTextColor(COLOR_BACK);
                group.setTextSize(TextSize);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackground(STYLE_CELL);
                row.addView(group);

                //получаем массив boolean[][]
                boolean st[] = user.status;

                //устанавливаем значения сдачи
                for (int n = 0; n < st.length; n++) {
                    TextView lab = new TextView(Rating.this);

                    if (st[n]) {
                        lab.setBackground(STYLE_GREEN);
                        lab.setText("     ✓     ");
                    } else {
                        lab.setBackground(STYLE_RED);
                        lab.setText("     ╳     ");
                    }
                    lab.setTextSize(TextSize);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    row.addView(lab);
                }

                TextView idz = new TextView(Rating.this);
                if (user.idz == 1) {
                    idz.setBackground(STYLE_GREEN);
                    idz.setText("     ✓     ");
                    idz.setTextSize(TextSize);
                    idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    row.addView(idz);
                } else if (user.idz == 0) {
                    idz.setBackground(STYLE_RED);
                    idz.setText("     ╳     ");
                    idz.setTextSize(TextSize);
                    idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    row.addView(idz);
                }


            }
            table.addView(row);
        }

        mainLayout.addView(table);
    }

    protected void createSpinner() {
        sub_spin = findViewById(R.id.subjectSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.subjects_values,
                R.layout.color_spinner_schedule
        );
        sub_spin.setOnItemSelectedListener(this);
        sub_spin.setAdapter(adapter);

        gr_spin = findViewById(R.id.gr_rat_spin);
        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.group_values_and_ALL,
                R.layout.color_spinner_schedule
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        gr_spin.setAdapter(adapter);
        gr_spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getRating(sub_spin.getSelectedItemPosition(), fromLocal());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String fromLocal() {
        //String tmpGrp = getResources().getString(R.string.all);
        switch (gr_spin.getSelectedItem().toString()) {
            case "Все":
                return "ALL";
            case "Усі":
                return "ALL";
            default:
                return gr_spin.getSelectedItem().toString();
        }

    }
}

