package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchCacheImages;

public class DishActivity extends FragmentActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3};
    private String dishName;
    private String category;
    private String price;
    private String foodService;
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
        GlobalClass global = (GlobalClass) getApplicationContext();

        /*ImageView iv_background =findViewById(R.id.iv_background);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_background.getDrawable();
        animationDrawable.start();*/

        // MISSING: GET IMAGES FROM SERVER AND CACHE THEM
        carouselView = findViewById(R.id.carouselView);
        //carouselView.setPageCount(sampleImages.length);
        //carouselView.setImageListener(imageListener);

        Intent intent = getIntent();
        dishName = intent.getStringExtra("name");
        category = intent.getStringExtra("category");
        price = intent.getStringExtra("price");
        foodService = intent.getStringExtra("foodService");

        TextView nameView = findViewById(R.id.dishName);
        nameView.setText(dishName);

        TextView categoryView = findViewById(R.id.dishCategory);
        categoryView.setText(category);

        TextView priceView = findViewById(R.id.dishPrice);
        priceView.setText(price);

        fetchCacheImages process = new fetchCacheImages(global, carouselView, foodService, dishName, 0);
        process.execute();


    }

    public void goToAddPictureActivity(View v) {
        Intent intent =  new Intent(DishActivity.this, AddPictureActivity.class);
        intent.putExtra("name", dishName);
        intent.putExtra("category", category);
        intent.putExtra("price", price);
        intent.putExtra("foodService", foodService);
        startActivity(intent);
    }


}
