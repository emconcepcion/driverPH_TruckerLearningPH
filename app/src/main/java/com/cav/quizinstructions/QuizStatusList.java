package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuizStatusList extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView Score, Email, Num_of_items, Chapter, Num_Of_Attempt, Date_Taken;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Score> arrayList = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;
    Button btn_view_result;
    Button refresh_list;
    SharedPreferences sp;

    boolean submittedScore;
    private boolean isRefreshedList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_status_list);

        refresh_list = findViewById(R.id.btn_refresh_list);
        btn_view_result = findViewById(R.id.btn_view_result);
        recyclerView = findViewById(R.id.recyclerView);
        Email = findViewById(R.id.txt_email);
        Score = findViewById(R.id.txt_score_for_syncing);
        Chapter = findViewById(R.id.txt_chapter_name);
        Num_of_items = findViewById(R.id.txt_numItems_recyView);
        Num_Of_Attempt = findViewById(R.id.txt_attempt_recyView);
        Date_Taken = findViewById(R.id.textView_date_of_quiz);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocalStorage();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
        String myEmail = sp.getString("email", "");
        Email.setText(myEmail);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };

        refresh_list.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (Chapter.getText().toString().contains("Unfinished Attempt:")) {
                    btn_view_result.setVisibility(View.INVISIBLE);
                    Button backToQMenu = findViewById(R.id.btn_backToQMenu);
                    backToQMenu.setVisibility(View.VISIBLE);
                    backToQMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(QuizStatusList.this, Quizzes_menu.class));
                        }
                    });
                }else{
                    submitScore();
                    submittedScore = true;
                    refresh_list.setVisibility(View.INVISIBLE);
                    btn_view_result.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_view_result.setVisibility(View.INVISIBLE);
        btn_view_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToQuizResults();
            }
        });

    }

    public void submitScore() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int txt_score_result = intent.getExtras().getInt("score");
        int txt_item_result = intent.getExtras().getInt("items");
//        String email_result = intent.getExtras().getString("myEmail");
        String chap_result = intent.getExtras().getString("chapter");
        int txt_attempt_result = intent.getExtras().getInt("attempt");
        String currentDate = intent.getExtras().getString("date_taken");

        // Email.setText(email_result);
        Score.setText("" + txt_score_result);
        Num_of_items.setText("" + txt_item_result);
        Chapter.setText(chap_result);
        Num_Of_Attempt.setText("" + txt_attempt_result);
        Date_Taken.setText(currentDate);

        String emailA = Email.getText().toString();
        int scoreA = Integer.parseInt(Score.getText().toString());
        int itemsA = Integer.parseInt(Num_of_items.getText().toString());
        String chapA = Chapter.getText().toString();
        int attemptsA = Integer.parseInt(Num_Of_Attempt.getText().toString());
        String dateTakenA = Date_Taken.getText().toString();

        saveToAppServer(emailA, scoreA, itemsA, chapA, attemptsA, dateTakenA);
    }

    public void readFromLocalStorage() {

        //clear data from arraylist
        arrayList.clear();
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //read using cursor
        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()) {
            String email = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_EMAIL));
            int score = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_SCORE));
            int num_items = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ITEMS));
            String chap = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_CHAPTER));
            int num_attempt = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_NUM_ATTEMPT));
            String date_Taken = cursor.getString(cursor.getColumnIndex(DbContract.ScoresTable.COLUMN_NAME_DATE_TAKEN));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.ScoresTable.SYNC_STATUS));

            arrayList.add(new Score(email, score, num_items, chap, num_attempt, date_Taken, sync_status));
        }

        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    public void saveToAppServer(String email, int score, int num_items, String chap,
                                int num_of_attempt, String date_taken) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.ScoresTable.SERVER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                //get response from jsonobject
                                String Response = jsonObject.getString("response");
                                //check response from server
                                if (Response.equals("OK")) {
                                    saveToLocalStorage(email, score, num_items, chap, num_of_attempt, date_taken, DbContract.SYNC_STATUS_SAVED);
                                    StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.synced),
                                            Toast.LENGTH_LONG, R.style.toastStyle).show();
                                } else { //for server error, unable to save, sav storage to local
                                    saveToLocalStorage(email, score, num_items, chap, num_of_attempt, date_taken, DbContract.SYNC_STATUS_FAILED);
                                    StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.sync_error_json),
                                            Toast.LENGTH_LONG, R.style.toastStyle).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    saveToLocalStorage(email, score, num_items, chap, num_of_attempt, date_taken, DbContract.SYNC_STATUS_FAILED);
                    StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.sync_error),
                            Toast.LENGTH_LONG, R.style.toastStyle).show();

                    if (error instanceof TimeoutError) {
                        Toast.makeText(QuizStatusList.this, "Timeout error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError) {
                        checkNetworkConnection();
                        Toast.makeText(QuizStatusList.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(QuizStatusList.this, "Auth error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(QuizStatusList.this, "Server error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(QuizStatusList.this, "Network error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(QuizStatusList.this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                //body of the string request
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("score", String.valueOf(score));
                    params.put("num_of_items", String.valueOf(num_items));
                    params.put("chapter", chap);
                    params.put("num_of_attempt", String.valueOf(num_of_attempt));
                    params.put("date_taken", date_taken);
                    return params;
                }
            };
            MySingleton.getInstance(QuizStatusList.this).addToRequestQueue(stringRequest);
            saveToLocalStorage(email, score, num_items, chap, num_of_attempt, date_taken, DbContract.SYNC_STATUS_SAVED);
            StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.saved),
                    Toast.LENGTH_LONG, R.style.toastStyle).show();

        } else { // no internet, save to SQLite
            saveToLocalStorage(email, score, num_items, chap, num_of_attempt, date_taken, DbContract.SYNC_STATUS_FAILED);
            StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.connect_to_net_to_save),
                    Toast.LENGTH_LONG, R.style.toastStyle).show();
        }

    }

    public void saveToLocalStorage(String email, int score, int num_items, String chap,
                                   int num_of_attempt, String date_taken, int sync_status) {

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //save to sqlite
        dbHelper.saveToLocalDatabase(email, score, num_items, chap, num_of_attempt, date_taken, sync_status, database);

        readFromLocalStorage();
        dbHelper.close();
    }

    //check for internet connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.check_net),
                Toast.LENGTH_LONG, R.style.toastStyle).show();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(new NetworkMonitor(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(broadcastReceiver, new IntentFilter(DbContract.ScoresTable.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void goToQuizResults() {

        Bundle bundle = getIntent().getExtras();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = bundle.getStringArrayList("askedQuestions");
        int score = bundle.getInt("score");
        int items = bundle.getInt("items");
        String chapter = bundle.getString("chapter");
        int nextAttempt = bundle.getInt("attempt");
        String email = bundle.getString("myEmail");

        int allAttempts = Integer.parseInt(Num_Of_Attempt.getText().toString());

        Intent intent = new Intent(QuizStatusList.this, QuizResults.class);
        intent.putStringArrayListExtra("askedQuestions", arrayList);
        intent.putExtra("score", score);
        intent.putExtra("items", items);
        intent.putExtra("chapter", chapter);
        intent.putExtra("attempt", nextAttempt);
        intent.putExtra("email", email);
        startActivity(intent);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() { {
//            submitScore();
//            btn_view_result.setVisibility(View.INVISIBLE);
//            refresh_list.setVisibility(View.GONE);
            TextView backAgain = findViewById(R.id.need_refresh);
            backAgain.setVisibility(View.VISIBLE);
            backAgain.setText("Please press the button to proceed.");
//            finish();
        }
//            TextView backAgain = findViewById(R.id.need_refresh);
//            backAgain.setText("Press the back button again to exit this screen.");
//
//            int attemptsA = Integer.parseInt(Num_Of_Attempt.getText().toString());
//
//            Intent intent = new Intent(QuizStatusList.this, Quizzes_menu.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("myAttempt", ++attemptsA);
//            intent.putExtras(bundle);
//            startActivity(intent);
//            sp = getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putInt("myAttempt", ++attemptsA);
//            editor.apply();
//            Toast.makeText(this, "attempt was saved", Toast.LENGTH_SHORT).show();
//            super.onBackPressed();
    }
}