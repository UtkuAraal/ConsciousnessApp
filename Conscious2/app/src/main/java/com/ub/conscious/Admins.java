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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Admins extends AppCompatActivity {
    ListView adminList;
    SharedPreferences sharedPreferences;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins);
        adminList = findViewById(R.id.adminList);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);

        new GetAdminsAsyncTask().execute();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView3);
        bottomNavigationView.setSelectedItemId(R.id.admins);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.admins:
                        return true;
                    case R.id.banned:
                        startActivity(new Intent(getApplicationContext(),bannedAnnouncement.class));
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
    }

    class GetAdminsAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        ArrayList<String> adminListArray;
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/getAdmins")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response.body().string());
                adminListArray = new ArrayList<String>();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String name = (String) obj.get("name");
                    String surname = (String) obj.get("surname");
                    String identity = (String) obj.get("identity");
                    adminListArray.add(name + " " + surname + " --- " + identity);
                }
                return true;

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

                adapter = new ArrayAdapter<String>(Admins.this, android.R.layout.simple_list_item_1, adminListArray);
                adminList.setAdapter(adapter);
            } else{
                Toast.makeText(Admins.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }
}