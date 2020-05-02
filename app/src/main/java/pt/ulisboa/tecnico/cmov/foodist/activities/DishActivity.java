package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
//import com.anychart.sample.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class DishActivity extends FragmentActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3};
    private String dishName;
    private String category;
    private String price;
    private String foodServiceName;
    private Integer dishIndex;
    private Dish dish;

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
        foodServiceName = intent.getStringExtra("foodService");
        Integer m = intent.getIntExtra("dishIndex",10);
        Log.i("MYLOGS", "MMMMMMM" + m);
        dishIndex = m;

        GlobalClass global = (GlobalClass) getApplicationContext();
        Menu menu = global.getFoodService(foodServiceName).getMenu();
        dish = menu.getDish(dishIndex);

        TextView nameView = findViewById(R.id.dishName);
        nameView.setText(dishName);

        TextView categoryView = findViewById(R.id.dishCategory);
        categoryView.setText(category);

        TextView priceView = findViewById(R.id.dishPrice);
        priceView.setText(price);

        TextView averageBigView = findViewById(R.id.averageBig);
        averageBigView.setText(dish.computeRatingAverage().toString());

        RatingBar averageRatingBar = findViewById(R.id.averageRatingBarDisplay);
        averageRatingBar.setRating(dish.computeRatingAverage().floatValue());

        TextView ratingsCounter = findViewById(R.id.numberOfRatings);
        ratingsCounter.setText(dish.computeNumberOfRatings().toString());


        // initiate rating bar and a button
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submitRatingButton = findViewById(R.id.submitRattingButton);
        // perform click event on button
        submitRatingButton.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars = "Total Stars:: " + ratingBar.getNumStars();
            String rating = "Rating :: " + ratingBar.getRating();
            DishActivity.this.dish.addRating((int) ratingBar.getRating());
            String average = "Average:: " + DishActivity.this.dish.computeRatingAverage();
            Toast.makeText(getApplicationContext(), rating + "\n" + average, Toast.LENGTH_LONG).show();
        });

        AnyChartView ratingChartView = findViewById(R.id.rating_chart_view);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<Integer, Integer> classification: dish.getRatings().entrySet()) {
            data.add(new ValueDataEntry(classification.getKey(), classification.getValue()));
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        //cartesian.animation(true);
        cartesian.yScale().minimum(0d);

        //cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        //cartesian.interactivity().hoverMode(HoverMode.BY_X);


        ratingChartView.setChart(cartesian);

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
