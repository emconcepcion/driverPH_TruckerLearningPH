package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cav.quizinstructions.Dashboard.Uid_PREFS;

public class QuizStatusList extends AppCompatActivity {

    RecyclerView recyclerView;
    public static TextView Score, Email, Num_of_items, Chapter, Num_Of_Attempt,
            Duration, Date_Taken, Is_Locked, Is_Completed;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Score> arrayList = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;
    Button btn_view_result;
    Button refresh_list;
    public static boolean completedQuizzes;
    public static TextView tv_userId_sList;

    public static int isLockedA, isCompletedA;
    boolean submittedScore;
    private boolean isRefreshedList = false;
    public static int uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_status_list);

        refresh_list = findViewById(R.id.btn_refresh_list);
        btn_view_result = findViewById(R.id.btn_view_result);
        recyclerView = findViewById(R.id.recyclerView);
        Email = findViewById(R.id.txt_email);
        Duration = findViewById(R.id.tv_duration_StatusList);
        tv_userId_sList =findViewById(R.id.tv_user_id_StatusList);
        Score = findViewById(R.id.txt_score_for_syncing);
        Chapter = findViewById(R.id.txt_chapter_name);
        Num_of_items = findViewById(R.id.txt_numItems_recyView);
        Num_Of_Attempt = findViewById(R.id.txt_attempt_recyView);
        Is_Locked = findViewById(R.id.tv_isLocked_StatusList);
        Is_Completed = findViewById(R.id.tv_isCompleted_StatusList);
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

        SharedPreferences sharedPreferences = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt("user_id", 0);
        tv_userId_sList.setText(String.valueOf(uid));

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
               // if (Chapter.getText().toString().contains("Unfinished Attempt:"))
                if (QuizActivity.endedAttempt){ //|| (QuizActivity.endedAttempt)
                    submitScore();
                    btn_view_result.setVisibility(View.GONE);
                    refresh_list.setVisibility(View.GONE);
                    Button backToQMenu = findViewById(R.id.btn_backToQMenu);
                    backToQMenu.setVisibility(View.VISIBLE);
                    backToQMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(QuizStatusList.this, Dashboard.class));
                        }
                    });
                }else{
                    submitScore();
                    submittedScore = true;
                    refresh_list.setVisibility(View.INVISIBLE);
                    btn_view_result.setVisibility(View.VISIBLE);
                    completedQuizzes = true;
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
        int txt_score_result = bundle.getInt("score");
        int txt_item_result = bundle.getInt("items");
        String chap_result = bundle.getString("chapter");
        int txt_attempt_result = bundle.getInt("attempt");
        String testDuration = bundle.getString("duration");
        String currentDate = bundle.getString("date_taken");
        int latestUnlocked = bundle.getInt("myLatestUnlocked");
        int latestCompleted = bundle.getInt("myLatestCompleted");

        String moduleCode = "";
        switch(chap_result){
            case Constant._1:
                moduleCode = "1";
                break;
            case Constant._2:
                moduleCode = "2";
                break;
            case Constant._3:
                moduleCode = "3";
                break;
        }

        Score.setText("" + txt_score_result);
        Num_of_items.setText("" + txt_item_result);
//        Chapter.setText(chap_result);
        Chapter.setText(moduleCode);
        Num_Of_Attempt.setText("" + txt_attempt_result);
        Duration.setText(testDuration);
        Date_Taken.setText(currentDate);
        Is_Locked.setText(String.valueOf(latestUnlocked));
        Is_Completed.setText(String.valueOf(latestCompleted));

        String emailA = Email.getText().toString();
        int userIdA = Integer.parseInt(tv_userId_sList.getText().toString());
        int scoreA = Integer.parseInt(Score.getText().toString());
        int itemsA = Integer.parseInt(Num_of_items.getText().toString());
        String chapA = Chapter.getText().toString();
        int attemptsA = Integer.parseInt(Num_Of_Attempt.getText().toString());
        String durationA = Duration.getText().toString();
        String dateTakenA = Date_Taken.getText().toString();
        isLockedA = Integer.parseInt(Is_Locked.getText().toString());
        isCompletedA = Integer.parseInt(Is_Completed.getText().toString());

        //user must log in to change its user name from guest to email address for saving
        if (emailA.equals("guest_user")){
            AlertDialog alertDialog = new AlertDialog.Builder(QuizStatusList.this).create();
            alertDialog.setTitle("Log in to Continue");
            alertDialog.setMessage("Please connect to the internet and log in before clicking \"Start the quiz\"");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(QuizStatusList.this, Login.class));
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        //save the score to web server for all attempts table if there is internet connection
        if (checkNetworkConnection()){
            saveAllAttemptsToAppServer(userIdA,emailA, scoreA, itemsA, chapA, attemptsA, durationA, dateTakenA, isLockedA, isCompletedA);
        }
        //save the score to web server only if the user passed the test, else, save only to local db
        if (QuizActivity.unlocked && checkNetworkConnection()){
            saveToAppServer(userIdA,emailA, scoreA, itemsA, chapA, attemptsA, durationA, dateTakenA, isLockedA, isCompletedA);
        }else{
            saveToLocalStorage(userIdA,emailA, scoreA, itemsA, chapA, attemptsA, durationA,
                    dateTakenA, isLockedA, isCompletedA, DbContract.SYNC_STATUS_SAVED);
        }
    }

    public void readFromLocalStorage() {

        //clear data from arraylist
        arrayList.clear();
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()) {
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

            String module = "";
            switch(chap){
                case "1":
                    module = Constant._1;
                    break;
                case "2":
                    module = Constant._2;
                    break;
                case "3":
                    module = Constant._3;
                    break;
            }

            arrayList.add(new Score(userId,email, score, num_items, module, num_attempt, duration,
                    date_Taken, isLocked, isCompleted, sync_status));
        }

        Collections.reverse(arrayList);
        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }


    public void saveToAppServer(int userId, String email, int score, int num_items, String chap,
                                int num_of_attempt, String duration, String date_taken,
                                int isLocked, int isCompleted) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.ScoresTable.SERVER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject("{}");

                                //get response from jsonobject
                                String Response = jsonObject.getString("response");
                                //check response from server
                                if (Response.equals("OK")) {
                                    saveToLocalStorage(userId, email, score, num_items, chap, num_of_attempt, duration,
                                            date_taken, isLocked, isCompleted, DbContract.SYNC_STATUS_SAVED);
                                    StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.synced),
                                            Toast.LENGTH_LONG, R.style.toastStyle).show();
                                } else { //for server error, unable to save, sav storage to local
                                    saveToLocalStorage(userId, email, score, num_items, chap, num_of_attempt, duration,
                                            date_taken, isLocked, isCompleted, DbContract.SYNC_STATUS_FAILED);
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
                    saveToLocalStorage(userId, email, score, num_items, chap, num_of_attempt, duration,
                            date_taken, isLocked, isCompleted, DbContract.SYNC_STATUS_FAILED);
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
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    String testDuration = bundle.getString("duration");

                    params.put("email", email);
                    params.put("score", String.valueOf(score));
                    params.put("num_of_items", String.valueOf(num_items));
                    params.put("chapter", chap);
                    params.put("num_of_attempt", String.valueOf(num_of_attempt));
                    params.put("duration", testDuration);
                    params.put("date_taken", date_taken);
                    params.put("isUnlocked", String.valueOf(isLocked));
                    params.put("isCompleted", String.valueOf(isCompleted));
                    return params;
                }
            };
            MySingleton.getInstance(QuizStatusList.this).addToRequestQueue(stringRequest);
            saveToLocalStorage(userId, email, score, num_items, chap, num_of_attempt, duration,
                    date_taken, isLocked, isCompleted, DbContract.SYNC_STATUS_SAVED);
            StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.saved),
                    Toast.LENGTH_LONG, R.style.toastStyle).show();
        } else { // no internet, save to SQLite
            saveToLocalStorage(userId, email, score, num_items, chap, num_of_attempt, duration,
                    date_taken, isLocked, isCompleted, DbContract.SYNC_STATUS_FAILED);
            StyleableToast.makeText(getApplicationContext(), QuizStatusList.this.getString(R.string.connect_to_net_to_save),
                    Toast.LENGTH_LONG, R.style.toastStyle).show();
        }

    }

    public void saveAllAttemptsToAppServer(int userId, String email, int score, int num_items, String chap,
                                int num_of_attempt, String duration, String date_taken,
                                int isLocked, int isCompleted) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DbContract.ScoresTable.SERVER_ALL_ATTEMPTS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject("{}");

                                //get response from jsonobject
                                String Response = jsonObject.getString("response");
                                //check response from server
                                if (Response.equals("OK")) {
                                } else { //for server error, unable to save, will be handled by saveToAppServer
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    String testDuration = bundle.getString("duration");

                    params.put("email", email);
                    params.put("score", String.valueOf(score));
                    params.put("num_of_items", String.valueOf(num_items));
                    params.put("chapter", chap);
                    params.put("num_of_attempt", String.valueOf(num_of_attempt));
                    params.put("duration", testDuration);
                    params.put("date_taken", date_taken);
                    params.put("isUnlocked", String.valueOf(isLocked));
                    params.put("isCompleted", String.valueOf(isCompleted));
                    return params;
                }
            };
            MySingleton.getInstance(QuizStatusList.this).addToRequestQueue(stringRequest);
            Toast.makeText(this, "Saved to all attempts db.", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveToLocalStorage(int userId, String email, int score, int num_items, String chap,
                                   int num_of_attempt, String duration, String date_taken,
                                   int isLocked, int isCompleted, int sync_status) {

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //save to sqlite
        dbHelper.saveToLocalDatabase(userId, email, score, num_items, chap, num_of_attempt, duration,
                date_taken, isLocked, isCompleted, sync_status, database);

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
        int userId = bundle.getInt("myUserId");

        Intent intent = new Intent(QuizStatusList.this, QuizResults.class);
        intent.putStringArrayListExtra("askedQuestions", arrayList);
        intent.putExtra("score", score);
        intent.putExtra("items", items);
        intent.putExtra("chapter", chapter);
        intent.putExtra("attempt", nextAttempt);
        intent.putExtra("email", email);
        intent.putExtra("user_id", userId);
        startActivity(intent);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() { {
            TextView backAgain = findViewById(R.id.need_refresh);
            backAgain.setVisibility(View.VISIBLE);
            backAgain.setText("Please press the button to proceed.");
//            finish();
        }

    }
}