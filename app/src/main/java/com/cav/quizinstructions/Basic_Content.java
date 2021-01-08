package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cav.quizinstructions.Constant.SP_LESSONID;

public class Basic_Content extends AppCompatActivity {
    public static WeakReference<Basic_Content> weakActivityBasicContent;

    public static String module, moduleName;
    ListView content;
    public static List<String> al, lesson_id_key;
    public static String currentLesson, currLessonId;
    public static TextView currLesson, Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic__content);
        weakActivityBasicContent = new WeakReference<>(Basic_Content.this);


        content = findViewById(R.id.listview);
        currLesson = findViewById(R.id.currLessonBasic);
        Email = findViewById(R.id.emailLesson1);
        Email.setText(Dashboard.dashboard_email);

        module = getIntent().getStringExtra("module");
        moduleName = getIntent().getStringExtra("moduleName");
        getData();

        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Basic_Content.this, al.get(position), Toast.LENGTH_SHORT).show();
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String currentDate = simpleDate.format(new Date());

                //my progress
                Intent intent = new Intent(Basic_Content.this, Lessons_Basic_Content.class);
                Bundle extras = new Bundle();
                extras.putString("module", module);
                extras.putString("course", al.get(position));
                extras.putString("lessonId", lesson_id_key.get(position));
                extras.putString("dateStarted", currentDate);
                extras.putString("dateFinished", currentDate);
                extras.putInt("status", 1);
                currLessonId = lesson_id_key.get(position);
                extras.putString("currLessonId", currLessonId);
                intent.putExtras(extras);
                startActivity(intent);

                currentLesson = al.get(position);
                currLesson.setText(String.valueOf(currentLesson));

                SharedPreferences sharedPreferences = getSharedPreferences(SP_LESSONID, MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("lessonTitle", currentLesson);
                myEdit.putString("moduleName", moduleName);
                myEdit.apply();

                Dashboard.activeLesson.setText(currentLesson);
                Dashboard.activeModule.setText(moduleName);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (Lessons_Menu.isFromLessonsMenu){
            startActivity(new Intent(Basic_Content.this, Lessons_Menu.class));
        }else{
            startActivity(new Intent(Basic_Content.this, Dashboard.class));
        }
        finish();
    }

    public void getData() {
        String value = module;
        String url = Config5.DATA_URL + value;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Basic_Content.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showJSON(String response) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        al = new ArrayList<String>();
        lesson_id_key = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config5.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String title = jo.getString(Config5.KEY_TITLE);

                al.add(jo.getString(Config5.KEY_TITLE));
                lesson_id_key.add(jo.getString(Config5.KEY_ID));

                final HashMap<String, String> employees = new HashMap<>();
                employees.put(Config5.KEY_TITLE, title);

                list.add(employees);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                Basic_Content.this, list, R.layout.my_list,
                new String[]{Config5.KEY_TITLE, Config5.KEY_ID},
                new int[]{R.id.title});

        content.setAdapter(adapter);
    }

    public static Basic_Content getmInstanceActivity() {
        return weakActivityBasicContent.get();
    }
}