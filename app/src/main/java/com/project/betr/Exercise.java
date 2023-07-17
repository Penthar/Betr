package com.project.betr;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class Exercise implements Serializable {

    Date date;
    String name;
    int weight;
    int sets;
    int reps;
    int rest_time;
    int set_duration;
    String id;
    int is_active;
    String exercise_category;
    double exercise_score;


    public Exercise(String name, int weight, int reps, int sets, int rest_time, int set_duration, String exercise_category, int is_active) {
        this.name = name;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
        this.rest_time = rest_time;
        this.set_duration = set_duration;
        this.exercise_category = exercise_category;
        this.is_active = is_active;
    }
    public Exercise(){
    }
    public Date getDate() {
        return date;
    }
    public String getName() {
        return name;
    }
    public int getWeight() {
        return weight;
    }
    public int getSets() {
        return sets;
    }
    public int getReps() {
        return reps;
    }
    public int getRest_time() {
        return rest_time;
    }
    public int getSet_duration() {
        return set_duration;
    }
    public String getId() {
        return id;
    }
    public int isActive() {
        return this.is_active;
    }
    public String getExercise_category() {

        return exercise_category;
    }
    public double getExercise_score() {
        calculateExercise_score();
        return exercise_score;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public void setRest_time(int rest_time) {
        this.rest_time = rest_time;
    }
    public void setSet_duration(int set_duration) {
        this.set_duration = set_duration;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }
    public void setExercise_category(String exercise_category) {
        this.exercise_category = exercise_category;
    }
    public void calculateExercise_score() {
        this.exercise_score = ((double)((0.8 * this.weight) + (0.2 * this.reps))/(0.05 * this.rest_time));
    }



}
