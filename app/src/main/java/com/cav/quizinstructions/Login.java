package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    EditText username, password;
    public String u_name, pword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btn_login);
        TextView signUpView = findViewById(R.id.textView_signUp);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);

        username.setText("Erin");
        password.setText("Concep");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u_name = username.getText().toString().trim();
                pword = password.getText().toString().trim();
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
    }

    public void userLogin()
    {
        String login_name = username.getText().toString();
        String login_pass = password.getText().toString();
        String method = "login";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,login_name,login_pass);
    }
}