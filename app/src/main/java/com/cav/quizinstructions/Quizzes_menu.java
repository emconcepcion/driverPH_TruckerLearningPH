package com.cav.quizinstructions;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;

public class Quizzes_menu extends AppCompatActivity {

    private final int REQUEST_CODE = 101;
    public static CardView cardViewMod1;
    public static CardView cardViewMod2;
    public static CardView cardViewMod3;
    TextView tChapter;
    Button btn_leaderBoard;
    Button btn_list_completed_quizzes;
    public static boolean isFromQuizMenu;
    public static TextView myEmailQMenu, userIdQMenu;
    int M1, M2, M3;
    TextView textV1, textV2, textV3;
    String testModule;
    public static boolean allTestsCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_menu);

        tChapter = findViewById(R.id.tChap);

        cardViewMod1 = findViewById(R.id.cardView_basic_competencies);
        cardViewMod2 = findViewById(R.id.cardView_common_competencies);
        cardViewMod3 = findViewById(R.id.cardView_core_competencies);

        textV1 = findViewById(R.id.tv1);
        textV2 = findViewById(R.id.tv2);
        textV3 = findViewById(R.id.tv3);

        btn_leaderBoard = findViewById(R.id.button5);
        btn_list_completed_quizzes = findViewById(R.id.button6);
        myEmailQMenu = findViewById(R.id.email_qMenu);
        userIdQMenu = findViewById(R.id.txt_userIdQuiz);

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Dashboard.dashboard_email = sp.getString(EMAIL, "");
        myEmailQMenu.setText(Dashboard.dashboard_email);

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt("user_id", 0);
        userIdQMenu.setText(String.valueOf(uid));

        Lessons_Menu.isFromLessonsMenu = false;
        isFromQuizMenu = true;

        if (allTestsCompleted){
            StyleableToast.makeText(getApplicationContext(), Quizzes_menu.this.getString(R.string.allTestsDone),
                    Toast.LENGTH_LONG, R.style.toastStyle).show();
        }

        cardViewMod1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText("Basic Competencies");
                String chapTest1 = tChapter.getText().toString();
                SharedPreferences sp1 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp1.edit();
                editor1.putString("Qchapter", chapTest1);
                editor1.apply();
                startActivity(new Intent(Quizzes_menu.this, QuizInstructions.class));
            }
        });

        cardViewMod2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText("Common Competencies");
                String chapTest2 = tChapter.getText().toString();
                SharedPreferences sp2 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putString("Qchapter", chapTest2);
                editor2.apply();
                startActivity(new Intent(Quizzes_menu.this, QuizInstructions.class));
            }
        });

        cardViewMod3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText("Core Competencies");
                String chapTest3 = tChapter.getText().toString();
                SharedPreferences sp3 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = sp3.edit();
                editor3.putString("Qchapter", chapTest3);
                editor3.apply();
                startActivity(new Intent(Quizzes_menu.this, QuizInstructions.class));

            }
        });

        lockAndUnlockModules();

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
        startActivity(new Intent(Quizzes_menu.this, Dashboard.class));
    }

    private void lockAndUnlockModules() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(getPackageName() + Constant.MY_LEVEL_PREFS, Context.MODE_PRIVATE);
        M1 = sharedPreferences.getInt("unlockMod1", 0);
        M2 = sharedPreferences.getInt("unlockMod2", 0);
        M3 = sharedPreferences.getInt("unlockMod3", 0);

        if (M1 == 1) {
            cardViewMod1.setClickable(true);
        }
        if (M2 == 1) {
            cardViewMod2.setClickable(true);
            //lock mod1 test
            cardViewMod1.setClickable(false);
            cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
        } else if (M2 == 0) {
            cardViewMod2.setClickable(false);
            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
        }
        if (M3 == 1) {
            cardViewMod3.setClickable(true);
            //lock mod2 test
            cardViewMod2.setClickable(false);
            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
        } else if (M3 == 0) {
            cardViewMod3.setClickable(false);
            cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
        }
        if (M1 == 1 && M2 == 1 && M3 == 1) {
            allTestsCompleted = true;
            cardViewMod3.setClickable(false);
            cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
        }
    }

    //testing method to view which values are passed
    public void LoadData(View view) {
        textV1.setText(String.valueOf(M1));
        textV2.setText(String.valueOf(M2));
        textV3.setText(String.valueOf(M3));
    }

}