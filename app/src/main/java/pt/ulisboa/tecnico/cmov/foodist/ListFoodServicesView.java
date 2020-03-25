package pt.ulisboa.tecnico.cmov.foodist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ListFoodServicesView extends AppCompatActivity {


    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodService> listFoodServices;
    private GlobalClass global;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_services);
        recyclerView = (RecyclerView) findViewById(R.id.FoodServices);
        recyclerView.setHasFixedSize(true);
        this.global = (GlobalClass) getApplicationContext();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                global.setLatitude(location.getLatitude());
                global.setLongitude(location.getLongitude());
                Log.i("Location: ", "long-lat" + global.getLongitude() + "-" + global.getLatitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        getLocation2();

       // getLocation();
        getCampus();

        //fetchData process = new fetchData(this, global);
        //process.execute();


        ListFoodServicesView listFoodServicesView = this;
        Spinner dropdown = findViewById(R.id.campus);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String newCampus = dropdown.getSelectedItem().toString();
                if (global.getCampus() != newCampus ) {
                    setCampus(newCampus);
                    getLocation2();
                    fetchData process = new fetchData(listFoodServicesView, global);
                    process.execute();
                    updateSpinner(global.getCampus(), dropdown);
                    Log.i("LOL", "Campus set to " + global.getCampus());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        updateSpinner(global.getCampus(), dropdown);
    }

    private void getLocation2() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 60000, 50, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
            switch (requestCode) {
                case 10:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getLocation2();
                    }
            }
    }

    private void updateSpinner(String campus, Spinner dropdown) {
        String[] items;
        if (campus == "Select a campus") {
            items = new String[]{campus, "Alameda", "Taguspark"};
        } else {
            items = new String[]{campus, global.getOtherCampus()};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    public void setCampus(String campus) {
        if (campus == "Alameda") {
            global.setCampus("Alameda");
            global.setOtherCampus("Taguspark");
        } else if (campus == "Taguspark") {
            global.setCampus("Taguspark");
            global.setOtherCampus("Alameda");
        }

    }

    public void getCampus() { //TODO change numbers to a class with min/max coordinates
        double latitude = global.getLatitude();
        double longitude = global.getLatitude();
        if (global.getAlamedaLatitude()[0] < latitude && latitude < global.getAlamedaLatitude()[1]) {
            if (global.getAlamedaLongitude()[0] < longitude && longitude < global.getAlamedaLongitude()[1]) {
                setCampus("Alameda");
            }
        } else if (global.getTagusLatitude()[0] < latitude && latitude < global.getTagusLatitude()[1]) {
            if (global.getTagusLongitude()[0] < longitude && longitude < global.getTagusLongitude()[1]) {
                setCampus("Taguspark");
            }
        } else { // keep the original
            return;
        }
    }


    public void getLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        Location location = getLastKnownLocation();
        global.setLatitude(location.getLatitude());
        global.setLongitude(location.getLongitude());
        Log.i("Location: ", "long-lat" + global.getLongitude() + "-" + global.getLatitude());
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Log.i("MYLOGS", provider);
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                Log.i("MYLOGS", provider + " null");

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
    private void getLocation() {

        global.getFusedLocationClient().getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            global.setLatitude(location.getLatitude());
                            global.setLongitude(location.getLongitude());
                            Log.i("MyLOCATION", location.getLongitude() + " " + location.getLatitude());

                        }
                    }
                });
    }

*/
    public void setView(String data) {

        listFoodServices = global.getCampusFoodServices(global.getCampus());
        for (int i=0; i< listFoodServices.size(); i++) {
        }
        mAdapter = new MyAdapter(listFoodServices, data);
        recyclerView.setAdapter(mAdapter);

    }


}
