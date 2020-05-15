package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchDishRatings;
import pt.ulisboa.tecnico.cmov.foodist.fetch.rateMenu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchCacheImages;

public class DishActivity extends FragmentActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3};
    private String dishName;
    private String category;
    private String price;
    private String foodServiceName;
    private Integer dishIndex;
    private Dish dish;
    private Menu menu;
    private ImageButton shareButton;

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

        foodServiceName = intent.getStringExtra("foodService");
        Integer m = intent.getIntExtra("dishIndex",10);
        Log.i("MYLOGS", "MMMMMMM" + m);
        dishIndex = m;

        menu = global.getFoodService(foodServiceName).getMenu();
        dish = menu.getDish(dishIndex);


        TextView nameView = findViewById(R.id.dishName);
        nameView.setText(dishName);

        TextView categoryView = findViewById(R.id.dishCategory);
        categoryView.setText(category);

        TextView priceView = findViewById(R.id.dishPrice);
        priceView.setText(price);


        // initiate rating bar and a button
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submitRatingButton = findViewById(R.id.submitRattingButton);

        User user = global.getUser();
        // no ratings in guest mode
        if(user == null) submitRatingButton.setEnabled(false);
        // perform click event on button
        submitRatingButton.setOnClickListener(v -> {
            TextView averageBigView = findViewById(R.id.averageBig);
            RatingBar averageRatingBar = findViewById(R.id.averageRatingBarDisplay);
            TextView ratingsCounter = findViewById(R.id.numberOfRatings);
            AnyChartView ratingChartView = findViewById(R.id.rating_chart_view);

            // get values and then displayed in a toast
            String totalStars = "Total Stars:: " + ratingBar.getNumStars();
            String rating = "Rating :: " + ratingBar.getRating();
            rateMenu process = new rateMenu(global, foodServiceName, dishName, dishIndex,
                    (int)ratingBar.getRating());
            //process.execute();
            try {
                process.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fetchDishRatings process2 = new fetchDishRatings(global, foodServiceName, dishName, dishIndex,
                    averageBigView, averageRatingBar, ratingsCounter, ratingChartView);

            try {
                process2.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //global.addRating(foodServiceName, dishName, (int) ratingBar.getRating());

            String average = "Average:: " + DishActivity.this.dish.computeRatingAverage();
            Toast.makeText(getApplicationContext(), rating + "\n" + average, Toast.LENGTH_LONG).show();
            //updateRatings();
        });

        //updateRatings();


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

        fetchCacheImages process = new fetchCacheImages(global, carouselView, foodServiceName, dishName, 0);
        process.execute();

        shareButton = (ImageButton) findViewById(R.id.share3);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = foodServiceName + " " + dish.toString();
                String shareSub = "Eat in IST";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            }});


    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalClass global = (GlobalClass) getApplicationContext();
        TextView averageBigView = findViewById(R.id.averageBig);
        RatingBar averageRatingBar = findViewById(R.id.averageRatingBarDisplay);
        TextView ratingsCounter = findViewById(R.id.numberOfRatings);
        AnyChartView ratingChartView = findViewById(R.id.rating_chart_view);

        fetchDishRatings process2 = new fetchDishRatings(global, foodServiceName, dishName, dishIndex,
                averageBigView, averageRatingBar, ratingsCounter, ratingChartView);

        try {
            process2.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void goToAddPictureActivity(View v) {
        Intent intent =  new Intent(DishActivity.this, AddPictureActivity.class);
        intent.putExtra("name", dishName);
        intent.putExtra("category", category);
        intent.putExtra("price", price);
        intent.putExtra("foodService", foodServiceName);
        intent.putExtra("dishIndex", dishIndex);
        startActivity(intent);
    }

    public void updateRatings() {
        GlobalClass global = (GlobalClass) getApplicationContext();
        menu = global.getFoodService(foodServiceName).getMenu();
        dish = menu.getDish(dishIndex);

        TextView averageBigView = findViewById(R.id.averageBig);
        DecimalFormat df = new DecimalFormat("#.##");
        averageBigView.setText(df.format(dish.computeRatingAverage()));

        RatingBar averageRatingBar = findViewById(R.id.averageRatingBarDisplay);
        averageRatingBar.setRating(dish.computeRatingAverage().floatValue());

        TextView ratingsCounter = findViewById(R.id.numberOfRatings);
        ratingsCounter.setText(dish.computeNumberOfRatings().toString());

        AnyChartView ratingChartView = findViewById(R.id.rating_chart_view);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<Integer, Integer> classification: dish.getRatings().entrySet()) {
            data.add(new ValueDataEntry(classification.getKey(), classification.getValue()));
        }

        for(DataEntry entry: data) {
            Toast.makeText(getApplicationContext(), entry.generateJs(), Toast.LENGTH_LONG).show();
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
    }


}
