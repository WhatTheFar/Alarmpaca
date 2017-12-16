package com.alpaca.alarmpaca.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;

import net.steamcrafted.lineartimepicker.dialog.LinearDatePickerDialog;

import java.util.Calendar;

import static android.graphics.Color.rgb;

public class TaskDetailActivity extends AppCompatActivity {
    EditText titleEditText , noteEditText ;
    CheckBox completeCheckBox;
    TextView dueDateTv;
    LinearDatePickerDialog dialog;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initInstances(savedInstanceState);


    }

    private void initInstances(Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        titleEditText = findViewById(R.id.title_et);
        noteEditText = findViewById(R.id.note_et);
        completeCheckBox = findViewById(R.id.complete_cb);
        dueDateTv = findViewById(R.id.due_date_tv);


        //        Library : https://github.com/code-mc/linear-time-picker
        LinearDatePickerDialog.Builder dialogBuilder = LinearDatePickerDialog.Builder.with(TaskDetailActivity.this)
                .setDialogBackgroundColor(rgb(0,150,136))
                .setButtonColor(rgb(224,242,241))
                .setPickerBackgroundColor(rgb(232,245,233))
                .setTextColor(rgb(0,77,64))
                .setLineColor(rgb(0,0,0))
                .setMinYear(calendar.get(Calendar.YEAR))
                .setMaxYear(calendar.get(Calendar.YEAR) + 100)
                .setButtonCallback(new LinearDatePickerDialog.ButtonCallback() {
                    @Override
                    public void onPositive(DialogInterface dialog, int year, int month, int day) {


                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {

                    }
                });

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (!prefs.getBoolean("isTutorial", false)) {
            dialogBuilder.setShowTutorial(true);
            prefs.edit().putBoolean("isTutorial", true).apply();
        }

        dialog = dialogBuilder.build();


        dueDateTv.setOnClickListener(onDueDateClickListener);


    }

    private final View.OnClickListener onDueDateClickListener = v -> {
        dialog.show();
    };



}
