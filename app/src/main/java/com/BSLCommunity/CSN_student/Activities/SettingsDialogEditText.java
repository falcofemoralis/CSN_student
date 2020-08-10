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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.User;

import java.util.ArrayList;

public class SettingsDialogEditText extends AppCompatDialogFragment {
    private EditText EditText;
    private DialogListener listener;
    private String title;
    private int applyKey;
    private Spinner groupSpinner;

    SettingsDialogEditText(int KEY) {
        applyKey = KEY;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;

        switch (applyKey) {
            case R.id.nickname:
                view = inflater.inflate(R.layout.dialog_settings, null);
                EditText = view.findViewById(R.id.editText_dialog);
                EditText.setText(Settings.encryptedSharedPreferences.getString(Settings.KEY_NICKNAME, ""));
                title = getResources().getString(R.string.nickname);
                break;
            case R.id.password:
                view = inflater.inflate(R.layout.dialog_settings, null);
                EditText = view.findViewById(R.id.editText_dialog);
                EditText.setText(Settings.encryptedSharedPreferences.getString(Settings.KEY_PASSWORD, ""));
                title = getResources().getString(R.string.password);
                break;
            case R.id.group:
                view = inflater.inflate(R.layout.dialog_settings2, null);
                try {
                    groupSpinner = view.findViewById(R.id.group);
                    ArrayList<String> spinnerArray = new ArrayList<String>();
                    for (int i = 0; i < User.GROUPS.length; ++i)
                        spinnerArray.add(User.GROUPS[i].GroupName);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getContext(), R.layout.spinner_dropdown_settings, spinnerArray);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);
                    groupSpinner.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                title = getResources().getString(R.string.group);
                break;
            default:
                view = inflater.inflate(R.layout.dialog_settings, null);
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
                            case R.id.nickname:
                                text = EditText.getText().toString();
                                break;
                            case R.id.password:
                                text = EditText.getText().toString();
                                break;
                            case R.id.group:
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
