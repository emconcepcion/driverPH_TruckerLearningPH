package com.cav.quizinstructions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.unity3d.player.UnityPlayerActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import static com.cav.quizinstructions.Basic_Content.currentLesson;
import static com.cav.quizinstructions.Basic_Content.module;
import static com.cav.quizinstructions.Constant.MODULE_ID_1;
import static com.cav.quizinstructions.Constant.MODULE_ID_2;
import static com.cav.quizinstructions.Constant.MODULE_ID_3;
import static com.cav.quizinstructions.Constant.SP_LESSONID;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static WeakReference<Dashboard> weakActivity;

    private int REQUEST_CODE = 11;

    private DrawerLayout drawer;
    static Button resumeLesson;
    public static String dashboard_email;
    public static TextView recentModule, activeModule, activeLesson,
                            activeModuleId, activeLessonId;
    public static String user_id, uId;
    public static int thisUserId;
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
    public static String lessonIdFromServer, moduleNameFromServer,
                        lessonTitleFromServer, moduleIdFromServer;
    CircleImageView dahsboard_avatar;
    SharedPreferences sp;
    SharedPreferences sharedPreferences;
    public static final String Uid_PREFS = "USER_IDPREFS";
    private static final String retrieveUrl = "https://phportal.net/driverph/retrieve.php";
    private static final String QUESTIONS_URL = "https://phportal.net/driverph/questions.php";
    private static final String Server_Scores_URL = "https://phportal.net/driverph/scoresOnline.php";
    public static final String SERVER_DASHBOARD = "https://phportal.net/driverph/dashboard_latest_module.php";
    public static final String DASH_TEST = "https://phportal.net/driverph/daa.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        weakActivity = new WeakReference<>(Dashboard.this);

        SharedPreferences sh = getSharedPreferences("MySharedPrefForEmail", MODE_PRIVATE);
        dashboard_email = sh.getString("driver_email", "");

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", dashboard_email);
        editor.apply();

        welcome_fname = findViewById(R.id.textView_userID);
        dahsboard_avatar = findViewById(R.id.dashboard_avatar);
        isModuleLocked = findViewById(R.id.isModLocked);
        isModuleCompleted = findViewById(R.id.isModCompleted);
        recentModule = findViewById(R.id.myprogresslesson);

        activeLessonId = findViewById(R.id.lessonIdServer);
        activeLesson = findViewById(R.id.Lesson);
        activeModuleId = findViewById(R.id.moduleIdServer);
        activeModule = findViewById(R.id.Module);


        resumeLesson = (Button) findViewById(R.id.resume);
        myEmailForAttempts = findViewById(R.id.emailForAttempts);
        myEmailForAttempts.setText(dashboard_email);
        recentModule.setText(myLatestChapter);

        SharedPreferences sharedPreferences = getSharedPreferences(SP_LESSONID, MODE_PRIVATE);
        lessonIdFromServer = sharedPreferences.getString("lessonId", "");
        lessonTitleFromServer = sharedPreferences.getString("lessonTitle", "");
        moduleIdFromServer = sharedPreferences.getString("moduleId", "");
        moduleNameFromServer = sharedPreferences.getString("moduleName", "");

        String null_moduleTitle = "No active modules yet,";
        String null_lessonTitle = "please click the button to start learning!";
        if (lessonTitleFromServer.equals("null") || moduleNameFromServer.equals("null") ||
                lessonIdFromServer.equals("null") || moduleIdFromServer.equals("null")){
            activeModule.setText(null_moduleTitle);
            activeLesson.setText(null_lessonTitle);
        }else{
            activeLessonId.setText(lessonIdFromServer);
            activeLesson.setText(lessonTitleFromServer);
            activeModuleId.setText(moduleIdFromServer);
            activeModule.setText(moduleNameFromServer);
        }


        if (activeModule.getText().toString().equals(null_moduleTitle)){
            resumeLesson.setText("Start Learning");
        }
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
            myLatestChapter = "1";
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

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(Dashboard.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE,
                                Dashboard.this, REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){
            Toast.makeText(this, "Start Download", Toast.LENGTH_SHORT).show();

            if (resultCode != RESULT_OK){
                Log.d("mm", "Update flow failed" + resultCode);
            }
        }
    }

    private void resumeModule() {
        String module= activeModule.getText().toString();
        switch (module){
            case Constant._1:
            case MODULE_ID_1:
                Intent intent1 = new Intent(Dashboard.this, Basic_Content.class);
                Bundle extras1 = new Bundle();
                extras1.putString("module", MODULE_ID_1);
                extras1.putString("lessonId", activeLessonId.getText().toString());
                extras1.putString("moduleName", activeModule.getText().toString());
                intent1.putExtras(extras1);
                startActivity(intent1);
                break;
            case Constant._2:
            case MODULE_ID_2:
                Intent intent2 = new Intent(Dashboard.this, Basic_Content.class);
                Bundle extras2 = new Bundle();
                extras2.putString("module", MODULE_ID_2);
                extras2.putString("lessonId", activeLessonId.getText().toString());
                extras2.putString("moduleName", activeModule.getText().toString());
                intent2.putExtras(extras2);
                startActivity(intent2);
                break;
            case Constant._3:
            case MODULE_ID_3:
                Intent intent3 = new Intent(Dashboard.this, Basic_Content.class);
                Bundle extras3 = new Bundle();
                extras3.putString("module", MODULE_ID_3);
                extras3.putString("lessonId", activeLessonId.getText().toString());
                extras3.putString("moduleName", activeModule.getText().toString());
                intent3.putExtras(extras3);
                startActivity(intent3);
                break;
            default:
                startActivity(new Intent(Dashboard.this, Lessons_Menu.class));
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
                Intent intent = new Intent(Dashboard.this, Lessons_Menu.class);
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
                startActivity(new Intent(Dashboard.this, UnityPlayerActivity.class));
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
            case R.id.action_log_out:
                logoutThisUser();
                break;
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
                startActivity(new Intent(Dashboard.this, Lesson.class));
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
                logoutThisUser();
                break;
        }
        return true;
    }

    public void logoutThisUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SHARED_PREFS);
        editor.apply();

        SharedPreferences sp = getSharedPreferences(Uid_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.remove(Uid_PREFS);
        editor1.apply();

        SharedPreferences sp1 = getSharedPreferences("mySavedAttempt", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp1.edit();
        editor2.remove("mySavedAttempt");
        editor2.apply();

        SharedPreferences sp2 = getSharedPreferences(SP_LESSONID, MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sp2.edit();
        editor3.remove(SP_LESSONID);
        editor3.apply();

        SharedPreferences sp3 = getSharedPreferences("MySharedPrefForEmail", MODE_PRIVATE);
        SharedPreferences.Editor editor4 = sp3.edit();
        editor4.remove("MySharedPrefForEmail");
        editor4.apply();

        Intent logout = new Intent(Dashboard.this, Login.class);
        startActivity(logout);
        Toast.makeText(this, "Log Out Success", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("Are you sure you want to exit?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            logoutThisUser();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
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

                                Log.d("userId " + i,
                                        menuitemArray.getJSONObject(i).getString("userId")
                                                .toString());
                                Log.d("email: " + i, menuitemArray.getJSONObject(i)
                                        .getString("email"));
                                Log.d("numberOfCorrectAnswers: " + i, menuitemArray.getJSONObject(i)
                                        .getString("numberOfCorrectAnswers"));
                                Log.d("numberOfQuestions: " + i, menuitemArray.getJSONObject(i)
                                        .getString("numberOfQuestions"));
                                Log.d("module: " + i, menuitemArray.getJSONObject(i)
                                        .getString("module"));
                                Log.d("retryCount: " + i, menuitemArray.getJSONObject(i)
                                        .getString("retryCount"));
                                Log.d("minutesToFinish: " + i, menuitemArray.getJSONObject(i)
                                        .getString("minutesToFinish"));
                                Log.d("createdOn: " + i, menuitemArray.getJSONObject(i)
                                        .getString("createdOn"));
                                Log.d("isUnlocked: " + i, menuitemArray.getJSONObject(i)
                                        .getString("isUnlocked"));
                                Log.d("passed: " + i, menuitemArray.getJSONObject(i)
                                        .getString("passed"));

                                user_idPassedTests = menuitemArray.getJSONObject(i).getString("userId");
                                String email = menuitemArray.getJSONObject(i).getString("email");
                                String score = menuitemArray.getJSONObject(i).getString("numberOfCorrectAnswers");
                                String num_items = menuitemArray.getJSONObject(i).getString("numberOfQuestions");
                                String chapter = menuitemArray.getJSONObject(i).getString("module");
                                String num_of_attempt = menuitemArray.getJSONObject(i).getString("retryCount");
                                String duration = menuitemArray.getJSONObject(i).getString("minutesToFinish");
                                String date_taken = menuitemArray.getJSONObject(i).getString("createdOn");
                                String isLocked = menuitemArray.getJSONObject(i).getString("isUnlocked");
                                String isCompleted = menuitemArray.getJSONObject(i).getString("passed");
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
                Log.d("module" + i, menuitemArray.getJSONObject(i)
                        .getString("module"));
                Log.d("moduleName" + i, menuitemArray.getJSONObject(i)
                        .getString("moduleName"));
                Log.d("imageUrl" + i, menuitemArray.getJSONObject(i)
                        .getString("imageUrl"));

                String question = menuitemArray.getJSONObject(i).getString("questionText");
                String option1 = menuitemArray.getJSONObject(i).getString("choiceA");
                String option2 = menuitemArray.getJSONObject(i).getString("choiceB");
                String option3 = menuitemArray.getJSONObject(i).getString("choiceC");
                String option4 = menuitemArray.getJSONObject(i).getString("choiceD");
                String answer_nr = menuitemArray.getJSONObject(i).getString("correctAnswer");
                String chapter = menuitemArray.getJSONObject(i).getString("module");
                String moduleName = menuitemArray.getJSONObject(i).getString("moduleName");
                String image = menuitemArray.getJSONObject(i).getString("imageUrl");
                Question q1 = new Question(question, option1, option2, option3, option4,
                        Integer.parseInt(answer_nr), chapter, moduleName, image);
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
                DASH_TEST,
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

                            Toast.makeText(Dashboard.this, "Fetched from attempts: " + myLatestUserId, Toast.LENGTH_SHORT).show();
                            Toast.makeText(Dashboard.this, "Latest Chapter: " + myLatestChapter, Toast.LENGTH_SHORT).show();
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
}