package com.project.betr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RegisterActivity extends Activity implements View.OnClickListener {
    Button Register;
    EditText email_input;
    EditText password_input;
    EditText name_input;
    ProgressBar progressBar;
    FirebaseAuth fbAuth;
    DBHelper dbHelper;
    boolean Success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register = findViewById(R.id.RegisterButton);
        email_input = findViewById(R.id.EmailAddressInput);
        password_input = findViewById(R.id.PasswordInput);
        name_input = findViewById(R.id.NameInput);
        progressBar = findViewById(R.id.prog_bar);

        Register.setOnClickListener(this);
        fbAuth = FirebaseAuth.getInstance();

        dbHelper = new DBHelper();
    }

    @Override
    public void onClick(View view) {
        if(isEmpty())
            return;
        inProgress(true);
        fbAuth.createUserWithEmailAndPassword(email_input.getText().toString(), password_input.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = fbAuth.getCurrentUser();
                        if(firebaseUser!=null) {
                            User user = new User(firebaseUser.getUid(), name_input.getText().toString());
                            addUserToDB(user);
                            inProgress(false);
                            Log.d("checksuccess", Success + " ");
                           // if (Success) { ///////////////////// NOTE ---- check DB success
                                Toast.makeText(RegisterActivity.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                finish(); return;
                                ////finish and return
                            //}/////////////////
                        }
                        /////failure
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        inProgress(false);
                        Toast.makeText(RegisterActivity.this, "REGISTRATION FAILED", Toast.LENGTH_SHORT).show();
                    }
                });
        //registration attempt
    }
    private void inProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);

            Register.setEnabled(false);
        }
        else{
            progressBar.setVisibility(View.GONE);

            Register.setEnabled(true);
        }
        //method to handle actions while communicating with firebase
    }
    private boolean isEmpty(){
        if(TextUtils.isEmpty(email_input.getText().toString())){
            email_input.setError("Input required!");
            return true;
        }
        if(TextUtils.isEmpty(password_input.getText().toString())) {
            password_input.setError("Input required!");
            return true;
        }
        if(password_input.getText().toString().length()<6){
            password_input.setError("Must be 6 or longer!");
            return true;
        }
        if(TextUtils.isEmpty(name_input.getText().toString())) {
            name_input.setError("Input required!");
            return true;
        }
        return false;
        //check if either input field is empty
    }
    private void addUserToDB(User user){

        dbHelper.addUser(user, new DBHelper.DataStatus() {

            @Override
            public void DataIsLoaded(User user) {

            }
            @Override
            public void DataIsInserted(boolean success) {
                Log.d("checksuccess11", "DataIsInserted: " + success);
                Success = success;
                Log.d("checksuccess11", "DataIsInserted: " + Success);
            }
            @Override
            public void getWorkout(Workout workout) {

            }
            @Override
            public void DataIsDeleted() {

            }
        });

        //insert user to realtime database
    }
}