package com.cav.quizinstructions;

import android.Manifest;
import android.annotation.SuppressLint;
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

import java.io.File;
import java.io.IOException;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Dashboard.Uid_PREFS;

public class VoiceResponse extends AppCompatActivity {

    MediaRecorder mediaRecorder = null;
    MediaPlayer mediaPlayer = null;
    Button start_rec,  play_rec, btn_back, btn_next;
    //  stop_rec,
    TextView textView, userName, chapVr;

    private final int MIC_PERMISSION_CODE= 1;


    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_response);

        requestPermission();
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
        btn_back = findViewById(R.id.btn_back_to_lesson);
        btn_next = findViewById(R.id.btn_next);
        chapVr = findViewById(R.id.chapterVR);

        userName.setText("Hi, " + uname);
//        userName.setText(AccountEdit.fname);
        chapVr.setText(chapter);
        textView.setVisibility(View.GONE);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VoiceResponse.this, Dashboard.class));
                Toast.makeText(VoiceResponse.this, "back button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        btn_next.setVisibility(View.INVISIBLE);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (lastModule == true){
                //    startActivity(new Intent(VoiceResponse.this, QuizInstructions.class));
//                  }else{
//                     startActivity(new Intent(VoiceResponse.this, Simulation.class));
//                  }
//                SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
//                String user_id = sharedPreferences.getString("user_id", "");
                String chapVR = chapVr.getText().toString();
                startActivity(new Intent(VoiceResponse.this, QuizInstructions.class));
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
        }else{

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



}
