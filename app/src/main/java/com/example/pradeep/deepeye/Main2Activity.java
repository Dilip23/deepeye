package com.example.pradeep.deepeye;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    EditText emailId,email_password;
    Button signUp,signIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        emailId = (EditText)findViewById(R.id.emailAddress);
        email_password = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.signup_button);
        signIn = (Button)findViewById(R.id.signin_button);


        mAuth = FirebaseAuth.getInstance();
        //Register on click listeners
        emailId.setOnClickListener(this);
        email_password.setOnClickListener(this);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String email = emailId.getText().toString();
        String password = email_password.getText().toString();
        if (id == R.id.signup_button){
            registerAccount(email,password);
        }
        else {
            Login(email,password);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            startActivity(new Intent(Main2Activity.this,MainActivity.class));
        }
        else {
            findViewById(R.id.emailAddress).setVisibility(View.VISIBLE);
            findViewById(R.id.password).setVisibility(View.VISIBLE);
            findViewById(R.id.signin_button).setVisibility(View.VISIBLE);
            findViewById(R.id.signup_button).setVisibility(View.VISIBLE);

        }
    }

    private void Login(String email, String password) {
        if (!valid()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user  = mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Toast.makeText(Main2Activity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

            }
        });
    }

    private void registerAccount(String email, String password) {
        if (!valid())
            return;
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else {
                    Toast.makeText(Main2Activity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private boolean valid() {
        boolean validate = true;
        String email = emailId.getText().toString();
        String password = email_password.getText().toString();
        if (TextUtils.isEmpty(email)){
            emailId.setError("Required");
            validate = false;
        }
        else {
            emailId.setError(null);
        }

        if (TextUtils.isEmpty(password)){
            email_password.setError("Required");
            validate = false;
        }else {
            email_password.setError(null);
        }
        return validate;

    }



}
