package com.project.betr;

import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class User {



    private String userID;
    private String name;

    private ArrayList<Workout> workoutList;
    private int character_pic_id;

    double score;


    private ArrayList<Boolean> completion;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        this.workoutList = new ArrayList<Workout>();
        workoutList.add(new Workout("Temp", new ArrayList<>()));

    }


    public User(String userID, String name, ArrayList<Workout> workoutList) {
        this.userID = userID;
        this.name = name;
        this.workoutList = workoutList;
    }
    public User(User user){
        this.userID = user.getUserID();
        this.name = user.getName();
        this.workoutList = user.getWorkoutList();
        this.completion=user.getCompletion();
    }
    public String getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }
    public ArrayList<Workout> getWorkoutList() {
        return workoutList;
    }
    public void addToWorkoutList(Workout workout){workoutList.add(workout);}
    public int getCharacter_pic() {
        return character_pic_id;
    }
    public void setCharacter_pic(int character_pic) {
        this.character_pic_id = character_pic;
    }
    public void setWorkoutList(ArrayList<Workout> workoutList) {
        this.workoutList = workoutList;
    }
    public ArrayList<Boolean> getCompletion() {
        return completion;
    }
    public void setCompletion(ArrayList<Boolean> completion) {
        this.completion = completion;
    }
    public double getScore(){return this.score;}
    public void setScore(double score){this.score = score;}

    public void calculateTotalUserScore(){
        double upper_score = 0;
        double lower_score = 0;
        double abs_score = 0;
        for (int i = 0; i < workoutList.size(); i++) {
            ArrayList<Exercise> exerciseList = workoutList.get(i).getExerciseList();
            for (int j = 0; j < exerciseList.size(); j++) {
                if (exerciseList.get(j).isActive() == 1) {
                    if (exerciseList.get(j).getExercise_category().equals("Upper Body"))
                        upper_score = addToAverage(upper_score, exerciseList.get(j).getExercise_score());
                    else if (exerciseList.get(j).getExercise_category().equals("Lower Body"))
                        lower_score = addToAverage(lower_score, exerciseList.get(j).getExercise_score());
                    else
                        abs_score = addToAverage(abs_score, exerciseList.get(j).getExercise_score());
                }
            }
        }
        Log.d("SCORE", "calculateTotalUserScore: "+ upper_score * 0.4 + lower_score * 0.4 + abs_score * 0.2);
        this.score = upper_score * 0.4 + lower_score * 0.4 + abs_score * 0.2;
        new DBHelper().updateUserScore(this.getUserID(), this.getScore());

    }
    public double addToAverage(double counter, double addition){
        if(counter==0)
            return addition;
        else
            return (counter+addition)/2;
    }
}
