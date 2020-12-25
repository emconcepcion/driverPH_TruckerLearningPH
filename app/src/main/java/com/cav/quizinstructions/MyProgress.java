package com.cav.quizinstructions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.kofigyan.stateprogressbar.StateProgressBar;

public class MyProgress extends AppCompatActivity {

//    String[] descriptionData = {"Begin here...", "Keep going...", "You're doing great!", "Almost done with this chapter.", "Completed! Great job!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_progress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("My Progress");


    }
}