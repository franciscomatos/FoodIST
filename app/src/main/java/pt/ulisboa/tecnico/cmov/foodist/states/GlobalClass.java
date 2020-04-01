package pt.ulisboa.tecnico.cmov.foodist.states;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;

public class GlobalClass extends Application {
    private String CAMPUS = "Select a campus";
    private String OTHERCAMPUS;
    private double LATITUDE;
    private double LONGITUDE;
    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
   // private FusedLocationProviderClient fusedLocationClient;

    private double[] AlamedaLatitude = new double[]{38.735740, 38.739740 };
    private double[] AlamedaLongitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLatitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLongitude = new double[]{38.735740, 38.739740 };
    private ArrayList<FoodService> listFoodServices;
    private Map<String, FoodService> foodServices = new HashMap<String, FoodService>(){{
        put("CIVIL", new FoodService("CIVIL", "RESTAURANT", "10:00", "20:00", 38.737069, -9.140017, new Menu()));
        put("ABILIO", new FoodService("ABILIO","BAR", "10:00", "20:00", 38.737135, -9.137655, new Menu()));
        put("AE", new FoodService("AE","RESTAURANT", "10:00", "22:00", 38.736221, -9.137195, new Menu()));
        put("GreenBar Tagus",new FoodService("GreenBar Tagus","BAR", "10:00", "20:00", 38.738019, -9.303139, new Menu() ));
        put("Cafetaria", new FoodService("Cafetaria","RESTAURANT", "10:00", "20:00", 38.736582,  -9.302166, new Menu() ));
    }};


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
}
