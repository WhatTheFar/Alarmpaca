package com.alpaca.alarmpaca.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.RealmTasks;

import net.steamcrafted.lineartimepicker.dialog.LinearDatePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static android.graphics.Color.rgb;

public class TaskDetailActivity extends AppCompatActivity {

    EditText titleEditText, noteEditText;
    TextView dueDateTv;
    LinearDatePickerDialog dialog;

    FloatingTextButton cancelBtn;
    FloatingTextButton saveBtn;

    private Date due;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initInstances();
    }

    private void initInstances() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Calendar calendar = Calendar.getInstance();
        titleEditText = findViewById(R.id.title_et);
        noteEditText = findViewById(R.id.note_et);
        dueDateTv = findViewById(R.id.due_date_tv);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(onSaveClickListener);
        cancelBtn.setOnClickListener(onCancelClickListener);

        //        Library : https://github.com/code-mc/linear-time-picker
        LinearDatePickerDialog.Builder dialogBuilder = LinearDatePickerDialog.Builder.with(TaskDetailActivity.this)
                .setDialogBackgroundColor(rgb(0, 150, 136))
                .setButtonColor(rgb(224, 242, 241))
                .setPickerBackgroundColor(rgb(232, 245, 233))
                .setTextColor(rgb(0, 77, 64))
                .setLineColor(rgb(0, 0, 0))
                .setMinYear(calendar.get(Calendar.YEAR))
                .setMaxYear(calendar.get(Calendar.YEAR) + 100)
                .setButtonCallback(linearDatePickerButtonCallback);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (!prefs.getBoolean("isTutorial", false)) {
            dialogBuilder.setShowTutorial(true);
            prefs.edit().putBoolean("isTutorial", true).apply();
        }

        dialog = dialogBuilder.build();

        dueDateTv.setOnClickListener(onDueDateClickListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener onDueDateClickListener = v -> dialog.show();

    private final LinearDatePickerDialog.ButtonCallback linearDatePickerButtonCallback = new LinearDatePickerDialog.ButtonCallback() {
        @Override
        public void onPositive(DialogInterface dialog, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month-1, day);
            dueDateTv.setText(DateFormat.getDateInstance().format(calendar.getTime()));
            due = calendar.getTime();
        }

        @Override
        public void onNegative(DialogInterface dialog) {

        }
    };

    private final View.OnClickListener onSaveClickListener = v -> {

        String title = titleEditText.getText().toString();
        String note = noteEditText.getText().toString();
        Date dueDate = due;

        RealmTasks task = new RealmTasks(RealmTasks.NEW_TASK_ID, title, note, "needsAction", dueDate);

        Log.wtf("Task", "New task : " + task.getId());

        Intent taskIntent = new Intent();
        taskIntent.putExtra("task", task);

        setResult(RESULT_OK, taskIntent);

        finish();

    };

    private final View.OnClickListener onCancelClickListener = v -> finish();

}
