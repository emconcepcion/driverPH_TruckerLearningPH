package com.cav.quizinstructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class AccountEdit extends AppCompatActivity {
    TextView changeavatar;
    EditText et_firstname, et_lastname, et_email, et_username, et_password;
    public String email, img_num, fname, lname,username, password, id;
    ImageView avatar_edit;
    Button btnsave, btncancel;
    private String updateUrl= "https://phportal.net/driverph/update_user_information.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        changeavatar = findViewById(R.id.changeavatar);
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_email = findViewById(R.id.et_email);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        avatar_edit = findViewById(R.id.avatar_edit);
        btnsave = findViewById(R.id.btn_save);
        btncancel = findViewById(R.id.btn_cancel);

        fname = getIntent().getStringExtra("first_name");
        lname = getIntent().getStringExtra("last_name");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        img_num = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");

        settextdata(fname, lname, email, username, password, img_num);

        changeavatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccountEdit.this, ChangeAvatar.class);
                    Bundle extras = new Bundle();
                    extras.putString("first_name", fname);
                    extras.putString("last_name", lname);
                    extras.putString("email", email);
                    extras.putString("username", username);
                    extras.putString("password",password);
                    extras.putString("image", img_num);
                    extras.putString("id", id);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatemyaccount();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountEdit.this, MyAccount.class);
                Bundle extras = new Bundle();
                extras.putString("email", email);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void settextdata(String setfname, String setlname, String set_email, String set_username, String set_password, String set_image){
        et_firstname.setText(setfname);
        et_lastname.setText(setlname);
        et_email.setText(set_email);
        et_username.setText(set_username);
        et_password.setText(set_password);

        if(set_image.equals("1")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));
        }else if(set_image.equals("2")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));
        }else if(set_image.equals("3")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));
        }else if(set_image.equals("4")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));
        }else if(set_image.equals("5")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));
        }else if(set_image.equals("6")){
            avatar_edit.setImageDrawable(getResources().getDrawable(R.drawable.avatar6));
        }
    }

    public void updatemyaccount(){
        final String id_u = id;
        final String fname_u = et_firstname.getText().toString();
        final String lname_u = et_lastname.getText().toString();
        final String email_u = et_email.getText().toString();
        final String username_u = et_username.getText().toString();
        final String password_u = et_password.getText().toString();
        final String img_num_u = img_num;
        class Update extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(AccountEdit.this);

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
                params.put("id", id_u);
                params.put("first_name",fname_u);
                params.put("last_name",lname_u);
                params.put("email",email_u);
                params.put("username",username_u);
                params.put("password",password_u);
                params.put("image",img_num_u);
                //returing the response
                return requestHandler.sendPostRequest(updateUrl, params);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                pdLoading.dismiss();
                Toast.makeText(AccountEdit.this, "Information Update Successfully, Please Login Again", Toast.LENGTH_SHORT).show();
                try{
                    //Converting response to JSON Object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")){
                        Toast.makeText(AccountEdit.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e ){
                    Intent intent = new Intent(AccountEdit.this, Login.class);
                    startActivity(intent);
//                    Toast.makeText(AccountEdit.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }
        Update update = new Update();
        update.execute();
    }
}