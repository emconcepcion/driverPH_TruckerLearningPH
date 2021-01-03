package com.cav.quizinstructions;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
import static com.cav.quizinstructions.Dashboard.emptyUserIdFromDb;
import static com.cav.quizinstructions.Dashboard.thisUserId;
import static com.cav.quizinstructions.Dashboard.user_id;

public class Quizzes_menu extends AppCompatActivity {

    private final int REQUEST_CODE = 101;
    public static CardView cardViewMod1;
    public static CardView cardViewMod2;
    public static CardView cardViewMod3;
    TextView tChapter, mylatestModLocked, mylatestModCompleted;
    Button btn_leaderBoard;
    Button btn_list_completed_quizzes;
    public static boolean isFromQuizMenu;
    @SuppressLint("StaticFieldLeak")
    public static TextView myEmailQMenu, userIdQMenu, uidDb_txt, updatedChapter;
    public static boolean allTestsCompleted;
    public static int  latestUnlocked, latestCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_menu);
    //    Dashboard.getmInstanceActivity().loadDataAllAttemptsAndLevels();

        tChapter = findViewById(R.id.tChap);

        cardViewMod1 = findViewById(R.id.cardView_basic_competencies);
        cardViewMod2 = findViewById(R.id.cardView_common_competencies);
        cardViewMod3 = findViewById(R.id.cardView_core_competencies);
        cardViewMod1.setClickable(true);
        cardViewMod2.setClickable(false);
        cardViewMod3.setClickable(false);

        btn_leaderBoard = findViewById(R.id.button5);
        btn_list_completed_quizzes = findViewById(R.id.button6);
        myEmailQMenu = findViewById(R.id.email_qMenu);
        userIdQMenu = findViewById(R.id.txt_userIdQuiz);
        uidDb_txt = findViewById(R.id.txt_uidDbQuizMenu);
        updatedChapter = findViewById(R.id.myprogresslesson);

        mylatestModLocked = findViewById(R.id.latestMod1Locked);
        mylatestModCompleted = findViewById(R.id.latestMod1Completed);
        mylatestModLocked.setText(Dashboard.myLatestIsUnlocked);
        mylatestModCompleted.setText(Dashboard.myLatestIsCompleted);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        int uidDb = b.getInt("user_idFromServer");
        int uid = b.getInt("user_idFromDashboard");
        uidDb_txt.setText(String.valueOf(uidDb));
        userIdQMenu.setText(String.valueOf(uid));

        Lessons_Menu.isFromLessonsMenu = false;
        isFromQuizMenu = true;

//        if (allTestsCompleted && QuizActivity.unlocked) {
//            cardViewMod3.setClickable(false);
//            cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//            StyleableToast.makeText(getApplicationContext(), Quizzes_menu.this.getString(R.string.allTestsDone),
//                    Toast.LENGTH_LONG, R.style.toastStyle).show();
//        }

        cardViewMod1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText(Constant._1);
                Dashboard.recentModule.setText(Constant._1);
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
                tChapter.setText(Constant._2);
                Dashboard.recentModule.setText(Constant._2);
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
                tChapter.setText(Constant._3);
                Dashboard.recentModule.setText(Constant._3);
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Quizzes_menu.this, Dashboard.class));
        finish();
    }


    private void lockAndUnlockModules() {

        // locked 0, unlocked 1
        int currUser = Integer.parseInt(userIdQMenu.getText().toString());
        String currChap = tChapter.getText().toString();
        int dbUser = Integer.parseInt(uidDb_txt.getText().toString());
        boolean sameUser = String.valueOf(dbUser).equals(String.valueOf(currUser));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        latestUnlocked = bundle.getInt("myLatestIsUnlocked");
        latestCompleted = bundle.getInt("myLatestIsCompleted");
        String myLatestChapter = bundle.getString("myLatestChapter");

        //lock all completed quizzes read from database
        switch (myLatestChapter){
            case Constant._1:
                if (sameUser && myLatestChapter.equals(Constant._1)
                        &&  latestUnlocked == 0 && latestCompleted == 1){
                    cardViewMod2.setClickable(true);
                    cardViewMod1.setClickable(false);
                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    cardViewMod3.setClickable(false);
                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
                }else{
                    cardViewMod2.setClickable(false);
                    cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
                    cardViewMod3.setClickable(false);
                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
                }
                break;
            case Constant._2:
                if (sameUser && myLatestChapter.equals(Constant._2)
                        &&  latestUnlocked == 0 && latestCompleted == 1) {
                    cardViewMod3.setClickable(true);
                    cardViewMod1.setClickable(false);
                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    cardViewMod2.setClickable(false);
                    cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                }else {
                    cardViewMod1.setClickable(false);
                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    cardViewMod3.setClickable(false);
                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
                }
                break;
            case Constant._3:
                if (sameUser && myLatestChapter.equals(Constant._3)
                        &&  latestUnlocked == 0 && latestCompleted == 1) {
                    cardViewMod3.setClickable(false);
                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    cardViewMod1.setClickable(false);
                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    cardViewMod2.setClickable(false);
                    cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                }
                break;
        }

        if (sameUser && latestUnlocked == 1 && latestCompleted == 0){
            cardViewMod1.setClickable(true);
        }

// && latestUnlocked == 0 && latestCompleted == 1
//        if (QuizResults.M1isPassed || sameUser && latestUnlocked == 0 && latestCompleted == 1) {
//            cardViewMod2.setClickable(true);
//            cardViewMod3.setClickable(false);
//            //tag mod1 as passed
//            cardViewMod1.setClickable(false);
//            cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//        }else if(!QuizResults.M1isPassed && sameUser){
//            //lock next modules
//            cardViewMod2.setClickable(false);
//            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
//        }
//
//        // && myLatestModule == 1 && hasPassedTest == 0
//        if (QuizResults.M2isPassed &&
//                sameUser && latestUnlocked == 0 && latestCompleted == 1) {
//            cardViewMod3.setClickable(true);
//            //tag mod1 and mod2 as passed
//            cardViewMod1.setClickable(false);
//            cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//            cardViewMod2.setClickable(false);
//            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//        }else if(!QuizResults.M2isPassed && sameUser){
//            //lock mod3
//            cardViewMod3.setClickable(false);
//            cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
//        }
//
//        if (QuizResults.M1isPassed && QuizResults.M2isPassed && QuizResults.M3isPassed && sameUser) {
//            allTestsCompleted = true;
//            switch (currChap) {
//                case Constant.MOD_1:
//                    cardViewMod1.setClickable(false);
//                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//                    break;
//                case Constant.MOD_2:
//                    cardViewMod2.setClickable(false);
//                    cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//                    break;
//                case Constant.MOD_3:
//                    cardViewMod3.setClickable(false);
//                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
//            }
//        }
    }

    //update unlocked and locked modules for each UNLOCK NEXT MODULE


    //check for internet connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }



}