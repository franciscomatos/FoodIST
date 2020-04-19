package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;

public class DishActivity extends FragmentActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3};
    private String dishName;
    private String category;
    private String price;
    private ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        /*ImageView iv_background =findViewById(R.id.iv_background);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_background.getDrawable();
        animationDrawable.start();*/

        // MISSING: GET IMAGES FROM SERVER AND CACHE THEM
        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        Intent intent = getIntent();
        dishName = intent.getStringExtra("name");
        category = intent.getStringExtra("category");
        price = intent.getStringExtra("price");

        TextView nameView = findViewById(R.id.dishName);
        nameView.setText(dishName);

        TextView categoryView = findViewById(R.id.dishCategory);
        categoryView.setText(category);

        TextView priceView = findViewById(R.id.dishPrice);
        priceView.setText(price);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_explore:
                        Intent intent =  new Intent(DishActivity.this, ListFoodServicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        Intent profileIntent =  new Intent(DishActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                }
                return true;
            }
        });
    }

    public void goToAddPictureActivity(View v) {
        Intent intent =  new Intent(DishActivity.this, AddPictureActivity.class);
        intent.putExtra("name", dishName);
        intent.putExtra("category", category);
        intent.putExtra("price", price);
        startActivity(intent);
    }


}
