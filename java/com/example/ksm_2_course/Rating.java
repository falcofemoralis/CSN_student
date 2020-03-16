package com.example.ksm_2_course;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class Rating extends AppCompatActivity {
    LinearLayout mainLayout;
    TableLayout table;
    String URL = MainActivity.MAIN_URL + "getRating.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mainLayout = findViewById(R.id.mainLayout);
        table = findViewById(R.id.table);

        //тут береться предмет со спиннера
        //setData() -> getRating(subject) -> если успешно setTable(users)
        setData();

    }

    public void setData(){
        final Spinner subjectSpinner = (Spinner) findViewById(R.id.subjectSpinner);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String subject = subjectSpinner.getSelectedItem().toString();
                getRating(subject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void getRating(final String subject){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                Toast.makeText(Rating.this, "No connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NameGroup", "ALL");
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

        for (int i=0; i < rating.length()+1; i++) {
            TableRow row = new TableRow(Rating.this);
            if(i==0){
                //эти переменные нужны для определения количества лаб в заголовке, да да, небольшой костыль
                JSONObject userL = rating.getJSONObject(0);
                String statusL = userL.getString("status");
                JSONArray statusjsonL = new JSONArray(statusL);

                //заголовок таблицы
                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);

                name.setText("  Имя  ");
                name.setTextSize(TextSizeHeader);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(name);

                group.setText("  Группа  ");
                group.setTextSize(TextSizeHeader);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(group);

                //количество лаб
                for (int k = 0; k < statusjsonL.length(); k++) {
                    TextView lab = new TextView(Rating.this);
                    lab.setText(String.valueOf(k+1));
                    lab.setTextSize(TextSizeHeader);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    lab.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                    row.addView(lab);
                }

                idz.setText("  ИДЗ  ");
                idz.setTextSize(TextSizeHeader);
                idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                idz.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(idz);
            }else {
                //row.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                //row.setBackground(ContextCompat.getDrawable(this,R.drawable.borders));
                JSONObject user = rating.getJSONObject(i-1);

                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);

                name.setText(user.getString("NickName"));
                name.setTextSize(TextSize);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                row.addView(name);

                group.setText(user.getString("NameGroup"));
                group.setTextSize(TextSize);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
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
                    lab.setText("Сдано ");
                    if(st[n][0] && st[n][1]) {
                        lab.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                    }
                    else{
                        lab.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
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
}


