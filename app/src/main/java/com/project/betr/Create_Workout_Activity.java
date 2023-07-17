package com.project.betr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class Create_Workout_Activity extends AppCompatActivity implements View.OnClickListener{
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_workout);
        fbauth = FirebaseAuth.getInstance();
        if(fbauth.getCurrentUser() == null){
            Intent intent = new Intent(Create_Workout_Activity.this, MainActivity.class);
            startActivity(intent);
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

        dbHelper = new DBHelper();

    }

    @Override
    public void onClick(View view) {
        if(view == addRowButton){
            addRowButton.setError(null);
            addRow();
        }
        else if (view == saveButton) {
            if(!check_matrix_empty()){
                for (int i = 0; i < matrix[0].size(); i++) {
                    row_To_Exercise(i);
                }
                dbHelper.addWorkoutToUser(fbauth.getUid(), create_workout(), new DBHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(User user) {

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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show();
                finish(); return;
            }

        }
        else{
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
    private void addRow(){
        TableRow tableRow = new TableRow(this);

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
            /////
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            String uniqueId = ref.push().getKey();
            new_exercise.setId(uniqueId);
            new_exercise.setDate(new Date(System.currentTimeMillis()));
            new_exercise.calculateExercise_score();
            //////
            exerciseList.add(new_exercise);
        }
        public Workout create_workout(){
        Workout newWorkout = new Workout(workoutNameText.getText().toString(), exerciseList);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        String uniqueId = ref.push().getKey();
        newWorkout.setID(uniqueId);
            return newWorkout;
        }
        private  void removeRow(int rowNum){
            for (int i = 0; i < matrix.length; i++) {
                matrix[i].remove(rowNum);
            }
            removeRowList.remove((rowNum));
            tableLayout.removeView(rowsList.get(rowNum));
            rowsList.remove(rowNum);
        }

}