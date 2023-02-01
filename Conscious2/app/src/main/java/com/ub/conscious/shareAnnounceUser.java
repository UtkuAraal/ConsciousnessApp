package com.ub.conscious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ub.conscious.entity.Announcement;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class shareAnnounceUser extends AppCompatActivity {
    EditText titleTxt;
    EditText eventTxt;
    SharedPreferences sharedPreferences;

    List<Announcement> markers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_announce_user);
        titleTxt = findViewById(R.id.titletxt);
        eventTxt = findViewById(R.id.eventtxt);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu2);
        bottomNavigationView.setSelectedItemId(R.id.share);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.share:
                        return true;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });

    }

    public void share(View view) {
        if (titleTxt.getText().toString().trim().equals("") || eventTxt.getText().toString().trim().equals("")) {
            Toast.makeText(shareAnnounceUser.this, "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_LONG).show();
        } else {
            JSONObject object = new JSONObject();
            object.put("event", eventTxt.getText().toString());
            object.put("title", titleTxt.getText().toString());

            object.put("latitude", sharedPreferences.getString("latitude", "").toString());
            object.put("longitude", sharedPreferences.getString("longitude", "").toString());

            object.put("city", sharedPreferences.getString("city", "").toString());
            new ShareAsyncTask().execute(object);
        }
    }

    class ShareAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
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
                    .url("http://10.0.2.2:8080/announcement/shareAnnouncement")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
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
                Toast.makeText(shareAnnounceUser.this, "Kaydınız başarıyla oluşturuldu!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(shareAnnounceUser.this, MapsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(shareAnnounceUser.this, "Kaydedilirken hata meydana geldi!", Toast.LENGTH_LONG).show();
            }
        }
    }


}