package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.MyAdapter;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.fetchData;

public class ListFoodServicesActivity extends AppCompatActivity {


    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodService> listFoodServices;
    private GlobalClass global;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            FoodService foodService = listFoodServices.get(position);
            Intent intent = new Intent(ListFoodServicesActivity.this, FoodServiceActivity.class);
            intent.putExtra("foodService", foodService.getName());
            TextView ETA = view.findViewById(R.id.ETA);
            intent.putExtra("duration", ETA.getText());
            //Toast.makeText(ListFoodServicesActivity.this, "You Clicked: " + foodService.getName(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    };


    private class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

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

        getCampus();

        if (global.getCampus() != "Select a campus") {
            fetchData process = new fetchData(this, global);
            process.execute();
        }

        ListFoodServicesActivity listFoodServicesActivity = this;
        Spinner dropdown = findViewById(R.id.campus);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String newCampus = dropdown.getSelectedItem().toString();
                if (global.getCampus() != newCampus ) {
                    global.setCampus(newCampus);
                    getLocation2();
                    fetchData process = new fetchData(listFoodServicesActivity, global);
                    process.execute();
                    updateSpinner(global.getCampus(), dropdown);
                    //Log.i("LOL", "Campus set to " + global.getCampus());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        updateSpinner(global.getCampus(), dropdown);

    }

    public void getCampus() { //TODO change numbers to a class with min/max coordinates
        GlobalClass global = (GlobalClass) getApplicationContext();
        double latitude = global.getLatitude();
        double longitude = global.getLatitude();
        if (global.getAlamedaLatitude()[0] < latitude && latitude < global.getAlamedaLatitude()[1]) {
            if (global.getAlamedaLongitude()[0] < longitude && longitude < global.getAlamedaLongitude()[1]) {
                global.setCampus("Alameda");
                Log.i("CAMPUS", global.getCampus());

            }
        } else if (global.getTagusLatitude()[0] < latitude && latitude < global.getTagusLatitude()[1]) {
            if (global.getTagusLongitude()[0] < longitude && longitude < global.getTagusLongitude()[1]) {
                global.setCampus("Taguspark");

            }
        } else { // keep the original
            return;
        }
    }


    private void getLocation2() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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


    public void setView(String data) {

        listFoodServices = global.getCampusFoodServices(global.getCampus());
        mAdapter = new MyAdapter(listFoodServices, data);
        mAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(mAdapter);

    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }


}
