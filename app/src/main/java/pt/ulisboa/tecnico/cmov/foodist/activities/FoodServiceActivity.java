package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.popups.PopUpClass;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class FoodServiceActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Menu menuState;
    private String route;
    private FoodService foodService;
    private MapView mapView;
    private MapView mapView2;
    private Bundle sis;
    private ImageButton shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sis = savedInstanceState;
        setContentView(R.layout.activity_food_service);
        GlobalClass global = (GlobalClass) getApplicationContext();
        FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(0); // restore
        this.foodService = global.getFoodService(getIntent().getStringExtra("foodService"));
        this.menuState = this.foodService.getMenu();
        mapView = (MapView) findViewById(R.id.map);
        //mapView.setClickable(false);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady1);
        //mapView.performClick();
        String duration = getIntent().getExtras().getString("duration");
        String queue = getIntent().getExtras().getString("queue");
        fillInfo(duration, queue);

        shareButton = (ImageButton) findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = foodService.toString();
                String shareSub = "Eat in IST";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
                
        TextView averageBigView = findViewById(R.id.averageBig);
        averageBigView.setText(menuState.computeRatingAverage().toString());

        RatingBar averageRatingBar = findViewById(R.id.averageRatingBarDisplay);
        averageRatingBar.setRating(menuState.computeRatingAverage().floatValue());

        TextView ratingsCounter = findViewById(R.id.numberOfRatings);
        ratingsCounter.setText(menuState.computeNumberOfRatings().toString());

        AnyChartView ratingChartView = findViewById(R.id.rating_chart_view);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<Integer, Integer> classification: menuState.getRatings().entrySet()) {
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
                        Intent intent =  new Intent(FoodServiceActivity.this, ListFoodServicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        Intent profileIntent =  new Intent(FoodServiceActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                }
                return true;
            }
        });

    }

    private void fillInfo(String duration, String queue) {

        TextView name = (TextView) findViewById(R.id.name);
        TextView openingHour = (TextView) findViewById(R.id.openingHour);
        TextView status = (TextView) findViewById(R.id.is_open);
        TextView distance = (TextView) findViewById(R.id.ETA);
        TextView queueTime = (TextView) findViewById(R.id.queue);
        ImageView photo = (ImageView) findViewById(R.id.service_photo);

        GlobalClass global = (GlobalClass) getApplicationContext();

        name.setText(foodService.getName());
        openingHour.setText(foodService.getName());

        if (foodService.getType() == "RESTAURANT") {
            photo.setImageResource(R.drawable.ic_restaurant);
        } else {
            photo.setImageResource(R.drawable.coffee4);
        }

        DateFormat presDateFormat = new SimpleDateFormat("HH:mm");
        Date current = new Date();   // given date

        try {
            String openString = foodService.getOpeningHour();
            String closeString = foodService.getClosingHour();
            Date open = presDateFormat.parse(openString);
            Date close = presDateFormat.parse(closeString);
            current = presDateFormat.parse(presDateFormat.format(current));
            openingHour.setText(openString + " - " + closeString);
            if (current.compareTo(close) < 0 && current.compareTo(open) > 0) {
                status.setText(R.string.Open);
                status.setTextColor(0xFF00AA00);
            } else {
                status.setText(R.string.Closed);
                status.setTextColor(Color.RED);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        distance.setText(duration);

        queueTime.setText(queue);



    }

    public void onMapReady1(GoogleMap map) {
        Log.i("MYLOGS", "Map ready1");

        GlobalClass global = (GlobalClass) getApplicationContext();
        LatLng camera = new LatLng(foodService.getLatitude(), foodService.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 18));
        LatLng target = new LatLng(foodService.getLatitude(), foodService.getLongitude());
        map.addMarker(new MarkerOptions().position(target));
//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
//            @Override
//            public void onMapClick(LatLng arg0)
//            {
//                Log.i("MYLOGS", findViewById(R.id.map).getResources().getResourceName((mapView.getId())));
//                showPopUp(findViewById(R.id.map));
//            }
//        });

    }

    public void onMapReady2(GoogleMap map) {
        Log.i("MYLOGS", "Map ready2");
        GlobalClass global = (GlobalClass) getApplicationContext();
        LatLng camera = new LatLng(global.getLatitude(), global.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 18));
        ArrayList markerPoints = new ArrayList();
        markerPoints.add(camera);
        LatLng target = new LatLng(foodService.getLatitude(), foodService.getLongitude());
        markerPoints.add(target);
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(markerPoints);
        lineOptions.width(12);
        lineOptions.color(Color.RED);
        lineOptions.geodesic(true);
        map.addPolyline(lineOptions);
        map.addMarker(new MarkerOptions().position(target));
    }


    public void showPopUp(View v) {
        // Create a button handler and call the dialog box display method in it
        Log.i("Popup", "started");
        final PopUpClass popUpClass = new PopUpClass();
        final View popupView = popUpClass.showPopupWindow(v, R.layout.pop_up_window_route);
        mapView2 = (MapView) popupView.findViewById(R.id.map2);

        mapView2.onCreate(sis);
        mapView2.onStart();
        mapView2.setClickable(false);

        mapView2.getMapAsync(this::onMapReady2);
        mapView2.performClick();

//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                background.getForeground().setAlpha(0);
//                popUpClass.onTouch();
//                return true;
//            }
//        });

        final FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(220); // dim
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() != mapView.getId()) {
                    background.getForeground().setAlpha(0);
                    popUpClass.onTouch();
                }
                else {
                    Log.i("MYLOGS", "map clicked");
                }
                return true;
            }
        });
    }

    public void showMenu(View view) {
        Intent intent =  new Intent(FoodServiceActivity.this, MenuActivity.class);
        intent.putExtra("foodService", this.foodService.getName());
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


}
