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

        setData();

    }

    public void setData(){
        final Spinner subjectSpinner = (Spinner) findViewById(R.id.subjectSpinner);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String subject = subjectSpinner.getSelectedItem().toString();
                getRating();
                setTable(subject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void setTable(String text){
        if(table.getParent() != null) {
            ((ViewGroup)table.getParent()).removeView(table); // <- fix
        }
        mainLayout.removeAllViews();
        table.removeAllViews();
        for (int i=0; i < 10; i++) {
            TableRow row = new TableRow(Rating.this);
            if(i==0){
                //HEADER
                TextView name = new TextView(Rating.this);
                TextView group = new TextView(Rating.this);
                TextView idz = new TextView(Rating.this);
                int size = 28;

                name.setText("  Имя  ");
                name.setTextSize(size);
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                name.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(name);

                group.setText("  Группа  ");
                group.setTextSize(size);
                group.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                group.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(group);

                for (int k = 0; k < 5; k++) {
                    TextView lab = new TextView(Rating.this);
                    lab.setText(String.valueOf(k+1));
                    lab.setTextSize(size);
                    lab.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    lab.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                    row.addView(lab);
                }

                idz.setText("  ИДЗ  ");
                idz.setTextSize(size);
                idz.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                idz.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                row.addView(idz);
            }else {
                for (int j = 0; j < 8; j++) {
                    TextView tv = new TextView(Rating.this);
                    tv.setText(text);
                    tv.setTextSize(24);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tv.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    row.addView(tv);
                    //row.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                    //row.setBackground(ContextCompat.getDrawable(this,R.drawable.borders));
                }
            }
            table.addView(row);
        }

       mainLayout.addView(table);
    }

    public void getRating(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest jsonObjectRequest  = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject user = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Rating.this, "User not found", Toast.LENGTH_SHORT).show();
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
                parameters.put("group", "ALL");
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}


