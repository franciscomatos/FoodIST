package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class ProfileActivity extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationItemView menuItemExplore = findViewById(R.id.action_explore);
        BottomNavigationItemView menuItemProfile = findViewById(R.id.action_profile);

        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        menuItemExplore.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_local_pizza_outline_24px));
        menuItemProfile.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_person_24px));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_explore:
                        Intent intent =  new Intent(ProfileActivity.this, ListFoodServicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        // go to profile activity yet to be created
                        break;
                }
                return true;
            }
        });
    }
}
