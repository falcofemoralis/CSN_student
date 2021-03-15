package com.BSLCommunity.CSN_student.Views;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
/*        LinearLayout listLL = findViewById(R.id.activity_rating_users_list_ll_list_users);

        for (int i = 0; i < AnotherUserList.users.size(); ++i) {

            AnotherUserList.AnotherUser user =  AnotherUserList.users.get(i);

            LinearLayout userLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.inflate_preview_user, listLL, false);
            RelativeLayout userRL = (RelativeLayout) userLayout.getChildAt(0);

            ((TextView) userRL.getChildAt(0)).setText(user.nickName);
            ((TextView) userRL.getChildAt(1)).setText("Real name : " + user.realName);
            ((TextView) userRL.getChildAt(2)).setText( "Group : " + user.groupName);

            listLL.addView(userLayout);
        }*/
    }

}
