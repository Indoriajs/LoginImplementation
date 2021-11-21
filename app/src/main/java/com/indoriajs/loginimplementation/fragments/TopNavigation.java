package com.indoriajs.loginimplementation.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.indoriajs.loginimplementation.R;
import com.indoriajs.loginimplementation.activities.StartActivity;


public class TopNavigation extends Fragment {


    public TopNavigation() {
        super(R.layout.fragment_top_navigation);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_top_navigation, container, false);

        Button btn = (Button) v.findViewById(R.id.logoutBtn);
        btn.setOnClickListener(v1 -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
            startNext();
        });
        return v;
    }

    private void startNext(){
        Intent intent = new Intent(getActivity(), StartActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }


}