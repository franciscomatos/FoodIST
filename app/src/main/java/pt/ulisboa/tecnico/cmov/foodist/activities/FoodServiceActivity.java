package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
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
        openingHour.setText(foodService.getOpeningHour() + " - " + foodService.getClosingHour());
        distance.setText(duration);

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minutes = calendar.get(Calendar.MINUTE);
        String[] openingSplit = foodService.getOpeningHour().split(":");
        String[] closingSplit = foodService.getClosingHour().split(":");
        Log.i("MYLOGS", openingSplit[0] + " " + closingSplit[0]);
        Log.i("MYLOGS", openingSplit[1] + " " + closingSplit[1]);
        Log.i("MYLOGS", hours + " " + minutes);
        if (Integer.parseInt(openingSplit[0]) == hours && Integer.parseInt(openingSplit[1]) <= minutes) {
            is_open.setText("Open");
            is_open.setTextColor(0xFF00AA00);

        } else if (Integer.parseInt(closingSplit[0]) == hours && Integer.parseInt(closingSplit[1]) > minutes ) {
            is_open.setText("Open");
            is_open.setTextColor(0xFF00AA00);

        } else if( Integer.parseInt(openingSplit[0]) < hours &&
                Integer.parseInt(closingSplit[0]) > hours ) {
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

    public void showMenu(View view) {
        Intent intent =  new Intent(FoodServiceActivity.this, MenuActivity.class);
        intent.putExtra("foodService", this.foodService.getName());
        startActivity(intent);
    }

}
