package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;


import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class DishActivity extends FragmentActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3};
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

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        String price = intent.getStringExtra("price");

        TextView nameView = findViewById(R.id.dishName);
        nameView.setText(name);

        TextView categoryView = findViewById(R.id.dishCategory);
        categoryView.setText(category);

        TextView priceView = findViewById(R.id.dishPrice);
        priceView.setText(price);
    }


}
