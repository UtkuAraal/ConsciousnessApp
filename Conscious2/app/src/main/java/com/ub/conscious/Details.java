package com.ub.conscious;

import static com.ub.conscious.adapters.SharedAdapter.context;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.conscious.adapters.SharedAdapter;
import com.ub.conscious.entity.Announcement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Details extends AppCompatActivity {
    TextView title;
    EditText event;
    SharedPreferences sharedPreferences;
    Announcement data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        data = (Announcement) intent.getSerializableExtra("announcement");
        title = findViewById(R.id.titleText);
        event = findViewById(R.id.eventText);
        title.setText(data.getTitle());
        event.setText(data.getEvent());
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);
    }

    public void cancel(View view){
        Intent intent = new Intent(Details.this, SeeShared.class);
        startActivity(intent);
    }

    public void banToAnn(View view){
        new BanToAnnAsyncTask().execute();
        Intent intent = new Intent(Details.this, SeeShared.class);
        startActivity(intent);
    }

    class BanToAnnAsyncTask extends AsyncTask<Long, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Long... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), String.valueOf(data.getId()));

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/announcement/banToAnnouncement")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                return true;

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Details.this, "Duyuru Yasaklandı!", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(Details.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }
}