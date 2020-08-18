
package com.BSLCommunity.CSN_student.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.BSLCommunity.CSN_student.Objects.Groups;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.Objects.Settings;
import java.util.ArrayList;


public class SubjectInfoDialogEditText extends AppCompatDialogFragment {
    private android.widget.EditText EditText;
    private DialogListener listener;
    private String title;
    Button name;
    SubjectInfo.Types type;
    int number;

    SubjectInfoDialogEditText(SubjectInfo.Types type, int number, Button name) {this.type = type; this.number = number; this.name = name;}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_settings_et, null);
        EditText = view.findViewById(R.id.activity_settings_et_dialog);
       // EditText.setText(com.BSLCommunity.CSN_student.Objects.Settings.encryptedSharedPreferences.getString(com.BSLCommunity.CSN_student.Objects.Settings.PrefKeys.NICKNAME.getKey(), ""));
        title = type.toString();

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
                        String text = EditText.getText().toString();
                        listener.applyText(text, type, number, name);
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
        void applyText(String text, SubjectInfo.Types type, int number, Button name);
    }
}