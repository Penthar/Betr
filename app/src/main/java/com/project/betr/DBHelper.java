package com.project.betr;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBHelper {
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbReference;
    private ArrayList<User> usersList = new ArrayList<>();
    public String exampleID = "ZbKhdE2xYxX59e1Ra9YmLNOw7x52";
    public interface DataStatus{
        void DataIsLoaded(User user);
        void DataIsInserted(boolean success);
        void getWorkout(Workout workout);
        void DataIsDeleted();
    }

    public DBHelper() {
        fbDatabase = FirebaseDatabase.getInstance("https://betr-b3ddb-default-rtdb.europe-west1.firebasedatabase.app");

    }
    public String getExampleID(){return this.exampleID;}

    public void addUser(User user, DataStatus dataStatus){
        dbReference = fbDatabase.getReference("Users");
        ArrayList<Boolean> completion = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            completion.add(false);
        }
        user.setCompletion(completion);
        dbReference.child(user.getUserID()).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("checksuccess", "onSuccess: ");
                        dataStatus.DataIsInserted(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("checksuccess", "onFailure: ");
                        dataStatus.DataIsInserted(false);
                    }
                });

    }
    public void addWorkoutToUser(String UID, Workout workout, DataStatus dataStatus){
        dbReference = fbDatabase.getReference("Users");
        //String workoutkey = dbReference.push().getKey();

        this.getUserFromUID(UID, new DataStatus() {
            @Override
            public void DataIsLoaded(User user) {

                if (user.getWorkoutList().size() == 1 && user.getWorkoutList().get(0).getWorkout_Name().equals("Temp")){ //user has enterd no workouts
                    Log.d("checkuser", user.getUserID());
                    ArrayList<Workout> workoutList = new ArrayList<>();
                    workoutList.add(workout);
                    dbReference.child(UID).child("workoutList").setValue(workoutList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dataStatus.DataIsInserted(true);
                                }
                            });
                }
                else{
                    ArrayList<Workout> workoutList = user.getWorkoutList();
                    workoutList.add(workout);
                    dbReference.child(UID).child("workoutList").setValue(workoutList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dataStatus.DataIsInserted(true);
                                }
                            });
                }
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
    public void updateUserCompletionList(String UID, ArrayList<Boolean> completion){
        dbReference = fbDatabase.getReference("Users").child(UID).child("completion");
        if(dbReference!=null){
            dbReference.setValue(completion);
        }
        else
            Log.d("ERROR", "updateUserCompletionList: user doesnt exist");

    }
    public void updateUserScore(String UID, double score){
        DatabaseReference scoreRef = fbDatabase.getReference("Users").child(UID).child("score");
        if(scoreRef!=null){
            scoreRef.setValue(score);
        }
        else
            Log.d("ERROR", "updateUserCompletionList: user doesnt exist");
    }
    public void updateUserWorkoutList(String UID, Workout workout){
        dbReference = fbDatabase.getReference("Users");
        this.getUserFromUID(UID, new DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                ArrayList<Workout> workoutsList = user.getWorkoutList();
                String workoutID = workout.getID();
                updateUserScore(UID, user.getScore());
                for (int i = 0; i < workoutsList.size(); i++) {

                    if(workoutsList.get(i).getID().equals(workoutID)){
                        ArrayList<Exercise> exerciseList = workout.getExerciseList();
                        //exerciseList.addAll(workoutsList.get(i).getExerciseList());
                        ArrayList<Exercise> currList = workoutsList.get(i).getExerciseList();
                        exerciseList.addAll(currList);
                        workoutsList.remove(i);
                        //Workout newWorkout = new Workout(workout.getWorkout_Name(), workout.getExerciseList());
                        //newWorkout.setID(workoutID);
                        workoutsList.add(i, workout);
                        dbReference.child(UID).child("workoutList").setValue(workoutsList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });

                    }

                }

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
    public void deleteWorkout(String UID, String workoutID){
        dbReference = fbDatabase.getReference("Users");
        this.getUserFromUID(UID, new DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                ArrayList<Workout> workoutsList = user.getWorkoutList();
                for (int i = 0; i < workoutsList.size(); i++) {
                    if(workoutsList.get(i).getID().equals(workoutID)){
                        workoutsList.remove(i);
                        dbReference.child(UID).child("workoutList").setValue(workoutsList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                        return;
                    }

                }

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

    public void getUserFromUID(String UID, DataStatus dataStatus){
        dbReference = fbDatabase.getReference("Users");
        Log.d("checkUID", "getUserFromUID: " + UID);
        dbReference.child(UID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        double score = 0;
                        if(snapshot.child("score")!=null)
                            score = snapshot.child("score").getValue(double.class);
                        ArrayList<Workout> workoutList = new ArrayList<Workout>();
                        ArrayList<Boolean> completedList = new ArrayList<>();
                        for (DataSnapshot completedSnapshot : snapshot.child("completion").getChildren()){
                            Log.d("boolcheck1", "onComplete: ");
                            if(completedSnapshot.getValue()!=null) {
                                completedList.add(completedSnapshot.getValue(Boolean.class));
                                Log.d("boolcheck1", "onComplete: " + completedSnapshot.getValue(Boolean.class));
                            }

                        }
                        for (DataSnapshot ds : snapshot.child("workoutList").getChildren()){
                            Workout workout = ds.getValue(Workout.class);
                            int i = 0;
                            for( DataSnapshot checkdata : ds.child("exerciseList").getChildren()) {
                                if(checkdata.child("is_active").getValue()!=null){
                                    workout.getExerciseList().get(i).setIs_active((int)checkdata.child("is_active").getValue(Integer.class));
                                }
                                else
                                    workout.getExerciseList().get(i).setIs_active((int)checkdata.child("active").getValue(Integer.class));
                                i++;
                            }
                            workoutList.add(workout);
                        }
                        User curruser = new User(UID, name, workoutList);
                        curruser.setCompletion(completedList);
                        curruser.setScore(score);
                        dataStatus.DataIsLoaded(curruser);
                    }
                }
                else {
                    Log.d("ERROR", "onComplete: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAIL", "onFailure: ");
            }
        });
    }
    public void getWorkoutFromID(String workoutID, DataStatus dataStatus){
        dbReference = fbDatabase.getReference("Users");

        Task<DataSnapshot> task = dbReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot workoutSnapshot : userSnapshot.child("workoutList").getChildren()) {
                                String id = workoutSnapshot.child("id").getValue(String.class);
                                if (id != null && id.equals(workoutID)) {
                                    // Found the matching workout
                                    Workout workout = workoutSnapshot.getValue(Workout.class);
                                    dataStatus.getWorkout(workout);
                                    return;

                                }
                            }
                        }
                    }
                    dataStatus.getWorkout(null);
                    // No matching workout found
                } else {
                    // Error occurred while retrieving data
                }
            }

        });

    }
    public void set_all_exercise_to_inactive(String UID, String workoutId, Workout workout){
        dbReference = fbDatabase.getReference("Users").child(UID).child("workoutList");
        Task<DataSnapshot> task = dbReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if(dataSnapshot != null){
                            for (DataSnapshot workoutsnapshot : dataSnapshot.getChildren()){
                                if(workoutsnapshot.child("id").getValue(String.class).equals(workoutId)){
                                    String key = workoutsnapshot.getKey();
                                    DatabaseReference workoutRef = dbReference.child(key);
                                    for(DataSnapshot exercises : workoutsnapshot.child("exerciseList").getChildren()){
                                        String exckey = exercises.getKey();
                                        DatabaseReference activeRef = workoutRef.child(exckey);

                                        exercises.getRef().child("active").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });

                                    }
                                }

                            }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateUserWorkoutList(UID, workout);
                    }
                }
            }

        });

    }
}
