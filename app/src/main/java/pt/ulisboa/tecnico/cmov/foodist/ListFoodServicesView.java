package pt.ulisboa.tecnico.cmov.foodist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_services);
        recyclerView = (RecyclerView) findViewById(R.id.FoodServices);
        recyclerView.setHasFixedSize(true);
        this.global = (GlobalClass) getApplicationContext();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getLocation();
        getCampus();

        fetchData process = new fetchData(this, global);
        process.execute();

        ListFoodServicesView listFoodServicesView = this;
        Spinner dropdown = findViewById(R.id.campus);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setCampus(dropdown.getSelectedItem().toString());
                fetchData process = new fetchData(listFoodServicesView, global);
                process.execute();
                updateSpinner(global.getCampus(), dropdown);
                Log.i("MYLOGS", "Campus set to " + global.getCampus());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        updateSpinner(global.getCampus(), dropdown);
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

    /*
    public void getLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        Location location = getLastKnownLocation();
        global.setLatitude(location.getLatitude());
        global.setLatitude(location.getLongitude());
        //Log.d("Location: ", "long-lat" + longitude + "-" + latitude);
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
    }*/

    private void getLocation() {
        global.getFusedLocationClient().getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            global.setLatitude(location.getLatitude());
                            global.setLatitude(location.getLongitude());
                        }
                    }
                });
    }


    public void setView(String ETA) {
        listFoodServices = global.getCampusFoodServices(global.getCampus());
        for (int i=0; i< listFoodServices.size(); i++) {
        }
        mAdapter = new MyAdapter(listFoodServices);
        recyclerView.setAdapter(mAdapter);

    }

    //Create URL with the coordinates of the places
    public String buildDistancceURL() {

        ArrayList<ServiceCoordinates> ServicesCoordinates = new ArrayList<ServiceCoordinates>();
        String url = "";
        return url;

    }


    /*
    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }*/
}
