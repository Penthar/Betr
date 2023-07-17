package com.project.betr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TimerActivity extends Activity implements View.OnClickListener {
    Button startTimer;
    TextView workoutName;
    ArrayList<Exercise> exerciseList;
    TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Intent intent = getIntent();
        workoutName = findViewById(R.id.workout_name);
        startTimer = findViewById(R.id.startTimer);
        startTimer.setOnClickListener(this);
        workoutName.setText(intent.getStringExtra("WORKOUT NAME"));
        exerciseList = (ArrayList<Exercise>) intent.getSerializableExtra("EXERCISE LIST");
        tableLayout = findViewById(R.id.Table);
        populate_table();

    }

    @Override
    public void onClick(View view) {
        createNotificationChannel();
        Intent intent = new Intent(TimerActivity.this, TimerService.class);
        intent.putExtra("EXERCISES", exerciseList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        startTimer.setClickable(false);
    }
    private void populate_table(){
        for (int i = 0; i < exerciseList.size(); i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 0; j < 7; j++) {
                tableRow.addView(create_block(i,j));
            }
            tableLayout.addView(tableRow);
        }
    }
    private TextView create_block(int exercise_indicator, int block_indicator){
        TextView textBlock = new TextView(this);
        textBlock.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        textBlock.setBackground(getDrawable(R.drawable.table_block_border));
        switch (block_indicator) {
            case 0:
                textBlock.setText(exerciseList.get(exercise_indicator).getName());
                break;
            case 1:
                textBlock.setText(String.valueOf(exerciseList.get(exercise_indicator).getWeight()));
                break;
            case 2:
                textBlock.setText(String.valueOf(exerciseList.get(exercise_indicator).getReps()));
                break;
            case 3:
                textBlock.setText(String.valueOf(exerciseList.get(exercise_indicator).getSets()));
                break;
            case 4:
                textBlock.setText(formatTime(exerciseList.get(exercise_indicator).getRest_time()));
                break;
            case 5:
                textBlock.setText(formatTime(exerciseList.get(exercise_indicator).getSet_duration()));
                break;
            case 6:
                textBlock.setText(exerciseList.get(exercise_indicator).getExercise_category());
            default:
                Log.d("ERROR", "create_block: ");
                break;
        }
        textBlock.setHeight(dpToPx(40));
        return textBlock;
    }

    private int dpToPx(int dps)
    {
// Get the screen's density scale
        final float scale =

                getResources().getDisplayMetrics().density;
// Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    public String formatTime(int time) {
        int minutes = time / 100; // Extract minutes from the input
        int seconds = time % 100; // Extract seconds from the input

        // Check if minutes or seconds are less than 10, and add leading zero if necessary
        String formattedMinutes = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String formattedSeconds = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);

        // Format the time as MM:SS
        return formattedMinutes + ":" + formattedSeconds;
    }

    private void createNotificationChannel() {
// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer Notifications";
            String description = "displays workout timers";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel("TimerChannel", name, importance);
            channel.setDescription(description);
// Register the channel with the system; you can't change the importance
// or other notification behaviors after this
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}