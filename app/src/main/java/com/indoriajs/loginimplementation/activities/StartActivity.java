package com.indoriajs.loginimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.indoriajs.loginimplementation.R;
import com.indoriajs.loginimplementation.fragments.SignInFragment;
import com.indoriajs.loginimplementation.fragments.SignUpFragment;


public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private SignInFragment sInFragment;
    private SignUpFragment sUpFragment;

    private FragmentManager fragmentManager;

    private boolean isSignIn = true;

    private Button sInFragBtn;
    private Button sUpFragBtn;
    private Button accountAction;

    private ViewGroup sceneRoot;

    private Scene signInScene;
    private Scene signUpScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();

        switchToSignIn();

        sInFragBtn.setOnClickListener(v -> {
            switchToSignIn();
        });

        sUpFragBtn.setOnClickListener(v -> {
            switchToSignUp();
        });

        accountAction.setOnClickListener(v -> {
            EditText emailET;
            EditText passwordET;
            if(isSignIn){
                emailET = findViewById(R.id.editTextTextEmailAddress);
                passwordET = findViewById(R.id.passET);

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if(!email.isEmpty() && !password.isEmpty())
                    signIn(email, password);
                else if(email.isEmpty())
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else {
                emailET = findViewById(R.id.userEmail);
                passwordET = findViewById(R.id.userPassword);

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if(!email.isEmpty() && !password.isEmpty())
                    createAccount(email, password);
                else if(email.isEmpty())
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("Check", "User: " + currentUser.getDisplayName());
            Toast.makeText(getApplicationContext(), "Welcome " + currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
            startNext(currentUser);
        }
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();

        sInFragment = new SignInFragment();
        sUpFragment = new SignUpFragment();

        sInFragBtn = findViewById(R.id.sInBtn);
        sUpFragBtn = findViewById(R.id.sUpBtn);
        accountAction = findViewById(R.id.checkAuth);

        sceneRoot = findViewById(R.id.flfragmentView);
        signInScene = Scene.getSceneForLayout(sceneRoot, R.layout.fragment_sign_in, getApplicationContext());
        signUpScene = Scene.getSceneForLayout(sceneRoot, R.layout.fragment_sign_up, getApplicationContext());

        fragmentManager = getSupportFragmentManager();
    }


    private void switchToSignIn() {
        sInFragBtn.setSelected(true);
        sUpFragBtn.setSelected(false);
        isSignIn = true;

        accountAction.setText(R.string.sign_in_btn);

        fragmentManager.beginTransaction()
                .replace(R.id.flfragmentView, sInFragment, null)
                .commit();
        TransitionManager.go(signInScene, new Slide());
    }


    private void switchToSignUp() {
        sInFragBtn.setSelected(false);
        sUpFragBtn.setSelected(true);
        isSignIn = false;

        accountAction.setText(R.string.create_acc);

        fragmentManager.beginTransaction()
                .replace(R.id.flfragmentView, sUpFragment, null)
                .commit();
        TransitionManager.go(signUpScene, new Slide());
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(getApplicationContext(), "Account created...", Toast.LENGTH_LONG).show();
                    switchToSignIn();
                });
    }

    private void  signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "LogIn Success", Toast.LENGTH_LONG).show();
                        startNext(mAuth.getCurrentUser());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "LogIn Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startNext(FirebaseUser current) {
        Intent intent = new Intent(StartActivity.this, HomeScreen.class);
        intent.putExtra("UserName", current);
        startActivity(intent);
        finish();
    }

}