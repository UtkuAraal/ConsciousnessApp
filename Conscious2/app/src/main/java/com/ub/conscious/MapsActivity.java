package com.ub.conscious;

import static com.ub.conscious.adapters.SharedAdapter.context;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.ub.conscious.adapters.MyAnnAdapter;
import com.ub.conscious.databinding.ActivityMapsBinding;
import com.ub.conscious.entity.Announcement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    SharedPreferences sharedPreferences;
    boolean info = false;

    double getLatitude;
    double getLongitude;
    List<Announcement> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        sharedPreferences = MapsActivity.this.getSharedPreferences("com.ub.conscious", MODE_PRIVATE);
        info = false;
        markers = new ArrayList<>();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menubari);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.map:
                        return true;
                    case R.id.share:
                        startActivity(new Intent(getApplicationContext(), shareAnnounceUser.class));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                info = sharedPreferences.getBoolean("info", false);

                if (!info) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    sharedPreferences.edit().putBoolean("info", true).apply();

                    getLatitude = location.getLatitude();
                    getLongitude = location.getLongitude();
                    sharedPreferences.edit().putString("latitude", String.valueOf(getLatitude)).apply();
                    sharedPreferences.edit().putString("longitude", String.valueOf(getLongitude)).apply();
                    new LoginAsyncTask().execute(getLatitude, getLongitude);

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.getRoot(), "Permission needed for maps", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
            }
            mMap.setMyLocationEnabled(true);//mavi konum gÃ¶stergesi
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        getLatitude = lastLocation.getLatitude();
        getLongitude = lastLocation.getLongitude();
        sharedPreferences.edit().putString("latitude", String.valueOf(getLatitude)).apply();
        sharedPreferences.edit().putString("longitude", String.valueOf(getLongitude)).apply();

        new LoginAsyncTask().execute(getLatitude, getLongitude);
        new GetMarkerAsyncTask().execute();
        for(Announcement ann: markers){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(ann.getLatitude()), Double.parseDouble(ann.getLongitude())))
                    .title(ann.getTitle())
                    .snippet(ann.getEvent())
                    .icon(bitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_announcement_24)));
            mMap.setInfoWindowAdapter(new CustomInfoWindowForGoogleMap(MapsActivity.this));
        }
    }

    private void registerLauncher() {

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    class LoginAsyncTask extends AsyncTask<Double, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Double... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            String ContentType = "application/json";


            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLatitude + "," + getLongitude+"&location_type=APPROXIMATE&result_type=street_address&key=AIzaSyDn1nuruC5rMYtIGyRVHISB7bJG9oWuJo4";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if(response.isSuccessful()){
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response.body().string());
                    JSONObject jsonObject = (JSONObject)json.get("plus_code");
                    String codeString = jsonObject.get("compound_code").toString();
                    int start = codeString.indexOf("/") + 1;
                    int last = codeString.indexOf(",");
                    String city = codeString.substring(start, last);
                    sharedPreferences.edit().putString("city", city).apply();

                    return true;


            }}catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(MapsActivity.this, "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_LONG).show();
            }
        }
    }

    class GetMarkerAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        JSONArray JsonArr;
        @Override
        protected Boolean doInBackground(JSONObject... object) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(
                    MediaType.parse("text/plain"), sharedPreferences.getString("city", "").toString());
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/announcement/getByCity")
                    .addHeader("Authorization", sharedPreferences.getString("token", "").toString())
                    .post(body)
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


                for(int i = 0; i<JsonArr.size(); i++){
                    JSONObject object = (JSONObject)JsonArr.get(i);
                    Announcement announcement = new Announcement();
                    announcement.setTitle(object.get("title").toString());
                    announcement.setEvent(object.get("event").toString());
                    announcement.setLatitude(object.get("latitude").toString());
                    announcement.setLongitude(object.get("longitude").toString());
                    double lat = Double.parseDouble(announcement.getLatitude());
                    double longi = Double.parseDouble(announcement.getLongitude());
                    markers.add(announcement);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, longi))
                            .title(announcement.getTitle())
                            .snippet(announcement.getEvent())
                            .icon(bitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_announcement_24)));
                    mMap.setInfoWindowAdapter(new CustomInfoWindowForGoogleMap(MapsActivity.this));
                }

            } else{
                Toast.makeText(MapsActivity.this, "Hata!", Toast.LENGTH_LONG).show();

            }
        }
    }

    private BitmapDescriptor bitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}