package com.project.betr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    Button SignIn;
    TextView Register;
    EditText email_input;
    EditText password_input;
    ProgressBar progressBar;
    FirebaseAuth fbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignIn = findViewById(R.id.SignInButton);
        Register = findViewById(R.id.RegisterLink);
        email_input = findViewById(R.id.EmailAddressInput);
        password_input = findViewById(R.id.PasswordInput);
        progressBar = findViewById(R.id.prog_bar);
        SignIn.setOnClickListener(this);
        Register.setOnClickListener(this);

        fbAuth = FirebaseAuth.getInstance();
        if(fbAuth.getCurrentUser()!=null){
            email_input.setText(fbAuth.getCurrentUser().getEmail());

        }


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == SignIn.getId()){
            if(isEmpty())
                return;
            inProgress(true);
            fbAuth.signInWithEmailAndPassword(email_input.getText().toString(), password_input.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            inProgress(false);
                            Toast.makeText(MainActivity.this, "Successful log in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish(); return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            inProgress(false);
                            Toast.makeText(MainActivity.this, "LOG IN FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });
            //sign in attempt
        }
        else{
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

    }
    private void inProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            SignIn.setEnabled(false);
            Register.setEnabled(false);
        }
        else{
            progressBar.setVisibility(View.GONE);
            SignIn.setEnabled(true);
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
        return false;
        //check if either input field is empty
    }

}