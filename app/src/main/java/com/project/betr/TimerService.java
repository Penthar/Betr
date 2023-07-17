package com.project.betr;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class TimerService extends Service {
    private int currentTime;
    private Thread timerThread;
    private int maxTime;
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder statusBarNotification;
    private static final String CHANNEL_ID = "TimerChannel";
    ArrayList<Exercise> exerciseList;


    @Override
    public void onCreate() {
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        statusBarNotification  =new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        super.onCreate();
    }
    public void createNotification(){

        statusBarNotification.setContentTitle("EXERCISE")

                .setSmallIcon(R.drawable.baseline_timer_24)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        statusBarNotification.setProgress(1, 1, false);
        startForeground(1, statusBarNotification.build());
    }
    public void updateNotification(int currentTime, int i, int reps, int weight, String exercise_name){ //i is indicator for working set or rest

        if(currentTime > 0) {
            statusBarNotification.setProgress(this.maxTime, currentTime, false);
            if(i==0){
                statusBarNotification.setContentTitle("START")
                        .setContentText(getTimeInFormat(currentTime) + " start " + exercise_name + " for " + reps + " reps " + "with " + weight + " kgs")
                        .setSmallIcon(R.drawable.baseline_timer_24)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
            }
            else{
                statusBarNotification.setContentTitle("REST")
                        .setContentText(getTimeInFormat(currentTime) + " Next: " + exercise_name + " for " + reps + " reps " + "with " + weight + " kgs")
                        .setSmallIcon(R.drawable.baseline_timer_24)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
            }
            notificationManager.notify(1, statusBarNotification.build());
        }
        else
            notificationManager.cancel(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            exerciseList = (ArrayList<Exercise>) intent.getSerializableExtra("EXERCISES");
        }
        createNotification();
        if (timerThread == null || !timerThread.isAlive()) {
            timerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < exerciseList.size(); i++) {//for each exercise
                        for (int j = 0; j < exerciseList.get(i).getSets(); j++) {//for each set
                            for (int k = 0; k < 2; k++) {///either rest or working set
                                if (k == 0)
                                    maxTime = getTimeInMilis(exerciseList.get(i).getSet_duration());
                                else
                                    maxTime = getTimeInMilis(exerciseList.get(i).getRest_time());
                                currentTime = maxTime;
                                Log.d("CHECKTIMER", "onStartCommand: " + exerciseList.get(i).getSet_duration() + "AAAA" + exerciseList.get(i).getRest_time() + "AAAA" + maxTime);
                                Exercise currentExercise = exerciseList.get(i);
                                while (currentTime > 0) {
                                    currentTime -= 1000;
                                    if(j<(currentExercise.getSets()-1) || k==0)//have more sets in same exercise left
                                        updateNotification(currentTime, k, currentExercise.getReps(), currentExercise.getWeight(), currentExercise.getName());
                                    else{//next set is next exercise
                                        if(i == exerciseList.size()-1 && j == exerciseList.get(i).getSets()-1 && k==1)
                                            notificationManager.cancel(1);
                                        else{
                                            updateNotification(currentTime, k, exerciseList.get(i+1).getReps(), exerciseList.get(i+1).getWeight(), exerciseList.get(i+1).getName());
                                        }
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                        }
                                    }
                                }
                            }
                        });

                        timerThread.start();

                    }

        stopForeground(true);
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // if(timerThread.isAlive())
          //  timerThread.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private int getTimeInMilis(int totalTime){
        int minutes = totalTime/100;
        int seconds = totalTime%100;
        return (minutes * 60 + seconds) * 1000;
        /// time is currently displayed in Minutes:Seconds. this method converts it to miliseconds.
    }
    public String getTimeInFormat(int timeMillis) {
        int totalSeconds = timeMillis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String minutesStr = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String secondsStr = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);

        return minutesStr + ":" + secondsStr;
        //convert time to Minutes:Seconds
    }
}

