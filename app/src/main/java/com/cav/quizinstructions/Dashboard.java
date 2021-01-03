package com.cav.quizinstructions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;
import static com.cav.quizinstructions.Basic_Content.currLesson;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static WeakReference<Dashboard> weakActivity;

    private DrawerLayout drawer;
    Button resumeLesson;
    private long backPressedTime;
    public static String dashboard_email;
    public static TextView recentModule, activeModule, activeLesson;
    public static String user_id;
    public static int thisUserId, uidFromDb;
    public static TextView isModuleLocked, isModuleCompleted;
    public static String myLatestUserId;
    public static String myLatestAttempt;
    public static String myLatestChapter;
    public static String myLatestIsUnlocked;
    public static String myLatestIsCompleted;
    public static String nameVR;
    public static String user_idPassedTests;
    private TextView welcome_fname;
    public static TextView myEmailForAttempts;
    public static boolean emptyUserIdFromDb;
    public static String emailForAttempts;
    CircleImageView dahsboard_avatar;
    SharedPreferences sp;
    SharedPreferences sharedPreferences;
    public static final String Uid_PREFS = "USER_IDPREFS";
    //    private String retrieveUrl="https://driver-ph.000webhostapp.com/driverphtest/retrieve.php";
    private final String retrieveUrl = "https://phportal.net/driverph/retrieve.php";
    private final String QUESTIONS_URL = "https://phportal.net/driverph/questions.php";
    private static final String Server_Scores_URL = "https://phportal.net/driverph/scoresOnline.php";
    private static final String Server_All_Attempts_URL = "https://phportal.net/driverph/get_all_attempts.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        weakActivity = new WeakReference<>(Dashboard.this);

//        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        dashboard_email = sharedPreferences.getString(EMAIL, "");
        dashboard_email = getIntent().getStringExtra("email");
        welcome_fname = findViewById(R.id.textView_userID);
        dahsboard_avatar = findViewById(R.id.dashboard_avatar);
        isModuleLocked = findViewById(R.id.isModLocked);
        isModuleCompleted = findViewById(R.id.isModCompleted);
        recentModule = findViewById(R.id.myprogresslesson);
        activeModule = findViewById(R.id.Module);
        activeLesson = findViewById(R.id.Lesson);
        resumeLesson = findViewById(R.id.resume);
        myEmailForAttempts = findViewById(R.id.emailForAttempts);
        myEmailForAttempts.setText(emailForAttempts);

        resumeLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeModule();
            }
        });

        //if no record yet from the database
        if (myLatestUserId == null || myLatestIsUnlocked == null ||
                myLatestIsCompleted == null || myLatestAttempt == null || myLatestChapter == null) {
            myLatestUserId = "0";
            myLatestIsUnlocked = "1";
            myLatestIsCompleted = "0";
            myLatestAttempt = "0";
            myLatestChapter = Constant._1;
        } else {
            isModuleLocked.setText(myLatestIsUnlocked);
            isModuleCompleted.setText(myLatestIsCompleted);
        }

        loadRecyclerViewData();
        loadDataAllAttemptsAndLevels();
        retrievedatas(dashboard_email);
        getJSON(QUESTIONS_URL);
        btnSetter();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("EEEE MMMM dd, yyyy");
        String currentDate = simpleDate.format(calendar.getTime());
        //String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.textView_greeting);
        textViewDate.setText(currentDate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void resumeModule() {
        String module= activeModule.getText().toString();
        switch (module){
            case Constant._1:
                startActivity(new Intent(Dashboard.this, Lessons_Basic_Content.class));
                break;
            case Constant._2:
                startActivity(new Intent(Dashboard.this, Basic_Content.class));
                break;
        }




    }

    public static Dashboard getmInstanceActivity() {
        return weakActivity.get();
    }

    public void btnSetter() {

        CardView cardViewLessons = findViewById(R.id.cardView_lessons);
        cardViewLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, Lessons_Menu.class));
            }
        });

        CardView cardViewQuizzes = findViewById(R.id.cardView_quizzes);
        cardViewQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = getSharedPreferences("mySavedAttempt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp.edit();
                editor1.putString("email", dashboard_email);
                editor1.putString("username", nameVR);
                editor1.apply();

                thisUserId = Integer.parseInt(user_id);
                SharedPreferences sp = getApplicationContext().getSharedPreferences(Uid_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("user_id", thisUserId);
                editor.apply();
                Toast.makeText(Dashboard.this, "Dashboard User id: " + thisUserId, Toast.LENGTH_SHORT).show();

              //  uidFromDb = Integer.parseInt(myLatestUserId);
                Intent intent = new Intent(Dashboard.this, Quizzes_menu.class);
                Bundle bundle = new Bundle();
                bundle.putInt("user_idFromServer", Integer.parseInt(myLatestUserId));
                bundle.putInt("user_idFromDashboard", thisUserId);
                bundle.putInt("myLatestIsUnlocked", Integer.parseInt(myLatestIsUnlocked));
                bundle.putInt("myLatestIsCompleted", Integer.parseInt(myLatestIsCompleted));
                bundle.putString("myLatestChapter", myLatestChapter);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        CardView cardViewSimulation = findViewById(R.id.cardView_simulation);
        cardViewSimulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Dashboard.this, "Simulation Activity", Toast.LENGTH_SHORT).show();
            }
        });

        CardView cardViewAssess = findViewById(R.id.cardView_assessment);
        cardViewAssess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Dashboard.this, "Assessment Activity", Toast.LENGTH_SHORT).show();
            }
        });

        CardView cardViewPolGuide = findViewById(R.id.cardView_polguide);
        cardViewPolGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Dashboard.this, "Policies and Guidelines Activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retrievedatas(String r_email) {
        final String email = r_email;

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
                params.put("email", email);

                //returing the response
                return requestHandler.sendPostRequest(retrieveUrl, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
                        View headerView = navigationView1.getHeaderView(0);
                        TextView navUsername = (TextView) headerView.findViewById(R.id.textView4);
                        TextView navemail = (TextView) headerView.findViewById(R.id.nav_header_email);
                        CircleImageView navImage = (CircleImageView) headerView.findViewById(R.id.headerimage);
                        user_id = obj.getString("id");
                        nameVR = obj.getString("first_name");
                        welcome_fname.setText("Welcome " + obj.getString("first_name"));
                        navUsername.setText(obj.getString("first_name") + " " + obj.getString("last_name"));
                        navemail.setText(obj.getString("email"));
                        String img_num = obj.getString("image");

                        if (img_num.equals("1")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));
                        } else if (img_num.equals("2")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));
                        } else if (img_num.equals("3")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));
                        } else if (img_num.equals("4")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));
                        } else if (img_num.equals("5")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));
                        } else if (img_num.equals("6")) {
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar6));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar6));
                        }
                        Toast.makeText(Dashboard.this, "Fetched from user table: " + user_id, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, "Exception: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_language:
                Toast.makeText(this, "Language Changed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                startActivity(new Intent(Dashboard.this, Dashboard.class));
                break;
            case R.id.nav_my_progress:
                startActivity(new Intent(Dashboard.this, MyProgress.class));
                break;
            case R.id.nav_advisory:
                startActivity(new Intent(Dashboard.this, Advisory.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(Dashboard.this, Help.class));
                break;
            case R.id.nav_my_account:
                Intent intent = new Intent(Dashboard.this, MyAccount.class);
                Bundle extras = new Bundle();
                extras.putString("email", dashboard_email);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.nav_siso:
                this.getSharedPreferences(SHARED_PREFS, 0).edit().clear().apply();
                this.getSharedPreferences(Uid_PREFS, 0).edit().clear().apply();
                this.getSharedPreferences("mySavedAttempt", 0).edit().clear().apply();
                finish();
                Intent logout = new Intent(Dashboard.this, Login.class);
                startActivity(logout);
                Toast.makeText(this, "Log Out Success", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(Dashboard.this, Login.class));
            finish();
        }
    }

    //for viewing of passed tests (summarized)
    private void loadRecyclerViewData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        Database db = new Database(this);
        db.Open();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.DEPRECATED_GET_OR_POST,
                Server_Scores_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
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


                                user_idPassedTests = menuitemArray.getJSONObject(i).getString("user_id");
                                String email = menuitemArray.getJSONObject(i).getString("email");
                                String score = menuitemArray.getJSONObject(i).getString("score");
                                String num_items = menuitemArray.getJSONObject(i).getString("num_of_items");
                                String chapter = menuitemArray.getJSONObject(i).getString("chapter");
                                String num_of_attempt = menuitemArray.getJSONObject(i).getString("num_of_attempt");
                                String duration = menuitemArray.getJSONObject(i).getString("duration");
                                String date_taken = menuitemArray.getJSONObject(i).getString("date_taken");
                                String isLocked = menuitemArray.getJSONObject(i).getString("isUnlocked");
                                String isCompleted = menuitemArray.getJSONObject(i).getString("isCompleted");
                                MyScoresServer mS1 = new MyScoresServer(Integer.parseInt(user_idPassedTests), email, Integer.parseInt(score),
                                        Integer.parseInt(num_items), chapter, Integer.parseInt(num_of_attempt), duration,
                                        date_taken, Integer.parseInt(isLocked), Integer.parseInt(isCompleted));
                                db.addScoresServer(mS1);
                            }
                            Toast.makeText(Dashboard.this, "Fetched from passed tests: " + user_idPassedTests, Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Dashboard.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", dashboard_email);
                Log.d("email", dashboard_email + "");
                Log.d("yes", "successful...");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //load all questions for tests
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
                Log.d("moduleName: " + i, menuitemArray.getJSONObject(i)
                        .getString("moduleName"));

                String question = menuitemArray.getJSONObject(i).getString("questionText");
                String option1 = menuitemArray.getJSONObject(i).getString("choiceA");
                String option2 = menuitemArray.getJSONObject(i).getString("choiceB");
                String option3 = menuitemArray.getJSONObject(i).getString("choiceC");
                String option4 = menuitemArray.getJSONObject(i).getString("choiceD");
                String answer_nr = menuitemArray.getJSONObject(i).getString("correctAnswer");
                String chapter = menuitemArray.getJSONObject(i).getString("moduleName");
                Question q1 = new Question(question, option1, option2, option3, option4, Integer.parseInt(answer_nr), chapter);
                db.addQuestion(q1);
            }

        } catch (Exception je) {

            Log.d("json error...", je + "");
        }
        Log.d("Inside aysnc task", "inside asynctask...");
//        db.close();
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
                        Toast.makeText(Dashboard.this, "Loading all attempts", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(Dashboard.this, "Fetched from attempts: " + myLatestUserId, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Dashboard.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String initialChapter = recentModule.getText().toString();
                String myEmailUnlock = myEmailForAttempts.getText().toString();
                params.put("email", dashboard_email);
                params.put("chapter", initialChapter);
                Log.d("email", myEmailUnlock + "");
                Log.d("yes", "successful...");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}