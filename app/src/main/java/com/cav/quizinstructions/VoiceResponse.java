package com.cav.quizinstructions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;
import static com.cav.quizinstructions.Dashboard.dashboard_email;

public class VoiceResponse extends AppCompatActivity {

    public static final String SERVER_DASHBOARD = "https://phportal.net/driverph/dashboard_latest_module.php";

    MediaRecorder mediaRecorder = null;
    MediaPlayer mediaPlayer = null;
    Button start_rec,  play_rec, btn_back, btn_next;
    //  stop_rec,
    TextView textView, userName, chapVr;

    private final int MIC_PERMISSION_CODE= 1;
    public static String myLatestUserId;
    public static String myLatestAttempt;
    public static String myLatestChapter;
    public static String myLatestIsUnlocked;
    public static String myLatestIsCompleted;

    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_response);

        requestPermission();
        loadDataAllAttemptsAndLevels();

//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("Quick Recap");
        SharedPreferences sharedPreferences = getSharedPreferences("mySavedAttempt", MODE_PRIVATE);
        String uname = sharedPreferences.getString("username", "");
        SharedPreferences spr = getSharedPreferences("SharedPrefChapter", MODE_PRIVATE);
        String chapter = spr.getString("chapter", "");

        start_rec = findViewById(R.id.btn_start_record);
        textView = findViewById(R.id.textView_status);
        userName = findViewById(R.id.user_name);
        play_rec = findViewById(R.id.btn_playback);
        btn_next = findViewById(R.id.btn_next);
        chapVr = findViewById(R.id.chapterVR);

        userName.setText("Hi, " + uname);
//        userName.setText(AccountEdit.fname);
        chapVr.setText(chapter);
        textView.setVisibility(View.GONE);

        btn_next.setVisibility(View.INVISIBLE);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (lastModule == true){
                //    startActivity(new Intent(VoiceResponse.this, QuizInstructions.class));
//                  }else{
//                     startActivity(new Intent(VoiceResponse.this, Simulation.class));
//                  }
                SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
                int user_id = sharedPreferences.getInt("user_id", 0);
                Intent intent = new Intent(VoiceResponse.this, Dashboard.class);
                Bundle bundle = new Bundle();
                bundle.putInt("user_idFromServer", user_id);
                bundle.putInt("user_idFromDashboard", user_id);
                bundle.putString("email", dashboard_email);
                intent.putExtras(bundle);
                startActivity(intent);
               // startActivity(new Intent(VoiceResponse.this, Dashboard.class));
            }
        });


        final boolean[] clicked = {false};
        start_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked[0]){
                    clicked[0] = true;
                    start_rec.setSelected(true);
                    onRecord(true);
                }else{
                    stop();
                    clicked[0] = false;
                    start_rec.setSelected(false);
                }
            }
        });


        play_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_rec.isSelected()){
                    textView.setText(R.string.stop_rec_first_before_play);
                }else {
                    onPlay(true);
                }
            }
        });

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(VoiceResponse.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            record();
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.start_recording);
        } else {
            if (mediaRecorder != null){
                stop();
            }
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            play();
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.play_recording);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    textView.setText(R.string.playback_ended);
//                    Log.i("Completion Listener","Song Complete");
                    btn_next.setVisibility(View.VISIBLE);
                }
            });
        } else {
            stop();
        }
    }

    private void record(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(file);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    private void stop() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        textView.setVisibility(View.VISIBLE);
        textView.setText(R.string.rec_stop);
    }


    private void play() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    //load all data attempts and unlocked modules from web
    public void loadDataAllAttemptsAndLevels() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        Database db = new Database(this);
        db.Open();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.DEPRECATED_GET_OR_POST,
                SERVER_DASHBOARD,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(VoiceResponse.this, "Loading all attempts", Toast.LENGTH_SHORT).show();
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
                                myLatestChapter= menuitemArray.getJSONObject(i).getString("chapter");
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

                            Toast.makeText(VoiceResponse.this, "Fetched from attempts: " + myLatestUserId, Toast.LENGTH_SHORT).show();
                            Toast.makeText(VoiceResponse.this, "Latest Chapter: " + myLatestChapter, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(VoiceResponse.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", dashboard_email);
                Log.d("email", Login.email + "");
                Log.d("yes", "successful...");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



}
