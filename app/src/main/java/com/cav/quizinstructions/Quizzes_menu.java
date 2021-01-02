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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
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
    public static TextView myEmailQMenu, userIdQMenu, uidDb_txt;
    public static boolean allTestsCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_menu);

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
        uidDb_txt = findViewById(R.id.txt_uidDbQuiz);
        mylatestModLocked = findViewById(R.id.latestMod1Locked);
        mylatestModCompleted = findViewById(R.id.latestMod1Completed);
        mylatestModLocked.setText(Dashboard.myLatestIsUnlocked);
        mylatestModCompleted.setText(Dashboard.myLatestIsCompleted);

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Dashboard.dashboard_email = sp.getString(EMAIL, "");
        myEmailQMenu.setText(Dashboard.dashboard_email);

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt("user_id", 0);
        int uidDb = sharedPreferences.getInt("uidFromServer", 0);
        userIdQMenu.setText(String.valueOf(uid));
        uidDb_txt.setText(String.valueOf(uidDb));

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
                String chapTest1 = tChapter.getText().toString();
                SharedPreferences sp1 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp1.edit();
                editor1.putString("Qchapter", chapTest1);
                editor1.apply();
                startActivity(new Intent(Quizzes_menu.this, PrepareForTest.class));
            }
        });

        cardViewMod2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText(Constant._2);
                String chapTest2 = tChapter.getText().toString();
                SharedPreferences sp2 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putString("Qchapter", chapTest2);
                editor2.apply();
                startActivity(new Intent(Quizzes_menu.this, PrepareForTest.class));

            }
        });

        cardViewMod3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromQuizMenu = true;
                tChapter.setText(Constant._3);
                String chapTest3 = tChapter.getText().toString();
                SharedPreferences sp3 = getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = sp3.edit();
                editor3.putString("Qchapter", chapTest3);
                editor3.apply();
                startActivity(new Intent(Quizzes_menu.this, PrepareForTest.class));
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
    }

    private void lockAndUnlockModules() {


        // locked 0, unlocked 1
        int myLatestModule = QuizStatusList.isLockedA;
        int hasPassedTest = QuizStatusList.isCompletedA;
        int currUser = Integer.parseInt(userIdQMenu.getText().toString());
        String currChap = tChapter.getText().toString();
        int dbUser = Integer.parseInt(uidDb_txt.getText().toString());
        boolean sameUser = String.valueOf(dbUser).equals(String.valueOf(currUser));

        if (sameUser && myLatestModule == 0 && hasPassedTest == 1){
            switch (Dashboard.myLatestChapter){
                case Constant._1:
                    Quizzes_menu.cardViewMod1.setClickable(false);
                    Quizzes_menu.cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    break;
                case Constant._2:
                    Quizzes_menu.cardViewMod2.setClickable(false);
                    Quizzes_menu.cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    break;
                case Constant._3:
                    Quizzes_menu.cardViewMod3.setClickable(false);
                    Quizzes_menu.cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    break;
            }
        }

        if (sameUser && hasPassedTest == 0){
        cardViewMod1.setClickable(true);
        }

        if (QuizResults.M1isPassed  && sameUser) {
            Quizzes_menu.cardViewMod2.setClickable(true);
            //tag mod1 as passed
            Quizzes_menu.cardViewMod1.setClickable(false);
            Quizzes_menu.cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
        }else{
            //lock next modules
            cardViewMod2.setClickable(false);
            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
        }

        if (QuizResults.M2isPassed && sameUser) {
            cardViewMod3.setClickable(true);
            //tag mod1 and mod2 as passed
            cardViewMod1.setClickable(false);
            cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
            cardViewMod2.setClickable(false);
            cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
        }else{
            //lock mod3
            cardViewMod3.setClickable(false);
            cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.mod_lock));
        }

        if (QuizResults.M1isPassed && QuizResults.M2isPassed && QuizResults.M3isPassed && sameUser) {
            allTestsCompleted = true;
            switch (currChap) {
                case Constant.MOD_1:
                    cardViewMod1.setClickable(false);
                    cardViewMod1.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    break;
                case Constant.MOD_2:
                    cardViewMod2.setClickable(false);
                    cardViewMod2.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
                    break;
                case Constant.MOD_3:
                    cardViewMod3.setClickable(false);
                    cardViewMod3.setBackground(ContextCompat.getDrawable(this, R.drawable.passed_locked));
            }
        }
    }

    //update unlocked and locked modules for each UNLOCK NEXT MODULE
    public void updateUnlockedModuleToServer(int userId, String chap, int isUnLocked, int isCompleted) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DbContract.ScoresTable.SERVER_UPDATE_PROGRESS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //get response from jsonobject
                                String Response = jsonObject.getString("response");
                                //check response from server
                                if (Response.equals("OK")) {
                                } else { //for server error, unable to save, will be handled by saveToAppServer
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof TimeoutError) {
                        Toast.makeText(Quizzes_menu.this, "Timeout error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        checkNetworkConnection();
                        Toast.makeText(Quizzes_menu.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(Quizzes_menu.this, "Auth error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(Quizzes_menu.this, "Server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(Quizzes_menu.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(Quizzes_menu.this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                //body of the string request
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", Dashboard.dashboard_email);
                    params.put("chapter", chap);
                    params.put("isUnlocked", String.valueOf(isUnLocked));
                    params.put("isCompleted", String.valueOf(isCompleted));
                    return params;
                }
            };
            MySingleton.getInstance(Quizzes_menu.this).addToRequestQueue(stringRequest);
            Toast.makeText(this, "Updated unlocked modules.", Toast.LENGTH_SHORT).show();
        }
    }

    //check for internet connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


}