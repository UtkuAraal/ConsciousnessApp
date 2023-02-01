package com.ub.conscious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingUp extends AppCompatActivity {

    EditText emailTxt;
    EditText passwordTxt;
    EditText nameTxt;
    EditText surnameTxt;
    EditText bodTxt;
    EditText nationalIdTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        emailTxt = findViewById(R.id.emailtxt);
        passwordTxt = findViewById(R.id.passwordtxt);
        nameTxt = findViewById(R.id.nametxt);
        surnameTxt = findViewById(R.id.surnametxt);
        bodTxt = findViewById(R.id.birthtxt);
        nationalIdTxt = findViewById(R.id.nationalidtxt);
    }

    public void login(View view){
        Intent intent = new Intent(SingUp.this, MainActivity.class);
        startActivity(intent);
    }

    public void register(View view){
        if(emailTxt.getText().toString().equals("") || passwordTxt.getText().toString().equals("") || nameTxt.getText().toString().equals("") || surnameTxt.getText().toString().equals("") || bodTxt.getText().toString().equals("")){
            Toast.makeText(SingUp.this, "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_LONG).show();
        }else{
            JSONObject params = new JSONObject();
            params.put("username", emailTxt.getText().toString());
            params.put("password", passwordTxt.getText().toString());
            params.put("identity", nationalIdTxt.getText().toString());
            params.put("name", nameTxt.getText().toString());
            params.put("surname", surnameTxt.getText().toString());
            params.put("birthDate", bodTxt.getText().toString());

            new RegisterAsyncTask().execute(params);

        }

    }


    class RegisterAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            String ContentType = "application/json";

            RequestBody body = RequestBody.create(
                    MediaType.parse(ContentType), object[0].toJSONString());
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/user/save")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string().equals("true")?true:false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(SingUp.this, "Kaydınız başarıyla oluşturuldu! Lütfen mail adresinizi doğrulayınız!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SingUp.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SingUp.this, "Kaydedilirken hata meydana geldi! Lütfen bilgileri doğru giriniz!", Toast.LENGTH_LONG).show();
            }
        }
    }



}