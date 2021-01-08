package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity {
    public String email, password;
    Button save, cancel;
    EditText et_old, et_new, et_confirm;
    private String updateUrl= "https://phportal.net/driverph/change_password.php";
    ProgressDialog pdLoading;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        SharedPreferences sh = getSharedPreferences("MySharedPrefForEmail", MODE_PRIVATE);
        password = sh.getString("driver_password", "");
        email = getIntent().getStringExtra("email");

        et_old = findViewById(R.id.et_password);
        et_new = findViewById(R.id.et_new_password);
        et_confirm = findViewById(R.id.et_confirm_password);
        save = findViewById(R.id.btn_save);
        cancel = findViewById(R.id.btn_cancel);
        pdLoading = new ProgressDialog(ChangePassword.this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                pdLoading.setMax(3000000);

                String old = et_old.getText().toString();
                String news = et_new.getText().toString();
                String confirm = et_confirm.getText().toString();
                if(password.equals(old)){
                    if(news.equals(confirm)){
                        newpassword();
                    }else{
                        pdLoading.dismiss();
                        Toast.makeText(ChangePassword.this, "New Password Didn't Match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pdLoading.dismiss();
                    Toast.makeText(ChangePassword.this, "Old Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassword.this, MyAccount.class);
                Bundle extras = new Bundle();
                extras.putString("email", email);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

    }

    public void newpassword() {
        final String upassword = et_confirm.getText().toString();
        final String uemail = email;
        class Update extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                //this method will be running on UI thread
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("password",upassword);
                params.put("email",uemail);
                //returing the response
                return requestHandler.sendPostRequest(updateUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                pdLoading.dismiss();

                Intent intent = new Intent(ChangePassword.this, Login.class);
                startActivity(intent);
                Toast.makeText(ChangePassword.this, "Changed Success", Toast.LENGTH_SHORT).show();
            }
        }
        Update update = new Update();
        update.execute();
    }

    private boolean validatePassword() {
        String passwordInput = et_new.getText().toString().trim();
        if (passwordInput.isEmpty()) {
            et_new.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            et_new.setError("Password must consist of minimum 8 characters and must contain a number, special character, letters");
            return false;
        } else {
            et_new.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String emailInput = et_confirm.getText().toString().trim();
        String password = et_new.getText().toString().trim();
        if (emailInput.isEmpty()) {
            et_confirm.setError("Field can't be empty");
            return false;
        } else if(!emailInput.equals(password)) {
            et_confirm.setError("Password didn't Match");
            et_new.setError("Password didn't Match");
            return false;
        } else {
            et_confirm.setError(null);
            return true;
        }
    }
}