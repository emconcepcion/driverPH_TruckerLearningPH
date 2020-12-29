package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class Lessons_Basic_Content extends AppCompatActivity {
    WebView content;
    String module, course;
    private String retrieveUrl="https://phportal.net/driverph/retrieve_content.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons__basic__content);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        module = getIntent().getStringExtra("module");
        course = getIntent().getStringExtra("course");

        retrievedatas();
        //Toast.makeText(Lessons_Basic_Content.this, module + " " + course, Toast.LENGTH_SHORT).show();
        Button btnBack = findViewById(R.id.button6);
        content = findViewById(R.id.content);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Lessons_Basic_Content.this,Lessons_Menu.class));
            }
        });

        Button btnNext = findViewById(R.id.btn_to_voiceR);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Lessons_Basic_Content.this, VoiceResponse.class));
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
                        String htmlData = obj.getString("htmldata");
                        //Toast.makeText(Lessons_Basic_Content.this, htmlData, Toast.LENGTH_SHORT).show();
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
}