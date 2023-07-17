package com.project.betr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Update_Workout_Activity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    TableLayout tableLayout;
    Button addRowButton;
    Button saveButton;
    EditText workoutNameText;
    ArrayList[] matrix;
    ArrayList<Button> removeRowList;
    ArrayList<Exercise> exerciseList;
    ArrayList<TableRow> rowsList;
    DBHelper dbHelper;
    FirebaseAuth fbauth;
    User currentUser;
    String workoutID;
    Workout currentWorkout;
    Button deleteExercise;
    AlertDialog.Builder builder;
    ArrayList<Button> graphButtons;
    boolean weightcheck;
    TextView codeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_workout);
        fbauth = FirebaseAuth.getInstance();
        ProgressBar progressBar = findViewById(R.id.prog_bar);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        workoutID = intent.getStringExtra("WORKOUT ID");
        deleteExercise = findViewById(R.id.delete_exercise);
        deleteExercise.setOnClickListener(this);
        codeText = findViewById(R.id.codeCopyText);
        if(fbauth.getCurrentUser() == null){
            Intent intent2 = new Intent(Update_Workout_Activity.this, MainActivity.class);
            startActivity(intent2);
            finish(); return;
        }
        tableLayout = findViewById(R.id.Table);
        addRowButton = findViewById(R.id.addRowButton);
        workoutNameText = findViewById(R.id.name_edit);
        addRowButton.setOnClickListener(this);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        init_matrix();
        exerciseList = new ArrayList<Exercise>();
        removeRowList = new ArrayList<>();
        rowsList = new ArrayList<>();
        builder = new AlertDialog.Builder(this);
        graphButtons = new ArrayList<>();
        dbHelper = new DBHelper();
        new DBHelper().getUserFromUID(fbauth.getUid(), new DBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                Log.d("CHECKNULL2", "DataIsLoaded: " + (user==null));
                currentUser = new User(user);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                progressBar.setVisibility(View.GONE);
                currentWorkout = getCurrentWorkout();
                populateRows();
                codeText.setText("CODE: " + currentWorkout.getID());
            }
            @Override
            public void DataIsInserted(boolean success) {

            }
            @Override
            public void getWorkout(Workout workout) {

            }
            @Override
            public void DataIsDeleted() {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == addRowButton){
            addRowButton.setError(null);
            addRow(null, false);
        }
        else if (view == saveButton) {
            if(!check_matrix_empty()){
                for (int i = 0; i < matrix[0].size(); i++) {
                    row_To_Exercise(i);
                }
                dbHelper.set_all_exercise_to_inactive(currentUser.getUserID(), currentWorkout.getID(), create_workout());
                //dbHelper.updateUserWorkoutList(fbauth.getUid(), create_workout());
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                finish(); return;
            }
        }
        else if(view == deleteExercise){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("DELETE");
            builder.setMessage("Are you sure you want to delete this workout?");

// Positive Button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHelper.deleteWorkout(currentUser.getUserID(), workoutID);
                    Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    finish(); return;
                }
            });

// Negative Button
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

// Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            for (int i = 0; i < removeRowList.size(); i++) {
                if(view == graphButtons.get(i)){
                    create_graph_dialog(((View)graphButtons.get(i).getParent()).getTag().toString());
                }
            }
            for (int i = 0; i < removeRowList.size(); i++) {
                if(view == removeRowList.get(i))
                    removeRow(i);
            }

        }
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
    private void addRow(String exercise_id, boolean addGraph){
        TableRow tableRow = new TableRow(this);
        tableRow.setTag(exercise_id);
        for (int i = 0; i < 4; i++) {
            EditText editBlock = new EditText(this);
            editBlock.setPadding(dpToPx(10), 0, dpToPx(10), 0);
            editBlock.setHeight(dpToPx(40));
            editBlock.setBackground(getDrawable(R.drawable.table_block_border));
            tableRow.addView(editBlock);
            matrix[i].add(editBlock);
        }
        TimerPicker picker1 = new TimerPicker(this);
        picker1.setHeight(dpToPx(40));
        picker1.setBackground(getDrawable(R.drawable.table_block_border));
        tableRow.addView(picker1);
        TimerPicker picker2 = new TimerPicker(this);
        picker2.setHeight(dpToPx(40));
        matrix[4].add(picker1);
        matrix[5].add(picker2);
        tableRow.addView(picker2);
        Category_Spinner spinner = new Category_Spinner(this);
        spinner.setMinimumHeight(dpToPx(40));
        tableRow.addView(spinner);
        matrix[6].add(spinner);
        Button removeRowButton = new Button(this);
        removeRowButton.setBackgroundResource(R.drawable.remove_row_block);
        removeRowButton.setOnClickListener(this);
        tableRow.addView(removeRowButton);
        removeRowButton.setHeight(dpToPx(40));
        removeRowList.add(removeRowButton);
        tableLayout.addView(tableRow);
        rowsList.add(tableRow);
        /////
        Button graphButton = new Button(this);
        graphButton.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        graphButton.setHeight(dpToPx(40));
        graphButton.setBackground(getDrawable(R.drawable.table_block_border));
        graphButton.setOnClickListener(this);
        graphButton.setTag(rowsList.size()-1);
        tableRow.addView(graphButton);
        graphButton.setBackgroundResource(R.drawable.graph_block);
        if(!addGraph)
            graphButton.setClickable(false);
        graphButtons.add(graphButton);
        //////

    }
    private void init_matrix(){
        matrix = new ArrayList[7];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new ArrayList<View>();
        }

    }
    private boolean check_matrix_empty() {
        boolean exists_empty = false;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].size(); j++) {
                if (matrix[i].get(j) instanceof TimerPicker) {
                    if (((TimerPicker) matrix[i].get(j)).getSelectedMinute() == 0 && ((TimerPicker) matrix[i].get(j)).getSelectedSecond() == 0) {
                        ((TimerPicker) matrix[i].get(j)).setError("Set Time!");
                        exists_empty = true;

                    }
                } else if (!(matrix[i].get(j) instanceof Category_Spinner)) {
                    if (TextUtils.isEmpty(((EditText) matrix[i].get(j)).getText().toString())) {
                        ((EditText) matrix[i].get(j)).setError("Empty!");
                        exists_empty = true;
                    }
                    if (i == 1 || i == 3 || i==2) {
                        if (!TextUtils.isDigitsOnly(((EditText) matrix[i].get(j)).getText().toString())) {
                            ((EditText) matrix[i].get(j)).setError("Must Be A Number!");
                            exists_empty = true;
                        }
                    }
                }
            }
        }
        if(TextUtils.isEmpty(workoutNameText.getText().toString())){
            workoutNameText.setError("Empty!");
            exists_empty = true;
        } else if (workoutNameText.getText().toString().equals("Temp")) {
            workoutNameText.setError("Cant have that name!");
            exists_empty = true;
        }
        if(matrix[0].size() == 0){
            addRowButton.setError("Must have atleast one exercise");
            exists_empty = true;
        }
        return exists_empty;
        //checks for every block in table if it is empty, if exists empty return true. also returns true if not integer in weight or sets category.
    }

    private void row_To_Exercise(int rowNum){
        String name;
        int weight;
        int sets;
        int reps;
        int rest_time;
        int set_duration;
        String exercise_category;

        name = ((EditText)matrix[0].get(rowNum)).getText().toString();
        weight = Integer.parseInt(((EditText)matrix[1].get(rowNum)).getText().toString());
        reps = Integer.parseInt(((EditText)matrix[2].get(rowNum)).getText().toString());
        sets = Integer.parseInt(((EditText)matrix[3].get(rowNum)).getText().toString());
        rest_time = ((TimerPicker)matrix[4].get(rowNum)).getSelectedMinute()*100 + ((TimerPicker)matrix[4].get(rowNum)).getSelectedSecond();
        set_duration = ((TimerPicker)matrix[5].get(rowNum)).getSelectedMinute()*100 + ((TimerPicker)matrix[5].get(rowNum)).getSelectedSecond();
        exercise_category = ((Category_Spinner)matrix[6].get(rowNum)).getCategory();
        Exercise new_exercise = new Exercise(name, weight, reps, sets, rest_time, set_duration, exercise_category, 1);
        new_exercise.calculateExercise_score();
        if(rowsList.get(rowNum).getTag()!=null){
            new_exercise.setId(rowsList.get(rowNum).getTag().toString());
        }
        else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            String uniqueId = ref.push().getKey();
            new_exercise.setId(uniqueId);

        }
        new_exercise.setDate(new Date(System.currentTimeMillis()));
        exerciseList.add(new_exercise);
    }
    public Workout create_workout(){
        Workout newWorkout = new Workout(workoutNameText.getText().toString(), exerciseList);
        newWorkout.setID(workoutID);
        return newWorkout;
    }
    private  void removeRow(int rowNum){
        for (int i = 0; i < matrix.length; i++) {
            matrix[i].remove(rowNum);
        }
        removeRowList.remove((rowNum));
        tableLayout.removeView(rowsList.get(rowNum));
        rowsList.remove(rowNum);
        graphButtons.remove(rowNum);
    }
    private Workout getCurrentWorkout(){
        for (int i = 0; i < currentUser.getWorkoutList().size(); i++) {

            if(currentUser.getWorkoutList().get(i).getID() != null && currentUser.getWorkoutList().get(i).getID().equals(workoutID))
                return currentUser.getWorkoutList().get(i);
        }
        try {
            throw new Exception("WORKOUT ID NOT MATCHING WORKOUTS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void populateRows(){
        workoutNameText.setText(currentWorkout.getWorkout_Name());
        ArrayList<Exercise> currentExerciseList = new ArrayList<>();
        for (int i = 0; i < currentWorkout.getExerciseList().size(); i++) {
            if(currentWorkout.getExerciseList().get(i).isActive()==1)
                currentExerciseList.add(currentWorkout.getExerciseList().get(i));
        }
        for (int i = 0; i < currentExerciseList.size(); i++) {
            addRow(currentExerciseList.get(i).getId(), true);
            for (int j = 0; j < 7; j++) {
                switch (j) {
                    case 0:
                        ((EditText)matrix[j].get(i)).setText(currentExerciseList.get(i).getName());
                        break;
                    case 1:
                        ((EditText)matrix[j].get(i)).setText(String.valueOf(currentExerciseList.get(i).getWeight()));
                        break;
                    case 2:
                        ((EditText)matrix[j].get(i)).setText(String.valueOf(currentExerciseList.get(i).getReps()));
                        break;
                    case 3:
                        ((EditText)matrix[j].get(i)).setText(String.valueOf(currentExerciseList.get(i).getSets()));
                        break;
                    case 4:
                        ((TimerPicker)matrix[j].get(i)).setPicker(currentExerciseList.get(i).getRest_time());
                        break;
                    case 5:
                        ((TimerPicker)matrix[j].get(i)).setPicker(currentExerciseList.get(i).getSet_duration());
                        break;
                    case 6:
                        ((Category_Spinner)matrix[j].get(i)).setCategory(currentExerciseList.get(i).getExercise_category());
                    default:
                        Log.d("ERROR", "create_block: ");
                        break;
                }
            }
        }
    }
    @SuppressLint("ResourceType")
    public void create_graph_dialog(String exerciseID){
        weightcheck = true;
        ArrayList<Exercise> progression = new ArrayList<>();
        for (int i = 0; i < currentUser.getWorkoutList().size(); i++) {
            if(currentUser.getWorkoutList().get(i).getID().equals(workoutID)){
                for (int j = 0; j < currentUser.getWorkoutList().get(i).getExerciseList().size(); j++) {
                    if(currentUser.getWorkoutList().get(i).getExerciseList().get(j).getId().equals(exerciseID))//get all exercises with same id from this workout
                        progression.add(currentUser.getWorkoutList().get(i).getExerciseList().get(j));
                }
            }

        }
        /////create the starting dialog
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton weightButton = new RadioButton(this);
        weightButton.setText("Weight");
        weightButton.setId(1);
        radioGroup.addView(weightButton);
        RadioButton scoreButton = new RadioButton(this);
        scoreButton.setText("Score");
        scoreButton.setId(2);
        radioGroup.addView(scoreButton);
        builder.setTitle("Your Progress");
        radioGroup.setGravity(Gravity.CENTER_HORIZONTAL);
        radioGroup.setOnCheckedChangeListener(this);
        builder.setView(radioGroup);
        weightButton.setChecked(true);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();

        AlertDialog finalDialog = dialog;
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int indicator) {
                finalDialog.dismiss();
                if(progression.size()>0) {
                    builder.setTitle("YOUR PROGRESSION")
                            .setCancelable(true);
                    GraphView graphView = new GraphView(getApplicationContext());
                    DataPoint[] points = new DataPoint[progression.size() + 1];
                    GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
                    gridLabel.setHorizontalAxisTitle("Date");
                    ///////
                    graphView.getViewport().setScalable(true);
                    graphView.getViewport().setScrollable(true);
                    graphView.getViewport().setScalableY(true);
                    graphView.getViewport().setScrollableY(true);
                    ///////
                    graphView.getViewport().setXAxisBoundsManual(true);
                    graphView.getViewport().setYAxisBoundsManual(true);
                    if(weightcheck){
                        Collections.sort(progression, new Comparator<Exercise>() {
                            @Override
                            public int compare(Exercise exercise1, Exercise exercise2) {
                                return exercise1.getDate().compareTo(exercise2.getDate());
                            }
                        });
                        for (int i = 0; i < progression.size(); i++) {
                            points[i] = new DataPoint(progression.get(i).getDate(), progression.get(i).getWeight());
                        }
                        points[points.length-1] = new DataPoint(new Date(System.currentTimeMillis() + 3200000),
                                progression.get(progression.size()-1).getWeight());
                        gridLabel.setVerticalAxisTitle("Weight");
                        graphView.getViewport().setMaxY(getLargestWeight(progression)*1.3);
                    }
                    else{
                        Collections.sort(progression, new Comparator<Exercise>() {
                            @Override
                            public int compare(Exercise exercise1, Exercise exercise2) {
                                return exercise1.getDate().compareTo(exercise2.getDate());
                            }
                        });
                        for (int i = 0; i < progression.size(); i++) {
                            points[i] = new DataPoint(progression.get(i).getDate(), progression.get(i).getExercise_score());
                        }
                        points[points.length-1] = new DataPoint(new Date(System.currentTimeMillis() + 3200000),
                                progression.get(progression.size()-1).getExercise_score());
                        gridLabel.setVerticalAxisTitle("Score");
                        graphView.getViewport().setMaxY(getLargestScore(progression) * 1.3);
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                    graphView.addSeries(series);
                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
                    graphView.getViewport().setMinX(progression.get(0).getDate().getTime());

                    graphView.getViewport().setMaxX(System.currentTimeMillis() + (6400000) * daysBetweenDates(progression.get(0).getDate(), new Date(System.currentTimeMillis() + 3200000)));
                    graphView.getViewport().setMinY(0);
                    graphView.getGridLabelRenderer().setHumanRounding(false);
                    series.setDrawDataPoints(true);
                    graphView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.addView(graphView);
                    builder.setView(layout);
                    builder.setPositiveButton(null, null);
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
                else {
                    builder.setTitle("YOUR PROGRESSION")
                            .setCancelable(true)
                            .setMessage("new exercise, no progression yet.");
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
            }
        });
        dialog = builder.create();
        dialog.show();


    }
    public int daysBetweenDates(Date startDate, Date endDate) {
        long diffInMilliseconds = endDate.getTime() - startDate.getTime();
        Log.d("finalcheck1", "daysBetweenDates: " + diffInMilliseconds);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);
        int roundedUpDays = (int) Math.ceil(diffInDays);
        Log.d("finalcheck", "daysBetweenDates: " + roundedUpDays);
        if(roundedUpDays == 0)
            roundedUpDays = 1;
        return roundedUpDays;
    }
    public int getLargestWeight(ArrayList<Exercise> exerciseList) {
        if (exerciseList.isEmpty()) {
            throw new IllegalArgumentException("The exerciseList is empty.");
        }

        int largestWeight = exerciseList.get(0).getWeight(); // Assume first weight as largest

        for (Exercise exercise : exerciseList) {
            int weight = exercise.getWeight();
            if (weight > largestWeight) {
                largestWeight = weight;
            }
        }

        return largestWeight;
    }
    public double getLargestScore(ArrayList<Exercise> exerciseList) {
        if (exerciseList.isEmpty()) {
            throw new IllegalArgumentException("The exerciseList is empty.");
        }

        double largestWeight = exerciseList.get(0).getExercise_score(); // Assume first weight as largest

        for (Exercise exercise : exerciseList) {
            double weight = exercise.getExercise_score();
            if (weight > largestWeight) {
                largestWeight = weight;
            }
        }

        return largestWeight;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        weightcheck = checkedId == 1;
    }
}