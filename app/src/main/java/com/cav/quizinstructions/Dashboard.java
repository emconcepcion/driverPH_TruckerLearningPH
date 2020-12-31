package com.cav.quizinstructions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cav.quizinstructions.BackgroundTask.EMAIL;
import static com.cav.quizinstructions.BackgroundTask.SHARED_PREFS;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private long backPressedTime;
    public static String dashboard_email;
    public static String user_id;
    public static int thisUserId;
    public static TextView navUsername;
    public static String nameVR;
    private TextView welcome_fname;
    CircleImageView dahsboard_avatar;
    SharedPreferences sp;
    public static final String Uid_PREFS = "USER_IDPREFS";
    //    private String retrieveUrl="https://driver-ph.000webhostapp.com/driverphtest/retrieve.php";
    private String retrieveUrl="https://phportal.net/driverph/retrieve.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        dashboard_email = getIntent().getStringExtra("email");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        dashboard_email = sharedPreferences.getString(EMAIL, "");
        welcome_fname = findViewById(R.id.textView_userID);
        dahsboard_avatar = findViewById(R.id.dashboard_avatar);



        retrievedatas(dashboard_email);
        btnSetter();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDate =  new SimpleDateFormat("EEEE MMMM dd, yyyy");
        String currentDate = simpleDate.format(calendar.getTime());
        //String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.textView_greeting);
        textViewDate.setText(currentDate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //setTitle("Driver.PH");

        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    public void btnSetter(){

        CardView cardViewLessons = findViewById(R.id.cardView_lessons);
        cardViewLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this,Lessons_Menu.class));
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
                createCategories();

                thisUserId = Integer.parseInt(user_id);
                SharedPreferences sp = getApplicationContext().getSharedPreferences(Uid_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("user_id", thisUserId);
                editor.apply();
                Toast.makeText(Dashboard.this, "email was saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Dashboard.this, Quizzes_menu.class);
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

    public void retrievedatas(String r_email){
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
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")){
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

                        if(img_num.equals("1")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));
                        }else if(img_num.equals("2")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));
                        }else if(img_num.equals("3")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));
                        }else if(img_num.equals("4")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));
                        }else if(img_num.equals("5")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));
                        }else if(img_num.equals("6")){
                            navImage.setImageDrawable(getResources().getDrawable(R.drawable.avatar6));
                            dahsboard_avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar6));
                        }
                        Toast.makeText(Dashboard.this, user_id, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e ){
                    Toast.makeText(Dashboard.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                Toast.makeText(this, "Sign in/Sign out Activity here", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            startActivity(new Intent(Dashboard.this,Login.class));
            //super.onBackPressed();
        }

        /*
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finishAffinity();
        }else{
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();

         */
    }

    //  1 = unlocked 0 = locked
    public void createCategories() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(getPackageName() + Constant.MY_LEVEL_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("unlockMod1", 1); //by default, level 1 unlocked
        editor.putString(Constant.MOD_1, "Unlock");
        editor.apply();

        if (sharedPreferences.getString(Constant.MOD_1, "N/A").equals("Unlock")) {
            editor.putInt("unlockMod1", 1);
            editor.putInt("unlockMod2", 0);
            editor.putInt("unlockMod3", 0);
        } else if (sharedPreferences.getString(Constant.MOD_2, "N/A").equals("Unlock")) {
            editor.putInt("unlockMod1", 1);
            editor.putInt("unlockMod2", 1);
            editor.putInt("unlockMod3", 0);
        } else if (sharedPreferences.getString(Constant.MOD_3, "N/A").equals("Unlock")) {
            editor.putInt("unlockMod1", 1);
            editor.putInt("unlockMod2", 1);
            editor.putInt("unlockMod3", 1);
        }
    }
}