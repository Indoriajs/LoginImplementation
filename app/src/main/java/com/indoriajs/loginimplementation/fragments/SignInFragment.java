package com.indoriajs.loginimplementation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.indoriajs.loginimplementation.R;
import com.indoriajs.loginimplementation.activities.HomeScreen;

import java.util.Objects;
import java.util.concurrent.Executor;

public class SignInFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> launchActivity;

    public SignInFragment() {
        super(R.layout.fragment_sign_in);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_i))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        firebaseAuth = FirebaseAuth.getInstance();

        launchActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Log.d("Check", ": Check 1 ");
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount gsA = task.getResult();
                                Log.d("Check", " : OnActivityResult try success");
                                firebaseAuthWithGoogle(gsA.getIdToken());
                            } catch (Exception e) {
                                Log.d("Check", ": Try failed ");
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        ImageButton btn = (ImageButton) v.findViewById(R.id.gSignButton);
        btn.setOnClickListener(v1 -> {
            signIn();
        });
        return v;
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Sign in Successful...", Toast.LENGTH_SHORT).show();
                            startNext();
                        }
                    }
                });
    }
    private void signIn() {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        launchActivity.launch(signInIntent);
    }

    private void startNext() {
        Intent passIntent = new Intent(getActivity(), HomeScreen.class);
        passIntent.putExtra("User: ", firebaseAuth.getCurrentUser());
        startActivity(passIntent);
        requireActivity().finish();
    }
}