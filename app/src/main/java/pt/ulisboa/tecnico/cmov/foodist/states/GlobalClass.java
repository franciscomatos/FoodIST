package pt.ulisboa.tecnico.cmov.foodist.states;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;

public class GlobalClass extends Application {
    private String CAMPUS = "Select a campus";
    private String OTHERCAMPUS;
    private double LATITUDE;
    private double LONGITUDE;
    private String URL = "http://192.168.1.95:8000";
    private FoodService currentFoodService;
    private User user = new User("User1", "ist111111", User.UserCourse.MEIC);

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
            Log.i("Location: ", "long-lat" + getLongitude() + "-" + getLatitude());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };

    private double[] AlamedaLatitude = new double[]{38.735740, 38.739740 };
    private double[] AlamedaLongitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLatitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLongitude = new double[]{38.735740, 38.739740 };
    private ArrayList<FoodService> listFoodServices;
    private Map<String, FoodService> foodServices = new HashMap<String, FoodService>(){{
        put("CIVIL", new FoodService("CIVIL", "RESTAURANT", "0000-01-01T10:00:00Z", "0000-01-01T20:00:00Z", 38.737069, -9.140017, new Menu(user.getDietaryConstraints())));
        put("ABILIO", new FoodService("ABILIO","BAR", "0000-01-01T10:00:00Z", "0000-01-01T20:00:00Z", 38.737135, -9.137655, new Menu(user.getDietaryConstraints())));
        put("AE", new FoodService("AE","RESTAURANT", "0000-01-01T10:00:00Z", "0000-01-01T22:00:00Z", 38.736221, -9.137195, new Menu(user.getDietaryConstraints())));
        put("GreenBar Tagus",new FoodService("GreenBar Tagus","BAR", "0000-01-01T10:00:00Z", "0000-01-01T20:00:00Z", 38.738019, -9.303139, new Menu(user.getDietaryConstraints()) ));
        put("Cafetaria", new FoodService("Cafetaria","RESTAURANT", "0000-01-01T10:00:00Z", "0000-01-01T20:00:00Z", 38.736582,  -9.302166, new Menu(user.getDietaryConstraints()) ));
    }};

    //FIXME:should be defined by the user
    private String username = "pedro";
    private String password = "123";


    public void getLocation2(Activity activity){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 60000, 50, locationListener);
    }


    public ArrayList<FoodService> getCampusFoodServices(String campus) {
        listFoodServices = new ArrayList<FoodService>();
        if (campus == "Alameda") {
            listFoodServices.add(foodServices.get("CIVIL"));
            listFoodServices.add(foodServices.get("ABILIO"));
            listFoodServices.add(foodServices.get("AE"));

        } else if (campus == "Taguspark") {
            listFoodServices.add(foodServices.get("GreenBar Tagus"));
            listFoodServices.add(foodServices.get("Cafetaria"));

        }
        return listFoodServices;
    }

    public String getCampus() {
        return CAMPUS;
    }
    public void setCampus(String campus) {
        this.CAMPUS = campus;
        if (campus == "Alameda") {
            this.OTHERCAMPUS = "Taguspark";
        } else {
            this.OTHERCAMPUS = "Alameda";
        }
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public String getOtherCampus() {
        return OTHERCAMPUS;
    }

    public void setOtherCampus(String OTHERCAMPUS) {
        this.OTHERCAMPUS = OTHERCAMPUS;
    }

    public double getLongitude() {
        return LONGITUDE;
    }

    public void setLongitude(double longitude) {
        this.LONGITUDE = longitude;
    }

    public double getLatitude() {
        return LATITUDE;
    }

    public void setLatitude(double latitude) {
        this.LATITUDE = latitude;
    }


    public double[] getAlamedaLatitude() {
        return AlamedaLatitude;
    }

    public void setAlamedaLatitude(double[] alamedaLatitude) {
        AlamedaLatitude = alamedaLatitude;
    }

    public double[] getAlamedaLongitude() {
        return AlamedaLongitude;
    }

    public void setAlamedaLongitude(double[] alamedaLongitude) {
        AlamedaLongitude = alamedaLongitude;
    }

    public double[] getTagusLatitude() {
        return TagusLatitude;
    }

    public void setTagusLatitude(double[] tagusLatitude) {
        TagusLatitude = tagusLatitude;
    }

    public String getUsername() { return this.username; }

    public void setUsername(String user) { this.username = user; }

    public String getPassword() { return this.password; }

    public void setPassword(String pass) { this.password = pass; }

    public double[] getTagusLongitude() {
        return TagusLongitude;
    }

    public void setTagusLongitude(double[] tagusLongitude) {
        TagusLongitude = tagusLongitude;
    }

    public ArrayList<FoodService> getListFoodServices() {
        return listFoodServices;
    }

    public void setListFoodServices(ArrayList<FoodService> listFoodServices) {
        this.listFoodServices = listFoodServices;
    }

    public FoodService getFoodService(String name) {
        return foodServices.get(name);
    }

    public String getURL() {
        return this.URL;
    }

    public Boolean isFoodService(String foodServiceName) {
        return foodServices.containsKey(foodServiceName);
    }

    public void setCurrentFoodService(String foodServiceName) {
        if (isFoodService(foodServiceName)) {
            this.currentFoodService = foodServices.get(foodServiceName);
        }
        else if (foodServiceName == "") { // left queue
            currentFoodService = null;
        }
        else {
            Log.e("GlobalError:", "food service "+ foodServiceName + " not found." );

        }
    }

    public FoodService getCurrentFoodService() {
        return this.currentFoodService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void updateMenuConstraints() {
        for (String key : foodServices.keySet()) {
            foodServices.get(key).getMenu().updateConstraints(user.getDietaryConstraints());
            foodServices.get(key).getMenu().updateConstraintDishes();
        }
    }

    public void addDish(String foodServiceName, Dish dish) {
        foodServices.get(foodServiceName).getMenu().addDish(dish);
    }
}
