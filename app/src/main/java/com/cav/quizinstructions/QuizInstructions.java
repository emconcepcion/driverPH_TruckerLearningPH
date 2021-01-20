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

import static com.cav.quizinstructions.Constant.Server_All_Attempts_URL;
import static com.cav.quizinstructions.Constant._1;
import static com.cav.quizinstructions.Constant._2;
import static com.cav.quizinstructions.Constant._3;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
import static com.cav.quizinstructions.Dashboard.dashboard_email;
import static com.cav.quizinstructions.Lessons_Menu.isFromLessonsMenu;


public class QuizInstructions extends AppCompatActivity {

    public static String chapter;
    Button buttonStartQuiz, back_btn;
    public static String myLatestUserId;
    public static String myLatestAttempt;
    public static String myLatestChapter;
    public static String myLatestIsUnlocked;
    public static String myLatestIsCompleted;
    public static String dash_email;

    // Adding HTTP Server URL to string variable.
    private final String QUESTIONS_URL = "https://phportal.net/driverph/questions.php";

    private TextView textViewChapter;
    public static TextView textViewModuleTitle;
    public TextView myEmailQuizInst, userIdQInst;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_instructions);
        Dashboard.getmInstanceActivity().loadDataAllAttemptsAndLevels();


        buttonStartQuiz = findViewById(R.id.btn_start_quiz);
        back_btn = findViewById(R.id.btn_back_to_take_quiz);
        textViewChapter = findViewById(R.id.textview_chapter_title);
        myEmailQuizInst = findViewById(R.id.myEmailQuizInst);
        userIdQInst = findViewById(R.id.myUserIdQuizInst);
        textViewModuleTitle = findViewById(R.id.textview_moduleTest);
//        textViewModuleTitle.setText();

        sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        String myEmail = sp.getString("email", "");
        myEmailQuizInst.setText(myEmail);

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt("user_id", 0);
        userIdQInst.setText(String.valueOf(uid));

        if (Lessons_Menu.isFromLessonsMenu) {
            SharedPreferences sp1 = getApplicationContext()
                    .getSharedPreferences("SharedPrefChapter", Context.MODE_PRIVATE);
            String lessonChap = sp1.getString("chapter", "");
            textViewChapter.setText(lessonChap);
        }else if (Quizzes_menu.isFromQuizMenu){
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
                backToLessons();
            }
        });
        getJSON(QUESTIONS_URL);
    //    loadDataAllAttemptsAndLevels();
    }

    private void backToLessons() {
        startActivity(new Intent(QuizInstructions.this, Lessons_Basic_Content.class));
    }

    private void startQuiz() {
        String chapTitle = textViewChapter.getText().toString();
        if (checkNetworkConnection()) {
            Intent i = new Intent(QuizInstructions.this, QuizActivity.class);
            Bundle b = new Bundle();
            b.putString("chapter", chapTitle);
            i.putExtras(b);
            startActivity(i);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(QuizInstructions.this).create();
            alertDialog.setTitle("Log in to Continue");
            alertDialog.setMessage("Please connect to the internet and log in before clicking \"Start the quiz\"");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(QuizInstructions.this, Login.class));
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
                Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
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

                Log.d("questionText " + i,
                        menuitemArray.getJSONObject(i).getString("questionText")
                                .toString());
                Log.d("choiceA: " + i, menuitemArray.getJSONObject(i)
                        .getString("choiceA"));
                Log.d("choiceB: " + i, menuitemArray.getJSONObject(i)
                        .getString("choiceB"));
                Log.d("choiceC: " + i, menuitemArray.getJSONObject(i)
                        .getString("choiceC"));
                Log.d("choiceD: " + i, menuitemArray.getJSONObject(i)
                        .getString("choiceD"));
                Log.d("correctAnswer: " + i, menuitemArray.getJSONObject(i)
                        .getString("correctAnswer"));
                Log.d("module" + i, menuitemArray.getJSONObject(i)
                        .getString("module"));
                Log.d("moduleName" + i, menuitemArray.getJSONObject(i)
                        .getString("moduleName"));
                Log.d("image" + i, menuitemArray.getJSONObject(i)
                        .getString("image"));

                String question = menuitemArray.getJSONObject(i).getString("questionText");
                String option1 = menuitemArray.getJSONObject(i).getString("choiceA");
                String option2 = menuitemArray.getJSONObject(i).getString("choiceB");
                String option3 = menuitemArray.getJSONObject(i).getString("choiceC");
                String option4 = menuitemArray.getJSONObject(i).getString("choiceD");
                String answer_nr = menuitemArray.getJSONObject(i).getString("correctAnswer");
                String chapter = menuitemArray.getJSONObject(i).getString("module");
                String moduleName = menuitemArray.getJSONObject(i).getString("moduleName");
                String image = menuitemArray.getJSONObject(i).getString("imageUrl");
                Question q1 = new Question(question, option1, option2, option3, option4,
                        Integer.parseInt(answer_nr), chapter, moduleName, image);
                db.addQuestion(q1);
            }

        } catch (Exception je) {

            Log.d("json error...", je + "");
        }
        Log.d("Inside aysnc task", "inside asynctask...");
     //   db.close();
    }

    //load all data attempts and unlocked modules from web
    public void loadDataAllAttemptsAndLevels() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        Database db = new Database(this);
        db.Open();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.DEPRECATED_GET_OR_POST,
                Server_All_Attempts_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(QuizInstructions.this, "Loading all attempts", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObj = new JSONObject(s);

                            JSONArray menuitemArray = jObj.getJSONArray("data");

                            for (int i = 0; i < menuitemArray.length(); i++) {

                                Log.d("user_id " + i,
                                        menuitemArray.getJSONObject(i).getString("user_id")
                                                .toString());
                                Log.d("email: " + i, menuitemArray.getJSONObject(i)
                                        .getString("email"));
                                Log.d("score: " + i, menuitemArray.getJSONObject(i)
                                        .getString("score"));
                                Log.d("num_of_items: " + i, menuitemArray.getJSONObject(i)
                                        .getString("num_of_items"));
                                Log.d("chapter: " + i, menuitemArray.getJSONObject(i)
                                        .getString("chapter"));
                                Log.d("num_of_attempt: " + i, menuitemArray.getJSONObject(i)
                                        .getString("num_of_attempt"));
                                Log.d("duration: " + i, menuitemArray.getJSONObject(i)
                                        .getString("duration"));
                                Log.d("date_taken: " + i, menuitemArray.getJSONObject(i)
                                        .getString("date_taken"));
                                Log.d("isUnlocked: " + i, menuitemArray.getJSONObject(i)
                                        .getString("isUnlocked"));
                                Log.d("isCompleted: " + i, menuitemArray.getJSONObject(i)
                                        .getString("isCompleted"));

                                myLatestUserId = menuitemArray.getJSONObject(i).getString("user_id");
                                String email = menuitemArray.getJSONObject(i).getString("email");
                                String score = menuitemArray.getJSONObject(i).getString("score");
                                String num_items = menuitemArray.getJSONObject(i).getString("num_of_items");
                                myLatestChapter = menuitemArray.getJSONObject(i).getString("chapter");
                                myLatestAttempt = menuitemArray.getJSONObject(i).getString("num_of_attempt");
                                String duration = menuitemArray.getJSONObject(i).getString("duration");
                                String date_taken = menuitemArray.getJSONObject(i).getString("date_taken");
                                myLatestIsUnlocked = menuitemArray.getJSONObject(i).getString("isUnlocked");
                                myLatestIsCompleted = menuitemArray.getJSONObject(i).getString("isCompleted");
                                MyScoresServer mS1 = new MyScoresServer(Integer.parseInt(myLatestUserId), email, Integer.parseInt(score),
                                        Integer.parseInt(num_items), myLatestChapter, Integer.parseInt(myLatestAttempt), duration,
                                        date_taken, Integer.parseInt(myLatestIsUnlocked), Integer.parseInt(myLatestIsCompleted));
                                db.addScoresServer(mS1);
                            }

                            Toast.makeText(QuizInstructions.this, "Fetched from attempts: " + myLatestUserId, Toast.LENGTH_SHORT).show();
                            Toast.makeText(QuizInstructions.this, "Latest Chapter: " + myLatestChapter, Toast.LENGTH_SHORT).show();
                            Dashboard.recentModule.setText(myLatestChapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(QuizInstructions.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String fetchedChap = Dashboard.myLatestChapter;
                params.put("email", dashboard_email);
                params.put("chapter", fetchedChap);
                Log.d("email", dashboard_email + "");
                Log.d("yes", "successful...");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
