package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
    EditText edittext_firstname, edittext_lastname, edittext_email, edittext_username, edittext_password;
    public String firstname, lastname, email, username, password;
    private String sendUrl="https://phportal.net/driverph/registerData.php";
    //private String sendUrl="C:\\xampp\\htdocs\\login.php";
    private RequestQueue requestQueue;
    private static final String TAG=Registration.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edittext_firstname = findViewById(R.id.editText_firstname);
        edittext_lastname = findViewById(R.id.editText_lastname);
        edittext_email = findViewById(R.id.editText_email);
        edittext_username = findViewById(R.id.editText_username);
        edittext_password = findViewById(R.id.editText_password);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Button signUpBtn = findViewById(R.id.btn_signUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = edittext_firstname.getText().toString().trim();
                lastname = edittext_lastname.getText().toString().trim();
                email = edittext_email.getText().toString().trim();
                username = edittext_username.getText().toString().trim();
                password = edittext_password.getText().toString().trim();


                if(firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Registration.this,"Please Fill all the Textbox", Toast.LENGTH_LONG).show();
                }else{
                    add_data();
                }

            }
        });
    }

    public void add_data(){
        final String fname =  edittext_firstname.getText().toString();
        final String lname = edittext_lastname.getText().toString();
        final String email1 = edittext_email.getText().toString();
        final String uname = edittext_username.getText().toString();
        final String pass = edittext_password.getText().toString();

        class Product extends AsyncTask<Void, Void, String> {

            ProgressDialog pdLoading = new ProgressDialog(Registration.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
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
                params.put("firstname", fname);
                params.put("lastname", lname);
                params.put("email", email1);
                params.put("username", uname);
                params.put("password", pass);
                //returing the response
                return requestHandler.sendPostRequest(sendUrl, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("Register Failed"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Registration.this, "Register Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Registration.this, Login.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

        }

        Product prod_exec = new Product();
        prod_exec.execute();
    }
}