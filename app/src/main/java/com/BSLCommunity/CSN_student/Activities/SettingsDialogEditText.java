package com.BSLCommunity.CSN_student.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.Settings;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;
import java.util.List;

public class SettingsDialogEditText extends AppCompatDialogFragment {
    private EditText EditText;
    private DialogListener listener;
    private String title;
    private int applyKey;
    private Spinner groupSpinner;
;
    SettingsDialogEditText(int key) {
        applyKey = key;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;

        switch (applyKey) {
            case R.id.activity_settings_ll_nickname:
                view = inflater.inflate(R.layout.dialog_settings_et, null);
                EditText = view.findViewById(R.id.activity_settings_et_dialog);
                EditText.setText(Settings.encryptedSharedPreferences.getString(Settings.PrefKeys.NICKNAME.getKey(), ""));
                title = getResources().getString(R.string.nickname);
                break;
            case R.id.activity_settings_ll_password:
                view = inflater.inflate(R.layout.dialog_settings_et, null);
                EditText = view.findViewById(R.id.activity_settings_et_dialog);
                EditText.setText(Settings.encryptedSharedPreferences.getString(Settings.PrefKeys.PASSWORD.getKey(), ""));
                title = getResources().getString(R.string.password);
                break;
            case R.id.activity_settings_ll_group:
                view = inflater.inflate(R.layout.dialog_settings_sp, null);

                ProgressBar groupProgressBar = view.findViewById(R.id.activity_settings_pb_groups);
                Sprite iIndeterminateDrawable = new ThreeBounce();
                iIndeterminateDrawable.setColor(getContext().getColor(R.color.main_color_3));
                groupProgressBar.setIndeterminateDrawable(iIndeterminateDrawable);

                try {
                    groupSpinner = view.findViewById(R.id.activity_settings_sp_groups);
                    //создаем лист групп
                    List<String> groupsAdapter = new ArrayList<String>();
                    if (Groups.groupsLists.size() != 0) {
                        groupProgressBar.setVisibility(View.GONE);

                        //добавляем в массив из класса Groups группы
                        for (int j = 0; j < Groups.groupsLists.size(); ++j)
                            groupsAdapter.add(Groups.groupsLists.get(j).GroupName);
                    }

                    //устанавливаем спинер выбора групп
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_dropdown_settings, groupsAdapter);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);
                    groupSpinner.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                title = getResources().getString(R.string.group);
                break;
            default:
                view = inflater.inflate(R.layout.dialog_settings_et, null);
                title = "";
        }

        builder.setView(view)
                .setTitle(title)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text;
                        switch (applyKey) {
                            case R.id.activity_settings_ll_nickname:
                                text = EditText.getText().toString();
                                break;
                            case R.id.activity_settings_ll_password:
                                text = EditText.getText().toString();
                                break;
                            case R.id.activity_settings_ll_group:
                                text = groupSpinner.getSelectedItem().toString();
                                break;
                            default:
                                text = "";
                        }

                        listener.applyText(text, applyKey);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void applyText(String text, int key);
    }
}
