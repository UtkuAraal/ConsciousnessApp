package com.ub.conscious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ub.conscious.adapters.SharedAdapter;
import com.ub.conscious.entity.Announcement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeeShared extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ListView sharedAnnouncement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_see_shared);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);
        sharedAnnouncement = findViewById(R.id.anolistesi);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.shared);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.shared:
                        return true;
                    case R.id.banned:
                        startActivity(new Intent(getApplicationContext(),bannedAnnouncement.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.admins:
                        startActivity(new Intent(getApplicationContext(),Admins.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });
        new GetSharedAsyncTask().execute();

    }


    class GetSharedAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        JSONArray JsonArr;
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/announcement/getAll")
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
                ArrayList<Announcement> annolist = new ArrayList<>();
                for(int i = 0; i<JsonArr.size(); i++){
                    JSONObject object = (JSONObject)JsonArr.get(i);
                    if(object.get("banned").toString().equals("false")){
                        Announcement announcement = new Announcement();
                        announcement.setId(Long.parseLong(object.get("id").toString()));
                        announcement.setTitle(object.get("title").toString());
                        announcement.setEvent(object.get("event").toString());
                        annolist.add(announcement);

                    }
                }
                SharedAdapter adapter = new SharedAdapter(SeeShared.this, annolist.toArray(new Announcement[annolist.size()]));
                sharedAnnouncement.setAdapter(adapter);

            } else{
                Toast.makeText(SeeShared.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }
}