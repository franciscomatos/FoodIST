package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.popups.PopUpClass;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class FoodServiceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TableLayout tableLayout;
    private Menu menuState;
    private String route;
    private FoodService foodService;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_service);
        GlobalClass global = (GlobalClass) getApplicationContext();
        this.foodService = global.getFoodService(getIntent().getStringExtra("foodService"));
        this.menuState = this.foodService.getMenu();
        mapView = (MapView) findViewById(R.id.map);
        mapView.setClickable(false);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fillInfo(getIntent().getExtras().getString("duration"));
        Log.i("Duration", getIntent().getExtras().getString("duration"));

    }

    private void fillInfo(String duration) {

        TextView name = (TextView) findViewById(R.id.name);
        TextView openingHour = (TextView) findViewById(R.id.openingHour);
        TextView is_open = (TextView) findViewById(R.id.is_open);
        TextView distance = (TextView) findViewById(R.id.ETA);
        ImageView photo = (ImageView) findViewById(R.id.service_photo);

        GlobalClass global = (GlobalClass) getApplicationContext();

        name.setText(foodService.getName());
        openingHour.setText(foodService.getName());

        if (foodService.getType() == "RESTAURANT") {
            photo.setImageResource(R.drawable.ic_restaurant);
        } else {
            photo.setImageResource(R.drawable.coffee4);
        }


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        DateFormat presDateFormat = new SimpleDateFormat("HH:mm");
        Date current = new Date();   // given date

        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(foodService.getOpeningHour());
        Instant i = Instant.from(ta);
        Date open = Date.from(i);

        ta = DateTimeFormatter.ISO_INSTANT.parse(foodService.getClosingHour());
        i = Instant.from(ta);
        Date close = Date.from(i);

        openingHour.setText(presDateFormat.format(open) + " - " + presDateFormat.format(close));
        distance.setText(duration);

        Log.i("MYLOGS", presDateFormat.format(open) + " " + presDateFormat.format(close));
        Log.i("MYLOGS", presDateFormat.format(open) + " " + presDateFormat.format(close));

        if (current.compareTo(close) < 0) {
            is_open.setText("Open");
            is_open.setTextColor(0xFF00AA00);
        } else {
            is_open.setText("Closed");
            is_open.setTextColor(Color.RED);
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("MYLOGS", "Map ready");
        ArrayList markerPoints = new ArrayList();
        GlobalClass global = (GlobalClass) getApplicationContext();
        LatLng camera = new LatLng(global.getLatitude(), global.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 16));
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
        final PopUpClass popUpClass = new PopUpClass();
        final View popupView = popUpClass.showPopupWindow(v, R.layout.pop_up_window_route);

//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                background.getForeground().setAlpha(0);
//                popUpClass.onTouch();
//                return true;
//            }
//        });
    }

    public void showMenu(View view) {
        Intent intent =  new Intent(FoodServiceActivity.this, MenuActivity.class);
        intent.putExtra("foodService", this.foodService.getName());
        startActivity(intent);
    }

}
