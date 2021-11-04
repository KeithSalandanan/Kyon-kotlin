package com.example.kyon;


import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm,getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        NeumorphCardView cardClassify= findViewById(R.id.cardClassifyDog);
//        NeumorphCardView cardGenerate= findViewById(R.id.cardGenerateOffspring);
//
//
//
//        cardClassify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (hasCameraPermission()) {
//                    startClassify();
//                } else {
//                    requestPermission();
//                }
//            }
//        });
//
//        cardGenerate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startGenerate();
//            }
//        });
    }

//    private boolean hasCameraPermission() {
//        return ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(
//                this,
//                CAMERA_PERMISSION,
//                CAMERA_REQUEST_CODE
//        );
//    }
//
//    private void startClassify() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivity(intent);
//    }
//
//    private void startGenerate() {
//        Intent intent = new Intent(this, GenOffspringActivity.class);
//        startActivity(intent);
//    }

}