package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.cav.quizinstructions.QuizActivity.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
import static com.cav.quizinstructions.Dashboard.thisUserId;

public class QuizResults extends AppCompatActivity {

    ListView listView;
    TextView score_result, chapter_name;
    Button btn_willRetake, btn_willReview, btn_willUnlock;

    public static boolean unlocked, unlockedForQuizMenu;
    public static boolean isRetake;
    public static boolean M1isPassed, M2isPassed, M3isPassed;
    public int attempt;
    TextView myEmailResult, myUserId;
    SharedPreferences sp;
    int UNLOCK_MOD2, UNLOCK_MOD3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btn_willRetake = findViewById(R.id.btn_retake);
        btn_willReview = findViewById(R.id.btn_review);
        btn_willUnlock = findViewById(R.id.btn_unlock);
        btn_willRetake.setVisibility(View.INVISIBLE);
        btn_willReview.setVisibility(View.INVISIBLE);
        btn_willUnlock.setVisibility(View.INVISIBLE);
        myEmailResult = findViewById(R.id.email_result);
        myUserId = findViewById(R.id.userId_result);

        sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        String myEmail = sp.getString("email", "");
        myEmailResult.setText(myEmail);

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt("user_id", 0);
        myUserId.setText(String.valueOf(uid));

        score_result = findViewById(R.id.txt_score_result);
        chapter_name = findViewById(R.id.chapter_name_result);
        listView = findViewById(R.id.list_view);
        String thisChapter = getIntent().getExtras().getString("chapter");
        chapter_name.setText(thisChapter);
        showResult();
    }

    public void showResult() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = (ArrayList<String>) getIntent().getSerializableExtra("askedQuestions");
        int txt_score_result = getIntent().getExtras().getInt("score");
        int items_test = getIntent().getExtras().getInt("items");

        score_result.setText(txt_score_result + "/" + items_test);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        if (txt_score_result > (items_test * 0.8)) {
            btn_willUnlock.setVisibility(View.VISIBLE);
            btn_willUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unlock();
                }
            });
        } else if (txt_score_result < (items_test * 0.8)) {
            btn_willRetake.setVisibility(View.VISIBLE);
            btn_willRetake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retake();
                }
            });

            btn_willReview.setVisibility(View.VISIBLE);
            btn_willReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    review();
                }
            });
        }

    }

    public void unlock() {
        unlocked = true;
        unlockedForQuizMenu = true;
        int txt_score_result = getIntent().getExtras().getInt("score");
        int items_test = getIntent().getExtras().getInt("items");
        String unlockNextModule = chapter_name.getText().toString();

        if (unlockNextModule.equals(Constant.MOD_1)) {
            //module 1 is active and need to unlock mod2
            UNLOCK_MOD2 = txt_score_result;
            if (UNLOCK_MOD2 > (items_test * 0.8)) {
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_1, 1, 1);
                M1isPassed = true;
            }else if (UNLOCK_MOD2 < (items_test * 0.8)){
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_1, 1, 0);
                M1isPassed = false;
            }
        } else if (unlockNextModule.equals(Constant.MOD_2)) {
            // module 2 is active and need to unlock mod3
            UNLOCK_MOD3 = txt_score_result;
            if (UNLOCK_MOD3 > (items_test * 0.8)) {
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_2, 0, 1);
                M2isPassed = true;
                Quizzes_menu.cardViewMod3.setClickable(true);
            }else if  (UNLOCK_MOD3 < (items_test * 0.8)){
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_2, 1, 0);
                M2isPassed = false;
            }
        } else if (unlockNextModule.equals(Constant.MOD_3)){
            // module 3 is active and need to lock all
            UNLOCK_MOD3 = txt_score_result;
            if (UNLOCK_MOD3 > (items_test * 0.8)) {
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_3, 0, 1);
                M3isPassed = true;
            }else if (UNLOCK_MOD3 < (items_test * 0.8)){
                updateUnlockedModuleToServer(thisUserId, Constant.MOD_3, 1, 0);
                M3isPassed = false;
            }
        }
        startActivity(new Intent(QuizResults.this, Quizzes_menu.class));
    }

    public void review() {
        startActivity(new Intent(QuizResults.this, Lessons_Menu.class));
    }

    public void retake() {
        isRetake = true;

        SharedPreferences sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        int incAttempt = sp.getInt("attempt", 1);
        String chapter = chapter_name.getText().toString();
        sp = getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("attempt", (++incAttempt));
        editor.putString("chapter", chapter);
        editor.apply();

        Intent resultIntent = new Intent(QuizResults.this, QuizActivity.class);
        startActivity(resultIntent);
    }

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
                        Toast.makeText(QuizResults.this, "Timeout error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        checkNetworkConnection();
                        Toast.makeText(QuizResults.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(QuizResults.this, "Auth error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(QuizResults.this, "Server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(QuizResults.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(QuizResults.this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                //body of the string request
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", Dashboard.dashboard_email);
                    params.put("chapter", chap);
                    params.put("isLocked", String.valueOf(isUnLocked));
                    params.put("isCompleted", String.valueOf(isCompleted));
                    return params;
                }
            };
            MySingleton.getInstance(QuizResults.this).addToRequestQueue(stringRequest);
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

