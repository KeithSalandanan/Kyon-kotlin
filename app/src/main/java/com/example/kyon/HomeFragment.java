package com.example.kyon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import soup.neumorphism.NeumorphCardView;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        NeumorphCardView cardClassify= view.findViewById(R.id.cardClassifyDog);
        NeumorphCardView cardGenerate= view.findViewById(R.id.cardGenerateOffspring);



        cardClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startClassify();

            }
        });

        cardGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGenerate();
            }
        });


        return view;
    }

    private void startGenerate() {
        Intent intent = new Intent(getActivity(), GenOffspringActivity.class);
        startActivity(intent);
    }

    private void startClassify() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }


}