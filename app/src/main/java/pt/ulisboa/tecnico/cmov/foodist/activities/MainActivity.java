package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureFoodListButton();
        GlobalClass global = (GlobalClass) getApplicationContext();
        //global.setFusedLocationClient(LocationServices.getFusedLocationProviderClient(this));

    }


    private void configureFoodListButton() {
        Button foodListButton = (Button) findViewById(R.id.foodServicesButton);
        foodListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this, ListFoodServicesActivity.class));
            }
        });
    }


}
