package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

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
import java.text.ParseException;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.fetchMap;
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
        fetchMap process = new fetchMap(this, getStartEnd() );
        process.execute();
        mapView = (MapView) findViewById(R.id.map);
        mapView.setClickable(false);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setVisibility(View.GONE);

        LinearLayout info = (LinearLayout) findViewById(R.id.info);
        info.setVisibility(info.isShown() ? View.GONE : View.VISIBLE);
       // Log.i("MYLOGS", route);

    }

    private ArrayList<String> getStartEnd() {
        GlobalClass global = (GlobalClass) getApplicationContext();

        String sourceLatitude = global.getLatitude() + "";
        String sourceLongitude = global.getLongitude() + "";
        String targetLatitude = this.foodService.getLatitude() + "";
        String targetLongitude = this.foodService.getLongitude() + "";
        ArrayList<String> startEnd = new ArrayList<String>();
        startEnd.add(sourceLongitude);
        startEnd.add(sourceLatitude);
        startEnd.add(targetLongitude);
        startEnd.add(targetLatitude);
        return startEnd;
    }

    public void setRoute(String response) {
/*
        MapView map = (MapView) findViewById(R.id.map);
        map.getMapAsync(FoodServiceActivity.this);

        JsonParser parser = new JsonParser();
        GeoJsonLayer layer = new GeoJsonLayer(map, (JsonElement) parser.parse(route));*/
        this.route = response;
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
/*
        try {

            JSONObject json = new JSONObject(route);
            GeoJsonLayer layer = new GeoJsonLayer(map, json);
            layer.addLayerToMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.addPolyline()

 */
    }

    public void toggle_contents(View view) {
        mapView.setVisibility(mapView.isShown() ? View.GONE : View.VISIBLE);
        LinearLayout info = (LinearLayout) findViewById(R.id.info);
        info.setVisibility(info.isShown() ? View.GONE : View.VISIBLE);
    }
}
