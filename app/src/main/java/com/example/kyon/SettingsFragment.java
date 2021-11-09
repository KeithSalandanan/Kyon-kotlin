package com.example.kyon;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
    View view;
    Button btnStatement, btnGuides, btnExit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        btnStatement = view.findViewById(R.id.btnPrivacy);
        btnGuides = view.findViewById(R.id.btnGuides);
        btnExit = view.findViewById(R.id.btnExit);

        btnStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PrivacyStatement.class);
                startActivity(i);
            }
        });

        btnGuides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GuidesAndTips.class);
                startActivity(i);
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Exit", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                System.exit(0);
            }
        });



        return view;
    }
}