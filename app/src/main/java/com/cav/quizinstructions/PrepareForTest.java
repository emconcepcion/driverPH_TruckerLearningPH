package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
import static com.cav.quizinstructions.Dashboard.dashboard_email;
import static com.cav.quizinstructions.Dashboard.thisUserId;
import static com.cav.quizinstructions.Lessons_Menu.isFromLessonsMenu;


public class PrepareForTest extends AppCompatActivity {

    public static String chapter;
    Button buttonStartQuiz, back_btn;
    private TextView textViewChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_for_test);
        Dashboard.getmInstanceActivity().loadDataAllAttemptsAndLevels();

        buttonStartQuiz = findViewById(R.id.btn_start_myTest);
        back_btn = findViewById(R.id.btn_back_to_quizMenu);
        textViewChapter = findViewById(R.id.textview_module_title);

        if (Lessons_Menu.isFromLessonsMenu) {
            SharedPreferences sp1 = getApplicationContext()
                    .getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
            String lessonChap = sp1.getString("chapter", "");
            textViewChapter.setText(lessonChap);
        } else if (Quizzes_menu.isFromQuizMenu) {
            SharedPreferences sp2 = getApplicationContext()
                    .getSharedPreferences("ChapFromQuizzes", Context.MODE_PRIVATE);
            String qChapter = sp2.getString("Qchapter", "");
            textViewChapter.setText(qChapter);
        }

        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToQuizMenu();
            }
        });
    }

    private void backToQuizMenu() {
        startActivity(new Intent(PrepareForTest.this, Dashboard.class));
    }

    private void startQuiz() {
        String chapTitle = textViewChapter.getText().toString();
        if (checkNetworkConnection()) {
            Intent i = new Intent(PrepareForTest.this, QuizActivity.class);
            Bundle b = new Bundle();
            b.putString("chapter", chapTitle);
            Dashboard.recentModule.setText(chapTitle);
            i.putExtras(b);
            startActivity(i);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(PrepareForTest.this).create();
            alertDialog.setTitle("Log in to Continue");
            alertDialog.setMessage("Please connect to the internet and log in before clicking \"Start the quiz\"");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(PrepareForTest.this, Login.class));
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    //check for internet connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}