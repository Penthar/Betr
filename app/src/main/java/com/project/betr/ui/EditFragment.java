package com.project.betr.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.betr.Create_Workout_Activity;
import com.project.betr.DBHelper;
import com.project.betr.MainActivity;
import com.project.betr.MenuActivity;
import com.project.betr.R;
import com.project.betr.TimerPicker;
import com.project.betr.Update_Workout_Activity;
import com.project.betr.User;
import com.project.betr.Workout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ///////
    Button create_workout;
    private DBHelper dbHelper;
    private FirebaseAuth fbAuth;
    User currentUser;
    LinearLayout workouts_layout;
    ProgressBar progressBar;
    //////

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
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
        return inflater.inflate(R.layout.fragment_edit, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workouts_layout = (LinearLayout) getView().findViewById(R.id.main_layout);
        create_workout = getView().findViewById(R.id.AddNewwButton);
        create_workout.setOnClickListener(this);
        progressBar = (ProgressBar) getView().findViewById(R.id.prog_bar);
    }

    @Override
    public void onClick(View view) {
        if(view == create_workout) {
            Intent intent = new Intent(getContext(), Create_Workout_Activity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(getContext(), Update_Workout_Activity.class);
            intent.putExtra("WORKOUT ID", (String)view.getTag());
            startActivity(intent);
        }
    }
    public void populate_workouts(){

        ArrayList<Workout> workoutList = currentUser.getWorkoutList();
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
                    workouts_layout.addView(newHorizontal);
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
    public void onResume() {
        progressBar.setVisibility(View.VISIBLE);
        workouts_layout.removeAllViews();
        workouts_layout.addView(create_workout);
        new DBHelper().getUserFromUID(fbAuth.getUid(), new DBHelper.DataStatus() {
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
                populate_workouts();

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
        super.onResume();
    }
}