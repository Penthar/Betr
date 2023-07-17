package com.project.betr;

import java.util.ArrayList;
import java.util.List;

public class Workout {



    private String workout_Name;
    private String ID;
    private ArrayList<Exercise> exerciseList;


    public Workout(String workout_name, ArrayList<Exercise> exerciseList) {
        workout_Name = workout_name;
        this.exerciseList = exerciseList;
    }
    public Workout(){this.exerciseList = new ArrayList<Exercise>();}
    public String getWorkout_Name() {
        return workout_Name;
    }
    public String getID() {
        return ID;
    }
    public void setID(String id){this.ID = id;}
    public ArrayList<Exercise> getExerciseList() {
        return exerciseList;
    }
    public void setExerciseList(ArrayList<Exercise> exerciseList){this.exerciseList = exerciseList;}

}
