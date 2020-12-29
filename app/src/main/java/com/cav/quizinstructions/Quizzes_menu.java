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
import android.widget.Toast;

import java.util.ArrayList;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;

public class Quizzes_menu extends AppCompatActivity {

    public static CardView cardViewBasic;
    public static CardView cardViewCommon;
    public static CardView cardViewCore;
    TextView tChapter;
    Button btn_leaderBoard;
    Button btn_list_completed_quizzes;
    public static boolean isFromQuizMenu;
    public static TextView myEmailQMenu, userIdQMenu;

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
        myEmailQMenu = findViewById(R.id.email_qMenu);
        userIdQMenu = findViewById(R.id.txt_userIdQuiz);


        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Dashboard.dashboard_email = sp.getString(EMAIL, "");
        myEmailQMenu.setText(Dashboard.dashboard_email);

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        Dashboard.user_id = sharedPreferences.getString("user_id", "");
        userIdQMenu.setText(Dashboard.user_id);

        Lessons_Menu.isFromLessonsMenu = false;
        isFromQuizMenu= true;


        cardViewBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromQuizMenu= true;
                if (QuizResults.unlocked){
                    Toast.makeText(Quizzes_menu.this, "You have passed this test and will no longer be able to retake it.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Quizzes_menu.this,Dashboard.class));
                }
                tChapter.setText("Basic Competencies");
                String chapTest = tChapter.getText().toString();
                SharedPreferences sp = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Qchapter", chapTest);
                editor.apply();
                startActivity(new Intent(Quizzes_menu.this,QuizInstructions.class));
            }
        });

        cardViewCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromQuizMenu= true;
                tChapter.setText("Common Competencies");
                String chapTest = tChapter.getText().toString();
                SharedPreferences sp = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Qchapter", chapTest);
                editor.apply();
                startActivity(new Intent(Quizzes_menu.this,QuizInstructions.class));
            }
        });

        cardViewCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromQuizMenu= true;
                tChapter.setText("Core Competencies");
                String chapTest = tChapter.getText().toString();
                SharedPreferences sp = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Qchapter", chapTest);
                editor.apply();
                startActivity(new Intent(Quizzes_menu.this,QuizInstructions.class));
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
//                    startActivity(new Intent(Quizzes_menu.this, TestListUnavailable.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Quizzes_menu.this,Dashboard.class));
    }

}