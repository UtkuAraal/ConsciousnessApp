package com.ub.conscious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailConfirmation extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);
        new LoginAsyncTask().execute();
    }

    class LoginAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/isAdmin")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response.body().string().equals("true")){
                    return true;
                }else{
                    return false;
                }

            }catch (Exception e){
                e.printStackTrace();
                Intent intent = new Intent(EmailConfirmation.this, MainActivity.class);
                startActivity(intent);
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EmailConfirmation.this, "Hoşgeldiniz admin!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EmailConfirmation.this, SeeShared.class);
                startActivity(intent);
            } else{
                Toast.makeText(EmailConfirmation.this, "Hoşgeldiniz kullanıcı!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EmailConfirmation.this, MapsActivity.class);
                startActivity(intent);
            }
        }
    }
}