package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Application;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;

public class GlobalClass extends Application {
    private String CAMPUS = "Select a campus";
    private String OTHERCAMPUS;
    private double LATITUDE;
    private double LONGITUDE;
    private FusedLocationProviderClient fusedLocationClient;

    private double[] AlamedaLatitude = new double[]{38.735740, 38.739740 };
    private double[] AlamedaLongitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLatitude = new double[]{38.735740, 38.739740 };
    private double[] TagusLongitude = new double[]{38.735740, 38.739740 };


    public ArrayList<FoodService> getCampusFoodServices(String campus) {
        ArrayList<FoodService> listFoodServices = new ArrayList<FoodService>();
        if (campus == "Alameda") {
            listFoodServices.add(new FoodService("CIVIL", "10:00", "20:00", 38.737069, -9.140017));
            listFoodServices.add(new FoodService("ABILIO", "10:00", "20:00", 38.737135, -9.137655));
            listFoodServices.add(new FoodService("AE", "10:00", "22:00", 38.736221, -9.137195));
        } else if (campus == "Taguspark") {
            listFoodServices.add(new FoodService("GreenBar Tagus", "10:00", "20:00", 38.738019, -9.303139 ));
            listFoodServices.add(new FoodService("Cafetaria", "10:00", "20:00", 38.736582,  -9.302166 ));
        }
        return listFoodServices;
    }

    public String getCampus() {
        return CAMPUS;
    }
    public void setCampus(String campus) {
        this.CAMPUS = campus;
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

    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }

    public void setFusedLocationClient(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient;
    }
}
