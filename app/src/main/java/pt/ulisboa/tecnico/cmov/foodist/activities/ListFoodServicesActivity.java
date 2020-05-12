package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.adapters.FoodServicesAdapter;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchData;



public class ListFoodServicesActivity extends AppCompatActivity {


    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FoodServicesAdapter adapter;
    private ArrayList<FoodService> listFoodServices;
    private GlobalClass global ;;
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
            TextView queue = view.findViewById(R.id.queue);
            intent.putExtra("queue", queue.getText());
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

        global = (GlobalClass) getApplicationContext();
        getCampus();
        recyclerView = (RecyclerView) findViewById(R.id.FoodServices);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setViewPrefetch();

        global.getLocation2(ListFoodServicesActivity.this);


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
                    global.getLocation2(ListFoodServicesActivity.this);
                    //setViewPostFetch("");
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
        double longitude = global.getLongitude();
        if (global.getAlamedaLatitude()[0] < latitude && latitude < global.getAlamedaLatitude()[1]) {
            if (global.getAlamedaLongitude()[0] < longitude && longitude < global.getAlamedaLongitude()[1]) {
                global.setCampus("Alameda");

            }
        } else if (global.getTagusLatitude()[0] < latitude && latitude < global.getTagusLatitude()[1]) {
            if (global.getTagusLongitude()[0] < longitude && longitude < global.getTagusLongitude()[1]) {
                global.setCampus("Taguspark");

            }
        } else if (global.getCTNLatitude()[0] < latitude && latitude < global.getCTNLatitude()[1]) {
            if (global.getCTNLongitude()[0] < longitude && longitude < global.getCTNLongitude()[1]) {
                global.setCampus("CTN");
            }
        } else { // keep the original
            return;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GlobalClass global = (GlobalClass) getApplicationContext();
                    global.getLocation2(ListFoodServicesActivity.this);
                }
        }
    }




    private void updateSpinner(String campus, Spinner dropdown) {
        String[] items;
        if (campus == "Select a campus") {
            items = new String[]{campus, "Alameda", "Taguspark", "CTN"};
        } else {
            items = new String[]{campus, global.getOtherCampus1(), global.getOtherCampus2()};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }


    public void setViewPrefetch() {
        listFoodServices = global.getCampusFoodServices(global.getCampus());
        adapter = new FoodServicesAdapter(listFoodServices);
        adapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);
    }

    public void setViewPostFetch(String data) {
        listFoodServices = global.getCampusFoodServices(global.getCampus());
        adapter = new FoodServicesAdapter(listFoodServices);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setDuration(data);
        recyclerView.swapAdapter(adapter, true);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }


}
