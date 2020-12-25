package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class QuizInstructions extends AppCompatActivity {

    public static String chapter;
    Button buttonStartQuiz, back_btn;

    // Adding HTTP Server URL to string variable.
    private final String QUESTIONS_URL = "https://phportal.net/driverph/get_qa_all.php";

    private TextView textViewChapter;

    boolean sourceLesson, sourceQuizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_instructions);
        buttonStartQuiz = findViewById(R.id.btn_start_quiz);
        back_btn = findViewById(R.id.btn_back_to_take_quiz);
        textViewChapter = findViewById(R.id.textview_chapter_title);

        if (Quizzes_menu.isFromQuizMenu){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String chapter = bundle.getString("chapter");
            textViewChapter.setText(chapter);
        }else{
            SharedPreferences sp = getApplicationContext().getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
            String lessonChap = sp.getString("chapter", "");
            textViewChapter.setText(lessonChap);
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
                backToLessons();
            }
        });
        getJSON(QUESTIONS_URL);

    }

    private void backToLessons() {
        startActivity(new Intent(QuizInstructions.this, Lessons_Basic_Content.class));
    }

    private void startQuiz() {
        String chapTitle = textViewChapter.getText().toString();
        Intent i = new Intent(QuizInstructions.this, QuizActivity.class);
        Bundle b = new Bundle();
        b.putString("chapter", chapTitle);
        i.putExtras(b);
        startActivity(i);
    }

    public void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    parserQuestionsFromString(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                chapter = textViewChapter.getText().toString();

                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    void parserQuestionsFromString(String refjson) {
        String stringjson = refjson;
        Database db = new Database(this);
        db.Open();
        try {
            JSONObject jObj = new JSONObject(stringjson);

            JSONArray menuitemArray = jObj.getJSONArray("data");

            for (int i = 0; i < menuitemArray.length(); i++) {

                Log.d("question_text " + i,
                        menuitemArray.getJSONObject(i).getString("question_text")
                                .toString());
                Log.d("option1: " + i, menuitemArray.getJSONObject(i)
                        .getString("option1"));
                Log.d("option2: " + i, menuitemArray.getJSONObject(i)
                        .getString("option2"));
                Log.d("option3: " + i, menuitemArray.getJSONObject(i)
                        .getString("option3"));
                Log.d("option4: " + i, menuitemArray.getJSONObject(i)
                        .getString("option4"));
                Log.d("answer_nr: " + i, menuitemArray.getJSONObject(i)
                        .getString("answer_nr"));
                Log.d("chapter: " + i, menuitemArray.getJSONObject(i)
                        .getString("chapter"));

                String question = menuitemArray.getJSONObject(i).getString("question_text");
                String option1 = menuitemArray.getJSONObject(i).getString("option1");
                String option2 = menuitemArray.getJSONObject(i).getString("option2");
                String option3 = menuitemArray.getJSONObject(i).getString("option3");
                String option4 = menuitemArray.getJSONObject(i).getString("option4");
                String answer_nr = menuitemArray.getJSONObject(i).getString("answer_nr");
                String chapter = menuitemArray.getJSONObject(i).getString("chapter");
                Question q1 = new Question(question, option1, option2, option3, option4, Integer.parseInt(answer_nr), chapter);
                db.addQuestion(q1);
            }

        } catch (Exception je) {

            Log.d("json error...", je + "");
        }
        Log.d("Inside aysnc task", "inside asynctask...");
        db.close();
    }
}
