package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Registration extends AppCompatActivity {
    EditText edittext_firstname, edittext_lastname, edittext_email, edittext_username, edittext_password, edittext_confirm;
    public String firstname, lastname, email, username, password, user_id, encryptedTextBase64;
    private String retrieveUrl="https://phportal.net/driverph/checking_user_register.php";
    private String sendUrl="https://phportal.net/driverph/registerData.php";
    //private String sendUrl="C:\\xampp\\htdocs\\login.php";
    private RequestQueue requestQueue;
    //    ProgressDialog pdLoading;
    private static final String TAG=Registration.class.getSimpleName();
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
        setContentView(R.layout.activity_registration);

        edittext_firstname = findViewById(R.id.editText_firstname);
        edittext_lastname = findViewById(R.id.editText_lastname);
        edittext_email = findViewById(R.id.editText_email);
        edittext_username = findViewById(R.id.editText_username);
        edittext_password = findViewById(R.id.editText_password);
        edittext_confirm = findViewById(R.id.editText_confirm_password);
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



                if (!validateEmail() | !validatePassword() | !validateFirstname() | !validateLastname() | !validateUsername() | !validateConfirmPassword()) {
                    return;
                }

                check_user_exist();

            }
        });
    }

    public void check_user_exist() {
        final String femail = edittext_email.getText().toString();
        final String funame = edittext_username.getText().toString();
        class show_prod extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(Registration.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(true);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("email", femail);
                params.put("username", funame);

                //returing the response
                return requestHandler.sendPostRequest(retrieveUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    user_id = obj.getString("id");

                    int i =Integer.parseInt(user_id);

                    if(i == 0){
                        add_data();
                        //pdLoading.dismiss();
                        //Toast.makeText(Registration.this, "There is no existing username and email", Toast.LENGTH_SHORT).show();
                    }else if(i >= 1){
                        pdLoading.dismiss();
//                            String strLowerCase = generateRandomString(CHAR_LOWERCASE, 3);
//                            String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, 3);
//                            String strDigit = generateRandomString(DIGIT, 2);
//
//                            newpassword = strUppercaseCase + strLowerCase + strDigit;
//
                        Toast.makeText(Registration.this, "Email And Username is Already Register", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e ){
                    //Toast.makeText(Dashboard.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }

        show_prod show = new show_prod();
        show.execute();
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

    private boolean validateEmail() {
        String emailInput = edittext_email.getText().toString().trim();
        if (emailInput.isEmpty()) {
            edittext_email.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            edittext_email.setError("Please enter a valid email address");
            return false;
        } else {
            edittext_email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = edittext_password.getText().toString().trim();
        if (passwordInput.isEmpty()) {
            edittext_password.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            edittext_password.setError("Password must consist of minimum 8 characters and must contain a number, special character, letters");
            return false;
        } else {
            edittext_password.setError(null);
            return true;
        }
    }

    private boolean validateFirstname() {
        String emailInput = edittext_firstname.getText().toString().trim();
        if (emailInput.isEmpty()) {
            edittext_firstname.setError("Field can't be empty");
            return false;
        } else {
            edittext_firstname.setError(null);
            return true;
        }
    }

    private boolean validateLastname() {
        String emailInput = edittext_lastname.getText().toString().trim();
        if (emailInput.isEmpty()) {
            edittext_lastname.setError("Field can't be empty");
            return false;
        } else {
            edittext_lastname.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String emailInput = edittext_username.getText().toString().trim();
        if (emailInput.isEmpty()) {
            edittext_username.setError("Field can't be empty");
            return false;
        } else {
            edittext_username.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String emailInput = edittext_confirm.getText().toString().trim();
        String password = edittext_password.getText().toString().trim();
        if (emailInput.isEmpty()) {
            edittext_confirm.setError("Field can't be empty");
            return false;
        } else if(!emailInput.equals(password)) {
            edittext_confirm.setError("Password didn't Match");
            return false;
        } else {
            edittext_confirm.setError(null);
            return true;
        }
    }
}