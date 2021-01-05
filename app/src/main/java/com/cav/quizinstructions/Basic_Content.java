package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Basic_Content extends AppCompatActivity {
    public static WeakReference<Basic_Content> weakActivity;
    public static String module;
    ListView content;
    List<String> al;
    public static String currentLesson;
    public static TextView currLesson, Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic__content);
        weakActivity = new WeakReference<>(Basic_Content.this);

        content = findViewById(R.id.listview);
        currLesson = findViewById(R.id.currLessonBasic);
        Email = findViewById(R.id.emailLesson1);
        Email.setText(Dashboard.dashboard_email);

        module = getIntent().getStringExtra("module");
        //Toast.makeText(Basic_Content.this, module, Toast.LENGTH_LONG).show();
        getData();

        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Basic_Content.this, al.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Basic_Content.this, Lessons_Basic_Content.class);
                Bundle extras = new Bundle();
                extras.putString("module", "1");
                extras.putString("course", al.get(position));
                intent.putExtras(extras);
                startActivity(intent);
                currentLesson = al.get(position);
                currLesson.setText(String.valueOf(currentLesson));
                Dashboard.activeLesson.setText(currentLesson);
                Dashboard.activeModule.setText(module);
                Lesson.progress_Module.setText(module);
            }
        });



    }

    public void setRecentActivity(){

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
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config5.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String title = jo.getString(Config5.KEY_TITLE);
                al.add(jo.getString(Config5.KEY_TITLE));
                final HashMap<String, String> employees = new HashMap<>();
                employees.put(Config5.KEY_TITLE, title);

                list.add(employees);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                Basic_Content.this, list, R.layout.my_list,
                new String[]{Config5.KEY_TITLE},
                new int[]{R.id.title});

        content.setAdapter(adapter);

    }

    public static Basic_Content getmInstanceActivity() {
        return weakActivity.get();
    }
}