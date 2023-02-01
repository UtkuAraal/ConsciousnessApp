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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class profile extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    EditText identity;
    EditText name;
    EditText surname;
    EditText birthYear;
    ListView myAnnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ub.conscious", Context.MODE_PRIVATE);
        identity = findViewById(R.id.identitytxt);
        name = findViewById(R.id.nametxt);
        surname = findViewById(R.id.surnametxt);
        birthYear = findViewById(R.id.birthyeartxt);
        myAnnList = findViewById(R.id.myAnnList);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        return true;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.share:
                        startActivity(new Intent(getApplicationContext(), shareAnnounceUser.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });
        new GetProfileAsyncTask().execute();

    }

    class GetProfileAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        JSONObject JsonObj;
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/member/getMyProfile")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                JSONParser parser = new JSONParser();
                JsonObj = (JSONObject) parser.parse(response.body().string());


                return true;

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                identity.setText(JsonObj.get("identity").toString());
                name.setText(JsonObj.get("name").toString());
                surname.setText(JsonObj.get("surname").toString());
                birthYear.setText(JsonObj.get("birthYear").toString());
                JSONArray JsonArray = (JSONArray) JsonObj.get("announcements");
                ArrayList<Announcement> annlist = new ArrayList<>();
                for(int i = 0; i<JsonArray.size(); i++){
                    JSONObject object = (JSONObject)JsonArray.get(i);
                    Announcement announcement = new Announcement();
                    announcement.setTitle(object.get("title").toString());
                    announcement.setEvent(object.get("event").toString());
                    announcement.setCity(object.get("city").toString());
                    announcement.setBanned(object.get("banned").toString().equals("true"));
                    annlist.add(announcement);

                }
                MyAnnAdapter adapter = new MyAnnAdapter(profile.this, annlist.toArray(new Announcement[annlist.size()]));
                myAnnList.setAdapter(adapter);
            } else{
                Toast.makeText(profile.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }
}