package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static com.cav.quizinstructions.Dashboard.dashboard_email;

public class CompletedQuizzes extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Score> arrayList = new ArrayList<>();
    ArrayList<MyScoresServer> myScoresServerArrayList = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;
    Button summary_btn;
    private final String SCORES_URL = "https://phportal.net/driverph/scoresOnline.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_quizzes);

        summary_btn = findViewById(R.id.btn_summarized_scorelist);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocalStorage();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };
        getJSON(SCORES_URL);

        summary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompletedQuizzes.this, SummarizedScoresServer.class);
                Bundle extras = new Bundle();
                extras.putString("email", dashboard_email);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void onClick (View v){
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        StyleableToast.makeText(getApplicationContext(), CompletedQuizzes.this.getString(R.string.list_updated),
                Toast.LENGTH_LONG, R.style.toastStyle).show();
    }

    public void readFromLocalStorage(){

        //clear data from arraylist
        arrayList.clear();
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //read using cursor
        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while(cursor.moveToNext()){
            int userId = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_USER_ID));
            String email = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_EMAIL));
            int score = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_SCORE));
            int num_items = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ITEMS));
            String chap = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_CHAPTER));
            int num_attempt = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ATTEMPT));
            String duration = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_DURATION));
            String date_Taken = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_DATE_TAKEN));
            int isLocked = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_IS_LOCKED));
            int isCompleted = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_IS_COMPLETED));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.SYNC_STATUS));

            arrayList.add(new Score(userId,email, score, num_items, chap, num_attempt, duration,
                    date_Taken, isLocked, isCompleted, sync_status));
        }

        //adapter.notifyDataSetChanged();
        Collections.reverse(arrayList);
        cursor.close();
        dbHelper.close();
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
                Log.d("date_taken: " + i, menuitemArray.getJSONObject(i)
                        .getString("date_taken"));
                Log.d("isLocked: " + i, menuitemArray.getJSONObject(i)
                        .getString("isLocked"));
                Log.d("isCompleted: " + i, menuitemArray.getJSONObject(i)
                        .getString("isCompleted"));

                String userId = menuitemArray.getJSONObject(i).getString("user_id");
                String email = menuitemArray.getJSONObject(i).getString("email");
                String score = menuitemArray.getJSONObject(i).getString("score");
                String num_items = menuitemArray.getJSONObject(i).getString("num_of_items");
                String chap = menuitemArray.getJSONObject(i).getString("chapter");
                String num_of_attempt = menuitemArray.getJSONObject(i).getString("num_of_attempt");
                String duration = menuitemArray.getJSONObject(i).getString("duration");
                String date_taken = menuitemArray.getJSONObject(i).getString("date_taken");
                String isLocked = menuitemArray.getJSONObject(i).getString("isLocked");
                String isCompleted = menuitemArray.getJSONObject(i).getString("isLocked");
                MyScoresServer s1 = new MyScoresServer(Integer.parseInt(userId), email, Integer.parseInt(score),
                        Integer.parseInt(num_items), chap, Integer.parseInt(num_of_attempt), duration,
                        date_taken, Integer.parseInt(isLocked), Integer.parseInt(isCompleted));
                db.addScoresServer(s1);
            }

        } catch (Exception je) {

            Log.d("json error...", je + "");
        }
        Log.d("Inside aysnc task", "inside asynctask...");
        db.close();
    }
}