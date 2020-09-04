package com.BSLCommunity.CSN_student.Activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.BSLCommunity.CSN_student.Objects.AnotherUserList;
import com.BSLCommunity.CSN_student.R;

public class RatingUsersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_users_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        drawUsersList();
    }

    protected void drawUsersList() {
        LinearLayout listLL = findViewById(R.id.activity_rating_users_list_ll_list_users);

        for (int i = 0; i < AnotherUserList.users.size(); ++i) {
            LinearLayout userLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.inflate_preview_user, listLL, false);
            Button bt = (Button) userLayout.getChildAt(0);
            bt.setText(AnotherUserList.users.get(i).nickName);
            listLL.addView(userLayout);
        }
    }

}
