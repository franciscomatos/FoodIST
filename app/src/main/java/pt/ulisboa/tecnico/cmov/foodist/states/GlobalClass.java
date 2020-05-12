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
import android.util.LruCache;
import androidx.core.app.ActivityCompat;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;

import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;

public class GlobalClass extends Application  {
    private String CAMPUS = "Select a campus";
    private String OTHERCAMPUS1;
    private String OTHERCAMPUS2;
    private int CACHESIZE = 100*1024*1024; //100 MB
    private String OTHERCAMPUS;
    private double LATITUDE;
    private double LONGITUDE;
    private String URL = "https://192.168.1.95:443";
    private FoodService currentFoodService;
    private AnnotationStatus status = new AnnotationStatus(AnnotationStatus.STUDENT);
    private boolean connected  = false;
    private Context context;

    private LruCache<String,AppImage> imageMemCache = new LruCache<String,AppImage>(CACHESIZE){
        @Override
        protected int sizeOf(String key, AppImage image){
            return image.getImage().getAllocationByteCount() / (1024*1024); //to get the size in MB
        }
    };

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double[] AlamedaLatitude = new double[]{38.735090, 38.738468 };
    private double[] AlamedaLongitude = new double[]{-9.140879, -9.136375 };
    private double[] TagusLatitude = new double[]{38.736282, 38.738326 };
    private double[] TagusLongitude = new double[]{-9.303761, -9.301535 };
    private double[] CTNLatitude = new double[]{38.809919, 38.813611 };
    private double[] CTNLongitude = new double[]{-9.097291, -9.092026 };
    private ArrayList<FoodService> listFoodServices;
    private Map<String, FoodService> foodServices = new HashMap<String, FoodService>(){{
        //ALAMEDA
        put("Central Bar", new FoodService("Central Bar", "BAR", "09:00", "17:00", 38.736606, -9.139532, new Menu()));
        put("Civil Bar", new FoodService("Civil Bar", "BAR", "09:00", "17:00", 38.736988,  -9.139955, new Menu()));
        put("Civil Cafeteria", new FoodService("Civil Cafeteria", "RESTAURANT", "12:00", "15:00", 38.737650,  -9.140384, new Menu()));
        put("Sena Pastry Shop", new FoodService("Sena Pastry Shop", "RESTAURANT", "08:00", "19:00", 38.737677,  -9.138672, new Menu()));
        put("Mechy Bar", new FoodService("Mechy Bar", "BAR", "09:00", "17:00", 38.737247,   -9.137434, new Menu()));
        put("AEIST Bar", new FoodService("AEIST Bar", "BAR", "09:00", "17:00", 38.736542, -9.137226, new Menu()));
        put("AEIST Esplanade", new FoodService("AEIST Esplanade", "BAR", "09:00", "17:00", 38.736318, -9.137820, new Menu()));
        put("Chemy Bar", new FoodService("Chemy Bar", "BAR", "09:00", "17:00", 38.736240, -9.138302, new Menu()));
        put("SAS Cafeteria", new FoodService("SAS Cafeteria", "RESTAURANT", "09:00", "21:00", 38.736571, -9.137036, new Menu()));
        if (status.toString() == "STUDENT" || status.toString() == "PUBLIC" ) {
            put("Math Cafeteria", new FoodService("Math Cafeteria", "RESTAURANT", "13:30", "15:00", 38.735508,-9.139645, new Menu()));
        }
        else {
            put("Math Cafeteria", new FoodService("Math Cafeteria", "RESTAURANT", "12:00", "15:00", 38.735508,-9.139645, new Menu()));
        }
        put("Complex Bar", new FoodService("Complex Bar", "BAR", "09:30", "17:00", 38.736050,-9.140156, new Menu()));

        //TAGUS
        put("Tagus Cafeteria", new FoodService("Tagus Cafeteria", "RESTAURANT", "12:00", "15:00", 38.737802,-9.303223, new Menu()));
        put("Red Bar", new FoodService("Red Bar", "BAR", "08:00", "22:00", 38.736546,-9.302207, new Menu()));
        put("Green Bar", new FoodService("Green Bar", "BAR", "07:00", "19:00", 38.738004,-9.303058, new Menu()));

        //CTN
        put("CTN Cafeteria", new FoodService("CTN Cafeteria", "RESTAURANT", "12:00", "14:00", 38.812522,-9.093773, new Menu()));
        put("CTN Bar", new FoodService("CTN Bar", "BAR", "08:30", "16:30", 38.812522,-9.093773, new Menu()));



    }};

    //FIXME:should be defined by the user
    private String username = "pedro";
    private String password = "123";


    public void getLocation2(Activity activity){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LOCATION", "no permission");
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(gpsOptionsIntent);
        }



        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 60000, 50, locationListener);
    }


    public ArrayList<FoodService> getCampusFoodServices(String campus) {
        listFoodServices = new ArrayList<FoodService>();
        if (campus == "Alameda") {
            listFoodServices.add(foodServices.get("Central Bar"));
            listFoodServices.add(foodServices.get("Civil Bar"));
            listFoodServices.add(foodServices.get("Civil Cafeteria"));
            listFoodServices.add(foodServices.get("Sena Pastry Shop"));
            listFoodServices.add(foodServices.get("Mechy Bar"));
            listFoodServices.add(foodServices.get("AEIST Bar"));
            listFoodServices.add(foodServices.get("AEIST Esplanade"));
            listFoodServices.add(foodServices.get("Chemy Bar"));
            listFoodServices.add(foodServices.get("SAS Cafeteria"));
            listFoodServices.add(foodServices.get("Math Cafeteria"));
            listFoodServices.add(foodServices.get("Complex Bar"));
        } else if (campus == "Taguspark") {
            listFoodServices.add(foodServices.get("Tagus Cafeteria"));
            listFoodServices.add(foodServices.get("Red Bar"));
            listFoodServices.add(foodServices.get("Green Bar"));
        } else if (campus == "CTN") {
            listFoodServices.add(foodServices.get("CTN Cafeteria"));
            listFoodServices.add(foodServices.get("CTN Bar"));
        }
        return listFoodServices;
    }

    public String getCampus() {
        return CAMPUS;
    }
    public void setCampus(String campus) {
        this.CAMPUS = campus;
        if (campus == "Alameda") {
            this.OTHERCAMPUS1 = "Taguspark";
            this.OTHERCAMPUS2 = "CTN";
        } else if (campus == "Taguspark"){
            this.OTHERCAMPUS1 = "CTN";
            this.OTHERCAMPUS2 = "Alameda";
        } else {
            this.OTHERCAMPUS1 = "Alameda";
            this.OTHERCAMPUS2 = "Taguspark";
        }
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;

    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public String getOtherCampus1() {
        return OTHERCAMPUS1;
    }

    public String getOtherCampus2() {
        return OTHERCAMPUS2;
    }

    public void setOtherCampus1(String OTHERCAMPUS) {
        this.OTHERCAMPUS1 = OTHERCAMPUS;
    }

    public void setOtherCampus2(String OTHERCAMPUS) {
        this.OTHERCAMPUS2 = OTHERCAMPUS;
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

    public double[] getCTNLatitude() {
        return CTNLatitude;
    }

    public double[] getCTNLongitude() {
        return CTNLongitude;
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

    public void setStatus(String status) {
        this.status = new AnnotationStatus(status);
        Log.i("STATUS",status);
    }

    public void addImageToCache(String key, AppImage image) {
        if (getImageFromCache(key) == null) {
            imageMemCache.put(key, image);
        }
    }

        public AppImage getImageFromCache(String key) {
            return imageMemCache.get(key);
        }

        public List<AppImage> getThumbnailsByFoodServiceDish(String foodService, String dish){
            List<AppImage> matches = new ArrayList<AppImage>();
            Map<String, AppImage> cachemap = imageMemCache.snapshot();
            //iterate through the map and find images from same foodService and Dish
            cachemap.forEach((k,v) -> {
                if(v.getFoodService().equals(foodService) && v.getDish().equals(dish) && v.isThumbnail())
                    matches.add(v);
            });
            return matches;
        }
        /*
        * Uses the max size of a Thumbnail possible:
        * JPEG with 500x500 size and bit depth of 48bit
        *
        * */
        public int getNrThumbnailsLeft(){
            return  ((CACHESIZE/(1024*1024)) - imageMemCache.size())/(int) 1.5;

        }

        public boolean isConnected(){
            return this.connected;
        }

        public void setConnected(boolean val){
            this.connected = val;
        }
        public void setContext(Context context){
            this.context = context;
        }
        public Context getContext(){
        return this.context;
    }
}
