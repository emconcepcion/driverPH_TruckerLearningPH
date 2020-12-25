package com.cav.quizinstructions;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Quizzes_menu extends AppCompatActivity {

    public static CardView cardViewBasic;
    public static CardView cardViewCommon;
    public static CardView cardViewCore;
    TextView tChapter;
    Button btn_leaderBoard;
    Button btn_list_completed_quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_menu);


        tChapter = findViewById(R.id.tChap);
        cardViewBasic = findViewById(R.id.cardView_basic_competencies);
        cardViewCommon = findViewById(R.id.cardView_common_competencies);
        cardViewCore = findViewById(R.id.cardView_core_competencies);
        btn_leaderBoard = findViewById(R.id.button5);
        btn_list_completed_quizzes = findViewById(R.id.button6);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
        String lessonChap = sp.getString("chapter", "");
        tChapter.setText(lessonChap);



        cardViewBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tChapter.setText("Basic Competencies");
                String chapTest = tChapter.getText().toString();
                Intent intent = new Intent(Quizzes_menu.this, QuizInstructions.class);
                Bundle bundle = new Bundle();
                bundle.putString("chapter", chapTest);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        cardViewCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tChapter.setText("Common Competencies");
                String chapTest = tChapter.getText().toString();
                Intent intent = new Intent(Quizzes_menu.this, QuizInstructions.class);
                Bundle bundle = new Bundle();
                bundle.putString("chapter", chapTest);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        cardViewCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tChapter.setText("Core Competencies");
                String chapTest = tChapter.getText().toString();
                Intent intent = new Intent(Quizzes_menu.this, QuizInstructions.class);
                Bundle bundle = new Bundle();
                bundle.putString("chapter", chapTest);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        btn_leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://driver-ph.000webhostapp.com/"));
                startActivity(intent);
            }
        });

        btn_list_completed_quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Quizzes_menu.this, CompletedQuizzes.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Quizzes_menu.this,Dashboard.class));
    }

}