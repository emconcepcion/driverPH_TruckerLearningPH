package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForgotPassword extends AppCompatActivity {
    EditText firstname, lastname, email;
    Button save, cancel;
    private String retrieveUrl="https://phportal.net/driverph/checking_user.php";
    private String updateUrl= "https://phportal.net/driverph/forgot_password.php";
    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static SecureRandom random = new SecureRandom();
    String newpassword, user_id;
    ProgressDialog pdLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firstname = findViewById(R.id.editText_firstname);
        lastname = findViewById(R.id.editText_lastname);
        email = findViewById(R.id.editText_email);
        save = findViewById(R.id.btn_signUp);
        cancel = findViewById(R.id.btn_cancel);

        pdLoading = new ProgressDialog(ForgotPassword.this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                retrievedatas();
            }
        });
    }

    public void retrievedatas(){
        final String femail = email.getText().toString().trim();
        final String ffname = firstname.getText().toString().trim();
        final String flname = lastname.getText().toString().trim();
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
                params.put("email", femail);
                params.put("first_name", ffname);
                params.put("last_name", flname);
                
                
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
                        user_id = obj.getString("id");

                        if(user_id.equals("0")){
                            pdLoading.dismiss();
                            Toast.makeText(ForgotPassword.this, "Fields Doesn't Exist", Toast.LENGTH_SHORT).show();
                        }else{
                            //pdLoading.dismiss();
                            String strLowerCase = generateRandomString(CHAR_LOWERCASE, 3);
                            String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, 3);
                            String strDigit = generateRandomString(DIGIT, 2);

                            newpassword = strUppercaseCase + strLowerCase + strDigit;

                            //Toast.makeText(ForgotPassword.this, user_id + " " + newpassword, Toast.LENGTH_SHORT).show();
                            update_password();
                        }

                    }
                } catch (Exception e ){
                    //Toast.makeText(Dashboard.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
    }

    public void update_password(){
        final String uid = user_id;
        final String upassword = newpassword;
        final String uemail = email.getText().toString().trim();
        class Update extends AsyncTask<Void, Void, String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", uid);
                params.put("password",upassword);
                params.put("email",uemail);
                //returing the response
                return requestHandler.sendPostRequest(updateUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                pdLoading.dismiss();

                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        }
        Update update = new Update();
        update.execute();
    }

    private static String generateRandomString(String input, int size) {

        if (input == null || input.length() <= 0)
            throw new IllegalArgumentException("Invalid input.");
        if (size < 1) throw new IllegalArgumentException("Invalid size.");

        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }
}