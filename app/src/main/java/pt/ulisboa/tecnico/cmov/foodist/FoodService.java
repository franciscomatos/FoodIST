package pt.ulisboa.tecnico.cmov.foodist;

import android.util.Log;

import java.time.LocalTime;

public class FoodService {

    private String name;
    private String openingHour;
    private String closingHour;
    private String ETA;

    public String getName() {
        return name;
    }

    public FoodService(String name, String openingHour, String closingHour, String ETA) {
        this.name = name;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.ETA = ETA; //just to test
      /*  Log.i("MyLOG: ", name + openingHour + closingHour);
        fetchData process = new fetchData();
        process.execute();*/
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


    public String getETA() {
        return ETA;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

}
