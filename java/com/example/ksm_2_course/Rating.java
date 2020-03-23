package com.example.ksm_2_course;

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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Rating extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int COLOR_WHITE = 0xFFFFFFFF;
    final int COLOR_BACK = 0xFF2D2D61;
    Drawable STYLE_CELL, STYLE_BACK, STYLE_GREEN, STYLE_RED;

    LinearLayout mainLayout;
    TableLayout table;
    String URL = MainActivity.MAIN_URL + "getRating.php";
    Spinner sub_spin, gr_spin;

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
        getRating(sub_spin.getSelectedItemPosition(), gr_spin.getSelectedItem().toString());
    }

    public void getRating(final int subjectNumber, final String group){
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
        StringRequest jsonObjectRequest  = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                JSONObject users = null;
                try {
                    users = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    setTable(users);
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

    public void setTable(JSONObject users) throws JSONException {
        int TextSizeHeader = 26;
        int TextSize = 22;
        if(table.getParent() != null) {
            ((ViewGroup)table.getParent()).removeView(table); // <- fix
        }
        //удаляем предыдущию таблицу
        mainLayout.removeAllViews();
        table.removeAllViews();

        //получаем весь рейтинг
        JSONArray rating = users.getJSONArray("Rating");


        for (int i=0; i < rating.length()+1; i++)
        {
            TableRow row = new TableRow(Rating.this);
            if(i==0)
            {
                //эти переменные нужны для определения количества лаб в заголовке, да да, небольшой костыль
                JSONObject userL = rating.getJSONObject(0);
                String statusL = userL.getString("status");
                JSONArray statusjsonL = new JSONArray(statusL);

                //заголовок таблицы
                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);

                name.setText("  "+getResources().getString(R.string.nickname)+"  ");
                name.setTextColor(COLOR_WHITE);
                name.setTextSize(TextSizeHeader);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackground(STYLE_BACK);
                row.addView(name);

                group.setText("  "+getResources().getString(R.string.group)+"  ");
                group.setTextColor(COLOR_WHITE);
                group.setTextSize(TextSizeHeader);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackground(STYLE_BACK);
                row.addView(group);

                //количество лаб
                for (int k = 0; k < statusjsonL.length(); k++) {
                    TextView lab = new TextView(Rating.this);
                    lab.setText(String.valueOf(k+1));
                    lab.setTextColor(COLOR_WHITE);
                    lab.setTextSize(TextSizeHeader);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    lab.setBackground(STYLE_BACK);
                    row.addView(lab);
                }

                idz.setText("  "+getResources().getString(R.string.IHW)+"  ");
                idz.setTextColor(COLOR_WHITE);
                idz.setTextSize(TextSizeHeader);
                idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                idz.setBackground(STYLE_BACK);
                row.addView(idz);
            }else {
                //row.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                //row.setBackground(ContextCompat.getDrawable(this,R.drawable.borders));
                JSONObject user = rating.getJSONObject(i-1);

                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);

                name.setText(user.getString("NickName"));
                name.setTextColor(COLOR_BACK);
                name.setTextSize(TextSize);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackground(STYLE_CELL);
                row.addView(name);

                group.setText(user.getString("NameGroup"));
                group.setTextColor(COLOR_BACK);
                group.setTextSize(TextSize);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackground(STYLE_CELL);
                row.addView(group);

                //получаем массив boolean[][]
                String status = user.getString("status");
                //statusArray [[true.false],[true.false]]
                JSONArray statusArray = new JSONArray(status);
                boolean st[][] = new boolean[statusArray.length()][2];
                for(int n =0;n<statusArray.length();n++){
                    //statusArrayArray [true,false]
                    JSONArray statusArrayArray = statusArray.getJSONArray(n);
                    for(int p=0;p<2;p++){
                        if(statusArrayArray.getString(p).equals("true")) st[n][p] = true;
                        else st[n][p] = false;

                    }
                }

                //устанавливаем значения сдачи
               for(int n =0;n<statusArray.length();n++){
                    TextView lab = new TextView(Rating.this);

                    if(st[n][0] && st[n][1]) {
                        lab.setBackground(STYLE_GREEN);
                        lab.setText("     ✓     ");
                    }
                    else{
                        lab.setBackground(STYLE_RED);
                        lab.setText("     ╳     ");
                    }
                    lab.setTextSize(TextSize);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    row.addView(lab);
                }

            }
            table.addView(row);
        }

        mainLayout.addView(table);
    }

       protected  void createSpinner()
    {
        sub_spin = findViewById(R.id.subjectSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.subject_arrays,
                R.layout.color_spinner_schedule
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        sub_spin.setAdapter(adapter);
        sub_spin.setOnItemSelectedListener(this);

        gr_spin = findViewById(R.id.gr_rat_spin);

        ArrayList<String> spinnerArray = new ArrayList<String>();

        for (int i = 0; i < MainActivity.GROUPS.length; ++i)
            spinnerArray.add(MainActivity.GROUPS[i].NameGroup);
        spinnerArray.add("ALL");
           
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, R.layout.color_spinner_schedule,spinnerArray);

        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_schedule);
        gr_spin.setAdapter(adapter2);
        gr_spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        getRating(sub_spin.getSelectedItemPosition(), gr_spin.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
