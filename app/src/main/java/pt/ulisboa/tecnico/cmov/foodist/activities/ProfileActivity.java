package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class ProfileActivity extends AppCompatActivity {

    private User user;
    private List<Boolean> checkedBoxes = Arrays.asList(true, true, true, true);

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GlobalClass global = (GlobalClass) getApplicationContext();
        this.user = global.getUser();

        if(user.containsConstraint(Dish.DishCategory.FISH)) checkedBoxes.set(0, true);
        if(user.containsConstraint(Dish.DishCategory.MEAT)) checkedBoxes.set(1, true);
        if(user.containsConstraint(Dish.DishCategory.VEGETARIAN)) checkedBoxes.set(2, true);
        if(user.containsConstraint(Dish.DishCategory.VEGAN)) checkedBoxes.set(3, true);

        CheckBox fishBox = findViewById(R.id.fishCheckbox);
        fishBox.setChecked(this.checkedBoxes.get(0));
        CheckBox meatBox = findViewById(R.id.meatCheckbox);
        meatBox.setChecked(this.checkedBoxes.get(1));
        CheckBox vegetarianBox = findViewById(R.id.vegetarianCheckbox);
        vegetarianBox.setChecked(this.checkedBoxes.get(2));
        CheckBox veganBox = findViewById(R.id.veganCheckbox);
        veganBox.setChecked(this.checkedBoxes.get(3));

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

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        GlobalClass global = (GlobalClass) getApplicationContext();

        CheckBox box = (CheckBox)view;

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.fishCheckbox:
                if (checked && !this.user.containsConstraint(Dish.DishCategory.FISH)) {
                    ProfileActivity.this.user.addConstraint(Dish.DishCategory.FISH);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(0, true);
                }
                else {
                    ProfileActivity.this.user.removeConstraint(Dish.DishCategory.FISH);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(0, false);
                }
                break;
            case R.id.meatCheckbox:
                if (checked && !this.user.containsConstraint(Dish.DishCategory.MEAT)) {
                    ProfileActivity.this.user.addConstraint(Dish.DishCategory.MEAT);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(1, true);
                }
                else {
                    ProfileActivity.this.user.removeConstraint(Dish.DishCategory.MEAT);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(1, false);
                }
                break;
            case R.id.vegetarianCheckbox:
                if (checked && !this.user.containsConstraint(Dish.DishCategory.VEGETARIAN)) {
                    ProfileActivity.this.user.addConstraint(Dish.DishCategory.VEGETARIAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(2, true);
                }
                else {
                    ProfileActivity.this.user.removeConstraint(Dish.DishCategory.VEGETARIAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(2, false);
                }
                break;
            case R.id.veganCheckbox:
                if (checked && !this.user.containsConstraint(Dish.DishCategory.VEGAN)) {
                    ProfileActivity.this.user.addConstraint(Dish.DishCategory.VEGAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(3, true);
                }
                else {
                    ProfileActivity.this.user.removeConstraint(Dish.DishCategory.VEGAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(3, false);
                }
                break;
        }
    }
}
