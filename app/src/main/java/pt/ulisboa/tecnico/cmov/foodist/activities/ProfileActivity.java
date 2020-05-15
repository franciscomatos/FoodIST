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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View.OnClickListener;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

import pt.ulisboa.tecnico.cmov.foodist.fetch.logout;

public class ProfileActivity extends AppCompatActivity {

    private User user;
    private List<Boolean> checkedBoxes;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalClass global = (GlobalClass) getApplicationContext();
        this.user = global.getUser();

        if(this.user == null) redirectToLoginPage();
        else setRegularUserPage();

    }

    public void redirectToLoginPage() {
        Intent toLoginPage = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(toLoginPage);
    }

    public void redirectToAddProfPic() {
        Intent toPicPage = new Intent(ProfileActivity.this, AddPictureProfileActivity.class);
        startActivity(toPicPage);
    }

    @SuppressLint("RestrictedApi")
    public void setRegularUserPage() {
        setContentView(R.layout.activity_profile);

        String username = this.user.getUsername();
        String email = this.user.getEmail();
        String istNumber = this.user.getIstNumber();
        AnnotationStatus status = this.user.getStatus();

        TextView usernameTextView = findViewById(R.id.username);
        usernameTextView.setText(username);

        TextView istNumberTextView = findViewById(R.id.istNumber);
        istNumberTextView.setText(istNumber);

        TextView statusTextView = findViewById(R.id.status);
        statusTextView.setText(status.getStatus());

        // get the button view
        CircleImageView img = (CircleImageView) findViewById(R.id.profile_image);

        // if the profile pic was updated
        if(user.getImage() != null) {
            img.setImageBitmap(user.getImage());
        }
        
        // set a onclick listener for when the button gets clicked
        img.setOnClickListener(new OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                redirectToAddProfPic(); // the circle view should also be sent to be changed
            }
        });

        checkedBoxes= Arrays.asList(false, false, false, false);

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
                if (checked && !global.getUser().containsConstraint(Dish.DishCategory.FISH)) {
                    global.getUser().addConstraint(Dish.DishCategory.FISH);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(0, true);
                }
                else {
                    global.getUser().removeConstraint(Dish.DishCategory.FISH);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(0, false);
                }
                break;
            case R.id.meatCheckbox:
                if (checked && !global.getUser().containsConstraint(Dish.DishCategory.MEAT)) {
                    global.getUser().addConstraint(Dish.DishCategory.MEAT);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(1, true);
                }
                else {
                    global.getUser().removeConstraint(Dish.DishCategory.MEAT);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(1, false);
                }
                break;
            case R.id.vegetarianCheckbox:
                if (checked && !global.getUser().containsConstraint(Dish.DishCategory.VEGETARIAN)) {
                    global.getUser().addConstraint(Dish.DishCategory.VEGETARIAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(2, true);
                }
                else {
                    global.getUser().removeConstraint(Dish.DishCategory.VEGETARIAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(2, false);
                }
                break;
            case R.id.veganCheckbox:
                if (checked && !global.getUser().containsConstraint(Dish.DishCategory.VEGAN)) {
                    global.getUser().addConstraint(Dish.DishCategory.VEGAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(3, true);
                }
                else {
                    global.getUser().removeConstraint(Dish.DishCategory.VEGAN);
                    global.updateMenuConstraints();
                    ProfileActivity.this.checkedBoxes.set(3, false);
                }
                break;
        }
    }

    public void logout(View view) {

        GlobalClass global = (GlobalClass) getApplicationContext();
        logout logout = new logout (global, this.user.getUsername(), this.user.getPassword());
        //register register = new login (global,email,password, FIXME: add here userType and dietary stuff);
        logout.execute();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}
