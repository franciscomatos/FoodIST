package pt.ulisboa.tecnico.cmov.foodist.domain;

import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class FoodService {

    private String name;
    private String openingHour;
    private String closingHour;
    private double latitude;
    private double longitude;
    private String type;
    private String campus;
    private Menu menu;
    private JSONObject map;
    public String getName() {
        return name;
    }

    public FoodService(String name,String campus, String type, String openingHour, String closingHour , double latitude, double longitude, Menu menu) {
        this.name = name;
        this.type = type;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.latitude = latitude;
        this.longitude = longitude;
        this.menu = menu;
        this.campus = campus;
    }
    public String getCampus(){
        return this.campus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public String getClosingHour() {
        return closingHour;
    }

    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public JSONObject getMap() {
        return map;
    }

    public void setMap(JSONObject map) {
        this.map = map;
    }

    @Override
    public String toString() {

        return  name + "\n" +
                openingHour + " - " + closingHour + "\n" +
                menu.toString();
    }
}
