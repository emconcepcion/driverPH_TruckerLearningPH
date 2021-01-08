package com.cav.quizinstructions;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.cav.quizinstructions.Dashboard.dashboard_email;
import static com.cav.quizinstructions.Dashboard.nameVR;
import static com.cav.quizinstructions.Lessons_Menu.myEmailLesson;

public class Lessons_Basic_Content extends AppCompatActivity {
    WebView content;
    Button download, textspeech;
    TextToSpeech textToSpeech;
    private SeekBar mSeekBarPitch, mSeekBarSpeed;
    private TextView pitch, speed;
    public static String module, course, status, dateStarted, dateFinished,
                    htmlData, lessonpdf, lessonId, currentLessonId;
    public static TextView email_lesson;
    private String retrieveUrl="https://phportal.net/driverph/retrieve_content.php";
    public static String SERVER_USER_PROGRESS = "https://phportal.net/driverph/user_progress.php";
    Button btnBack, btnNext;
    public static String myProgressUserId;
    public static String myProgressModule;
    public static String myProgressLessonId;
    public static String myProgressStatus;
    public static String myProgressDateStarted;
    public static String myProgressDateFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons__basic__content);
//        Basic_Content.getmInstanceActivity().getData();

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityCompat.requestPermissions(Lessons_Basic_Content.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        module = getIntent().getStringExtra("module");
        course = getIntent().getStringExtra("course");
        lessonId = getIntent().getStringExtra("lessonId");
        status = String.valueOf(getIntent().getIntExtra("status", 0));
        dateStarted = getIntent().getStringExtra("dateStarted");
        dateFinished = getIntent().getStringExtra("dateFinished");
        currentLessonId = getIntent().getStringExtra("currLessonId");

        insertUserProgressModules();

        email_lesson = findViewById(R.id.emailBContent);
        email_lesson.setText(dashboard_email);
        Dashboard.resumeLesson.setText("Resume");
        retrievedatas();
        btnBack = findViewById(R.id.button4);
        content = (WebView) findViewById(R.id.content);

        btnNext = findViewById(R.id.btn_to_voiceR);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==btnNext)
                {
                    if(content.canGoForward())
                    {
                        content.goForward();
                    }
                }
                SharedPreferences sp = getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp.edit();
                editor1.putString("email", dashboard_email);
                editor1.putString("username", nameVR);
                editor1.apply();
                startActivity(new Intent(Lessons_Basic_Content.this, VoiceResponse.class));

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view==btnBack)
                {
                    if(content.canGoBack())
                    {
                        content.goBack();
                    }
                }
            }
        });

        download = findViewById(R.id.download);
        content = findViewById(R.id.content);
        textspeech = findViewById(R.id.textspeech);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        pitch = findViewById(R.id.pitch);
        speed = findViewById(R.id.speed);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.GERMAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        textspeech.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                createMyPDF();
            }
        });

        textspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txttospeech();
            }
        });

        mSeekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playtxtspeech();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playtxtspeech();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void retrievedatas(){
//        final String course1 = course;

        class show_prod extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("course", course);

                //returing the response
                return requestHandler.sendPostRequest(retrieveUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")){
                        lessonId = obj.getString("id");
                        htmlData = obj.getString("htmldata");
                        lessonpdf = obj.getString("pdflesson");
                        content.loadData(htmlData, "text/html", "UTF-8");
                    }
                } catch (Exception e ){
                    Toast.makeText(Lessons_Basic_Content.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createMyPDF(){

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595,842,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        Paint myPaint = new Paint();
        int x = 10, y=25;

        for (String line:lessonpdf.split("\n")){
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y+=myPaint.descent()-myPaint.ascent();
        }

        myPdfDocument.finishPage(myPage);

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DriverLesson");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        File fileLocation = new File(folder, course + ".pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(fileLocation));
            Toast.makeText(Lessons_Basic_Content.this, "Saved to Files -> DriverLesson folder.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            //myEditText.setText("ERROR");
        }
        myPdfDocument.close();
    }

    public void txttospeech() {
        String txttts = textspeech.getText().toString();

        if(txttts.equals("Play")) {
            textspeech.setText("Stop");
            textspeech.setTextColor(Color.TRANSPARENT);
//            download.setVisibility(View.INVISIBLE);
            pitch.setVisibility(View.VISIBLE);
            speed.setVisibility(View.VISIBLE);
            mSeekBarPitch.setVisibility(View.VISIBLE);
            mSeekBarSpeed.setVisibility(View.VISIBLE);
            playtxtspeech();
        }else {
            textToSpeech.stop();
            textspeech.setText("Play");
            textspeech.setTextColor(Color.TRANSPARENT);
            download.setVisibility(View.VISIBLE);
            pitch.setVisibility(View.INVISIBLE);
            speed.setVisibility(View.INVISIBLE);
            mSeekBarPitch.setVisibility(View.INVISIBLE);
            mSeekBarSpeed.setVisibility(View.INVISIBLE);
        }
    }

    public void playtxtspeech() {
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(lessonpdf, TextToSpeech.QUEUE_FLUSH, null);
    }

    //load all data for user's progress / latest module
    public void insertUserProgressModules() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        Database db = new Database(this);
        db.Open();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.DEPRECATED_GET_OR_POST,
                SERVER_USER_PROGRESS,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(Lessons_Basic_Content.this, "Loading all attempts", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jObj = new JSONObject("");

                            JSONArray menuitemArray = jObj.getJSONArray("data");

                            for (int i = 0; i < menuitemArray.length(); i++) {

                                Log.d("userId " + i,
                                        menuitemArray.getJSONObject(i).getString("userId"));
                                Log.d("module: " + i, menuitemArray.getJSONObject(i)
                                        .getString("module"));
                                Log.d("lessonId: " + i, menuitemArray.getJSONObject(i)
                                        .getString("lessonId"));
                                Log.d("status: " + i, menuitemArray.getJSONObject(i)
                                        .getString("status"));
                                Log.d("dateStarted: " + i, menuitemArray.getJSONObject(i)
                                        .getString("dateStarted"));
                                Log.d("dateFinished: " + i, menuitemArray.getJSONObject(i)
                                        .getString("dateFinished"));

                                myProgressUserId = menuitemArray.getJSONObject(i).getString("userId");
                                myProgressModule= menuitemArray.getJSONObject(i).getString("module");
                                myProgressLessonId = menuitemArray.getJSONObject(i).getString("lessonId");
                                myProgressStatus = menuitemArray.getJSONObject(i).getString("status");
                                myProgressDateStarted = menuitemArray.getJSONObject(i).getString("dateStarted");
                                myProgressDateFinished = menuitemArray.getJSONObject(i).getString("dateFinished");
                            }

                            Toast.makeText(Lessons_Basic_Content.this, "Fetched from Progress: " + myProgressUserId, Toast.LENGTH_SHORT).show();
                            Toast.makeText(Lessons_Basic_Content.this, "Progress Module: " + myProgressModule, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Lessons_Basic_Content.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", dashboard_email);
                params.put("module", module);
                params.put("lessonId", lessonId);
                params.put("status", status);
                params.put("dateStarted", dateStarted);
                params.put("dateFinished", dateFinished);
                Log.d("email", dashboard_email + "");
                Log.d("yes", "successful...");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}