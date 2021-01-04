package com.cav.quizinstructions;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity {
    EditText username, password1;
    public String u_name, pword, password2;
    public static String email;
    TextView fgtpassword;
    private String retrievedatasUrl="https://phportal.net/driverph/xample_login.php";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "text";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btn_login);
        TextView signUpView = findViewById(R.id.textView_signUp);
        username = findViewById(R.id.login_username);
        password1 = findViewById(R.id.login_password);
        fgtpassword = findViewById(R.id.textView_fgtpassword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u_name = username.getText().toString().trim();
                pword = password1.getText().toString().trim();
                if(u_name.isEmpty() || pword.isEmpty()) {
                    Toast.makeText(Login.this,"Please Fill all the Textbox", Toast.LENGTH_LONG).show();
                }else{
                    userLogin();
                };
            }
        });

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        fgtpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, ForgotPassword.class);
//                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        });
    }

    public void userLogin() {
        final String uname = username.getText().toString().trim();
        final String pass = password1.getText().toString().trim();

        class show_prod extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(Login.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", uname);
                params.put("password", pass);
                //returing the response
                return requestHandler.sendPostRequest(retrievedatasUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        email = obj.getString("email");

//                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("email", email);
//                        editor.apply();

                        pdLoading.dismiss();
                        Intent intent = new Intent(Login.this, Dashboard.class);
                        Bundle extras = new Bundle();
                        extras.putString("email", email);
                        extras.putString("password", pass);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }else{
                        pdLoading.dismiss();
                        Toast.makeText(Login.this, "Incorrect", Toast.LENGTH_SHORT).show();
                    }
//                            Toast.makeText(Login.this, email, Toast.LENGTH_SHORT).show();
                } catch (Exception e ){
                    Toast.makeText(Login.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
    }
}
