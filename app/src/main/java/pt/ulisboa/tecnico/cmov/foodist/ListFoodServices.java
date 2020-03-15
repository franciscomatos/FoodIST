package pt.ulisboa.tecnico.cmov.foodist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ListFoodServices extends AppCompatActivity {


    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodService> listFoodServices;
    private String ETA;
    private FusedLocationProviderClient fusedLocationClient;
    //private Location myLocation;
    private String latitude;
    private String longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_services);
        recyclerView = (RecyclerView) findViewById(R.id.FoodServices);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getLocation();
        fetchData process = new fetchData(this, latitude, longitude);
        process.execute();
        /*
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task myTask = fusedLocationClient.getLastLocation();
        //this.myLocation = ;
        */
    }

    public String getETA() {
        return ETA;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    public void AddWhenReady(String ETA) {
        listFoodServices = new ArrayList<FoodService>();
        listFoodServices.add(new FoodService("CIVIL", "10:00", "20:00", ETA));
        listFoodServices.add(new FoodService("ABILIO", "10:00", "20:00", ETA));
        listFoodServices.add(new FoodService("AE", "10:00", "22:00", ETA));
        mAdapter = new MyAdapter(listFoodServices);
        recyclerView.setAdapter(mAdapter);

    }

    //Create URL with the coordinates of the places
    public String buildDistancceURL() {

        ArrayList<ServiceCoordinates> ServicesCoordinates = new ArrayList<ServiceCoordinates>();
        String url = "";
        return url;

    }

    public void getLocation() {

        LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);


       // Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        Location location = getLastKnownLocation();
        this.latitude = "" + location.getLatitude();
        this.longitude = "" + location.getLongitude();
        Log.d("Location: ", "long-lat" + longitude + "-" + latitude);



    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    /*
    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }*/
}
