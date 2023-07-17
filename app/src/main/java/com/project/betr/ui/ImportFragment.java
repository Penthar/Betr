package com.project.betr.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.betr.DBHelper;
import com.project.betr.Exercise;
import com.project.betr.MainActivity;
import com.project.betr.R;
import com.project.betr.RegisterActivity;
import com.project.betr.TimerActivity;
import com.project.betr.User;
import com.project.betr.Workout;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //////////
    EditText codeInput;
    DBHelper dbHelper;
    FirebaseAuth fbAuth;
    User currentUser;
    LinearLayout workoutsLayout;
    ProgressBar progressBar;
    ArrayList<Workout> exampleWorkoutsList;
    Button selectedWorkout;
    Button addWorkout;
    ArrayList<Button> workoutButtonsList;
    Workout currentworkout;
    ///////////

    public ImportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImportFragment newInstance(String param1, String param2) {
        ImportFragment fragment = new ImportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fbAuth = FirebaseAuth.getInstance();
        dbHelper = new DBHelper();
        if(fbAuth.getCurrentUser()==null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            this.getActivity().finish();
        }
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        codeInput = (EditText) getView().findViewById(R.id.codeInput);
        codeInput.setOnClickListener(this);
        workoutsLayout = (LinearLayout) getView().findViewById(R.id.workouts_layout);
        progressBar = (ProgressBar) getView().findViewById(R.id.prog_bar);
        progressBar.setVisibility(View.VISIBLE);
        addWorkout = (Button) getView().findViewById(R.id.AddNewButton) ;
        addWorkout.setOnClickListener(this);
        workoutButtonsList = new ArrayList<>();
        new DBHelper().getUserFromUID(fbAuth.getUid(), new DBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                currentUser = new User(user);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                getExampleWorkouts();

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
        super.onViewCreated(view, savedInstanceState);
    }
    private void getExampleWorkouts(){
        new DBHelper().getUserFromUID(dbHelper.getExampleID(), new DBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                 exampleWorkoutsList = user.getWorkoutList();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                populate_workouts();
                progressBar.setVisibility(View.GONE);

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
    public void populate_workouts(){

        ArrayList<Workout> workoutList = exampleWorkoutsList;
        int[] colors = getResources().getIntArray(R.array.color_array);
        int rownums;
        ///
        if((workoutList.size() % 2) == 0)
            rownums = workoutList.size();
        else
            rownums = workoutList.size() + 1;
        //returns number of rows required
        ///
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(dpToPx(160), dpToPx(100));
        buttonParams.setMargins(dpToPx(30), dpToPx(15), dpToPx(0), dpToPx(15));
        LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout currentHorizontal = new LinearLayout(getContext());
        if(!(workoutList.size()==1 && workoutList.get(0).getWorkout_Name().equals("Temp"))){//user has inserted workout
            for (int i = 0; i < workoutList.size(); i++) {
                if(i % 2 == 0 || (i == (workoutList.size() - 1) && workoutList.size() % 2 == 1)){
                    LinearLayout newHorizontal = new LinearLayout(getContext());
                    newHorizontal.setLayoutParams(horizontalParams);
                    newHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                    workoutsLayout.addView(newHorizontal);
                    currentHorizontal = newHorizontal;

                }
                setButtonVisuals(buttonParams, colors, workoutList, i, currentHorizontal);
            }
        }
    }
    public void setButtonVisuals(LinearLayout.LayoutParams buttonParams, int[] colors, ArrayList<Workout> workoutList, int i, LinearLayout horizontalLayout){
        Button workout_Button = new Button(getContext());
        //workout_Button.setBackground(getContext().getDrawable(R.drawable.workout_button));
        GradientDrawable gradientDrawable= new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(colors[i%13]);
        gradientDrawable.setCornerRadius(dpToPx(15));
        workout_Button.setBackground(gradientDrawable);
        workout_Button.setText(workoutList.get(i).getWorkout_Name());
        workout_Button.setTag(workoutList.get(i).getID());
        workout_Button.setOnClickListener(this);
        workout_Button.setTextColor(Color.WHITE);
        workout_Button.setTypeface(Typeface.DEFAULT_BOLD);
        workout_Button.setLayoutParams(buttonParams);
        horizontalLayout.addView(workout_Button);
        workoutButtonsList.add(workout_Button);
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

    @Override
    public void onClick(View view) {
        if(view != addWorkout && view instanceof Button){
            for (int i = 0; i < workoutButtonsList.size(); i++) {
                GradientDrawable gradientDrawable= (GradientDrawable) workoutButtonsList.get(i).getBackground();
                gradientDrawable.setStroke(0, Color.TRANSPARENT);
                workoutButtonsList.get(i).setBackground(gradientDrawable);
            }
            for (int i = 0; i < exampleWorkoutsList.size(); i++) {
                if(view.getTag().equals(exampleWorkoutsList.get(i).getID()))
                    currentworkout = exampleWorkoutsList.get(i);
            }
            if(selectedWorkout == view){

                Intent intent = new Intent(getContext(), TimerActivity.class);

                if(currentworkout!=null) {
                    ArrayList<Exercise> activeExercises = new ArrayList<Exercise>();
                    for (int i = 0; i < currentworkout.getExerciseList().size(); i++) {
                        if(currentworkout.getExerciseList().get(i).isActive()==1)
                            activeExercises.add(currentworkout.getExerciseList().get(i));
                    }
                    intent.putExtra("EXERCISE LIST", activeExercises);
                    intent.putExtra("WORKOUT NAME", currentworkout.getWorkout_Name());
                    startActivity(intent);
                }
                else
                    try {
                        throw new Exception("example workout id doesn't match");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }
            selectedWorkout = (Button) view;
            GradientDrawable gradientDrawable= (GradientDrawable) view.getBackground();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadius(dpToPx(15));
            gradientDrawable.setStroke(dpToPx(3), Color.BLACK);
            view.setBackground(gradientDrawable);

        }
        else if (view == codeInput){
            selectedWorkout = null;
            for (int i = 0; i < workoutButtonsList.size(); i++) {
                GradientDrawable gradientDrawable= (GradientDrawable) workoutButtonsList.get(i).getBackground();
                gradientDrawable.setStroke(0, Color.TRANSPARENT);
                workoutButtonsList.get(i).setBackground(gradientDrawable);
            }
        }
        else{//view is edittext
            if(selectedWorkout!=null){
                Workout workoutToAdd = currentworkout;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();
                String uniqueId = ref.push().getKey();
                workoutToAdd.setID(uniqueId);
                for (int i = 0; i < workoutToAdd.getExerciseList().size(); i++) {
                    workoutToAdd.getExerciseList().get(i).calculateExercise_score();
                    workoutToAdd.getExerciseList().get(i).setDate(new Date(System.currentTimeMillis()));
                }
                Toast.makeText(getContext(), "WORKOUT ADDED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                dbHelper.addWorkoutToUser(currentUser.getUserID(), workoutToAdd, new DBHelper.DataStatus() {
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
            }
            else{//code input is selected
                if(TextUtils.isEmpty(codeInput.getText().toString()))
                    Toast.makeText(getContext(), "ENTER WORKOUT CODE", Toast.LENGTH_SHORT).show();
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    dbHelper.getWorkoutFromID(codeInput.getText().toString(), new DBHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(User user) {

                        }

                        @Override
                        public void DataIsInserted(boolean success) {

                        }

                        @Override
                        public void getWorkout(Workout workout) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(workout==null) {
                                Toast.makeText(getContext(), "WORKOUT CODE IS WRONG", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                            else{
                                Workout workoutToAdd = workout;
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ref = database.getReference();
                                String uniqueId = ref.push().getKey();
                                workoutToAdd.setID(uniqueId);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "WORKOUT ADDED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                codeInput.setText("");
                                dbHelper.addWorkoutToUser(currentUser.getUserID(), workoutToAdd, new DBHelper.DataStatus() {
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
                            }
                        }

                        @Override
                        public void DataIsDeleted() {

                        }
                    });
                }
            }
        }
    }
}