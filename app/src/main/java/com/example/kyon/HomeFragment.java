package com.example.kyon;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
//                startGenerate();
                inputFirst();
            }
        });


        return view;
    }

    private void inputFirst() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Input First");

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getActivity(), GenOffpsringActivity.class);
                intent.putExtra("link", input.getText().toString());
                startActivity(intent);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getActivity(), "Closed", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();

    }

    private void startGenerate() {
        Intent intent = new Intent(getActivity(), GenOffpsringActivity.class);
        startActivity(intent);
    }


    private void startClassify() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }


}