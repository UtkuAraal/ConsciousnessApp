package com.ub.conscious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ub.conscious.adapters.BannedAdapter;
import com.ub.conscious.adapters.MyAnnAdapter;
import com.ub.conscious.entity.Announcement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class bannedAnnouncement extends AppCompatActivity {
    ListView bannedList;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned_announcement);
        bannedList = findViewById(R.id.bannedAnnouncement);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.banned);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.banned:
                        return true;
                    case R.id.admins:
                        startActivity(new Intent(getApplicationContext(),Admins.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.shared:
                        startActivity(new Intent(getApplicationContext(),SeeShared.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });
        new GetBannedAsyncTask().execute();
    }
    class GetBannedAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        JSONArray JsonArr;
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/announcement/getBannedAnnouncement")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                JSONParser parser = new JSONParser();
                JsonArr = (JSONArray) parser.parse(response.body().string());


                return true;

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

                ArrayList<Announcement> annlist = new ArrayList<>();
                for(int i = 0; i<JsonArr.size(); i++){
                    JSONObject object = (JSONObject)JsonArr.get(i);
                    Announcement announcement = new Announcement();
                    announcement.setTitle(object.get("title").toString());
                    announcement.setEvent(object.get("event").toString());
                    announcement.setCity(object.get("city").toString());
                    annlist.add(announcement);

                }
                BannedAdapter adapter = new BannedAdapter(bannedAnnouncement.this, annlist.toArray(new Announcement[annlist.size()]));
                bannedList.setAdapter(adapter);
            } else{
                Toast.makeText(bannedAnnouncement.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }
}