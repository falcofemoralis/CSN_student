package com.BSLCommunity.CSN_student.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.BSLCommunity.CSN_student.Managers.LocaleHelper;
import com.BSLCommunity.CSN_student.R;

import java.util.List;

import static com.BSLCommunity.CSN_student.Models.Settings.languages;

public class SettingsDialogEditText extends AppCompatDialogFragment {
    private EditText EditText;
    private DialogListener listener;
    private String title;
    private int applyKey;
    private Spinner groupSpinner, languageSpinner;

    String nickName, password;
    List<String> groupsAdapter;

    SettingsDialogEditText(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }

    public void setApplyKey(int applyKey) {
        this.applyKey = applyKey;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;

        if (applyKey == R.id.activity_settings_ll_nickname) {
            view = inflater.inflate(R.layout.dialog_settings_et_nickname, null);
            EditText = view.findViewById(R.id.activity_settings_et_dialog);
            EditText.setText(this.nickName);
            title = getResources().getString(R.string.nickname);
        }
        else if (applyKey == R.id.activity_settings_ll_password) {
            view = inflater.inflate(R.layout.dialog_settings_et_password, null);
            EditText = view.findViewById(R.id.activity_settings_et_dialog);
            EditText.setText(this.password);
            title = getResources().getString(R.string.password);
        }
        else if  (applyKey == R.id.activity_settings_ll_group) {
            // TODO
            view = inflater.inflate(R.layout.dialog_settings_sp_groups, null);
//            ProgressBar groupProgressBar = view.findViewById(R.id.activity_settings_pb_groups);
//            Sprite iIndeterminateDrawable = new ThreeBounce();
//            iIndeterminateDrawable.setColor(getContext().getColor(R.color.main_color_3));
//            groupProgressBar.setIndeterminateDrawable(iIndeterminateDrawable);
//
//            try {
//                groupSpinner = view.findViewById(R.id.activity_settings_sp_groups);
//
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_dropdown_settings, groupsAdapter);
//                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);
//                groupSpinner.setAdapter(dataAdapter);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            title = getResources().getString(R.string.group);
        }
        else if (applyKey == R.id.activity_settings_ll_language) {
            view = inflater.inflate(R.layout.dialog_settings_sp_languages, null);

            try {
                languageSpinner = view.findViewById(R.id.activity_settings_sp_languages);

                //устанавливаем спинер выбора групп
                ArrayAdapter  languagesAdapter = ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.languages,
                        R.layout.spinner_dropdown_settings
                );
                languagesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_settings);
                languageSpinner.setAdapter(languagesAdapter);

                for (Pair<String,String> element : languages){
                    if (element.second.contains(LocaleHelper.getLanguage(getContext()))){
                        languageSpinner.setSelection(languages.indexOf(element));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            title = getResources().getString(R.string.language);
        }
        else {
            view = inflater.inflate(R.layout.dialog_settings_et_nickname, null);
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
                        if (applyKey == R.id.activity_settings_ll_nickname || applyKey == R.id.activity_settings_ll_password) {
                            text = EditText.getText().toString();
                        }
                        else if (applyKey == R.id.activity_settings_ll_group) {
                            text = groupSpinner.getSelectedItem().toString();
                        }
                        else if (applyKey == R.id.activity_settings_ll_language) {
                            text = Integer.toString(languageSpinner.getSelectedItemPosition());
                        }
                        else {
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
