package com.ub.conscious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText emailTxt;
    EditText passwordTxt;
    SharedPreferences sharedPreferences;

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailTxt = findViewById(R.id.emailtxt);
        passwordTxt = findViewById(R.id.passwordtxt);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);

    }

    public void login(View view){
        if(emailTxt.getText().toString().trim().equals("") || passwordTxt.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this, "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_LONG).show();
        }else{
            JSONObject params = new JSONObject();
            params.put("username", emailTxt.getText().toString());
            params.put("password", passwordTxt.getText().toString());
            new LoginAsyncTask().execute(params);
        }
    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, SingUp.class);
        startActivity(intent);
    }

    class LoginAsyncTask extends AsyncTask<JSONObject, Void, Integer> {
        @Override
        protected Integer doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            String ContentType = "application/json";

            RequestBody body = RequestBody.create(
                    MediaType.parse(ContentType), object[0].toJSONString());
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/login")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if(response.isSuccessful()){
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response.body().string());
                    String access_token = (String) json.get("access_token");
                    sharedPreferences.edit().putString("token", "Bearer " + access_token).apply();

                    return 2;
                }else{
                    return 1;
                }

            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }

        }


        @Override
        protected void onPostExecute(Integer success) {
            if (success == 0) {
                Toast.makeText(MainActivity.this, "İşlemi gerçekleştirirken bir hata meydana geldi!", Toast.LENGTH_LONG).show();

            } else if(success == 1){
                Toast.makeText(MainActivity.this, "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_LONG).show();
            }else if(success == 2){
                Toast.makeText(MainActivity.this, "Başarıyla giriş yaptınız!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, EmailConfirmation.class);
                startActivity(intent);
            }
        }
    }
}