package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Lessons_Menu extends AppCompatActivity {

    public static TextView tChapter;
    public static CardView cardViewBasic;
    public static CardView cardViewCommon;
    public static CardView cardViewCore;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons__menu);

        tChapter = findViewById(R.id.tChap_Lesson);
        cardViewBasic = findViewById(R.id.cardView_basic_competencies);
        cardViewCommon = findViewById(R.id.cardView_common_competencies);
        cardViewCore = findViewById(R.id.cardView_core_competencies);
        Button btnEvaluation = findViewById(R.id.button5);

        cardViewBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tChapter.setText("Basic Competencies");
                String chapTest = tChapter.getText().toString();
                sp = getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("chapter", chapTest);
                editor.apply();
                startActivity(new Intent(Lessons_Menu.this,Lessons_Basic_Content.class));
            }
        });

        cardViewCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tChapter.setText("Common Competencies");
                String chapTest = tChapter.getText().toString();
                sp = getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("chapter", chapTest);
                editor.apply();
                startActivity(new Intent(Lessons_Menu.this,Lessons_Basic_Content.class));
            }
        });

        cardViewCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tChapter.setText("Core Competencies");
                String chapTest = tChapter.getText().toString();
                sp = getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("chapter", chapTest);
                editor.apply();
                startActivity(new Intent(Lessons_Menu.this,Lessons_Basic_Content.class));
            }
        });

        btnEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Lessons_Menu.this,Evaluation_Menu.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Lessons_Menu.this,Dashboard.class));
    }
}