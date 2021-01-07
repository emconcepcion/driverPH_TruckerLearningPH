package com.cav.quizinstructions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by prabeesh on 7/14/2015.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {

    AlertDialog alertDialog;
    public String fname;
    Context ctx;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "text";
    BackgroundTask(Context ctx)
    {
        this.ctx =ctx;
    }
    @Override
    protected void onPreExecute() {

        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Loading....");
        alertDialog.show();
    }
    @Override
    protected String doInBackground(String... params) {
//        String login_url = "https://driver-ph.000webhostapp.com/driverphtest/login.php";
        String login_url = "https://phportal.net/driverph/login.php";
        String method = params[0];
        if(method.equals("login"))
        {
            String login_name = params[1];
            String login_pass = params[2];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(login_pass,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result) {
        String email = result;



        if(email.isEmpty()){
            alertDialog.dismiss();
            Toast.makeText(ctx, "Incorrect....", Toast.LENGTH_SHORT).show();
        }else{
            alertDialog.dismiss();
            SharedPreferences sharedPreferences = ctx.getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EMAIL, email);
            editor.apply();
            Intent intent = new Intent(ctx,Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Bundle extras = new Bundle();
//            extras.putString("email", email);
//            intent.putExtras(extras);
            ctx.startActivity(intent);
        }
    }
}