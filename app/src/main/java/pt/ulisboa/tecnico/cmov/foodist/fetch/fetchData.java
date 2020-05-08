package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import java.net.HttpURLConnection;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.activities.ListFoodServicesActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class fetchData extends fetchBase {

    private ListFoodServicesActivity listFoodServices;

    //private String key = "AIzaSyAVuqTJjlLwltDajwlrHBwgqpm58hQFeQw"; OLD KEY
    private String key = "5b3ce3597851110001cf6248fb8cf9c19bae4bf08f17f4d3454d90ff";
    private String destinations;
    private String whereToIndex;

    public fetchData(ListFoodServicesActivity list, GlobalClass global) {
        super(global, "https://api.openrouteservice.org/v2/matrix/foot-hiking");
        this.listFoodServices = list;
        createDestinations();
    }

    private void createDestinations() {
        ArrayList<FoodService> FoodServiceList = getGlobal().getCampusFoodServices( getGlobal().getCampus());
        destinations = "[" + "["+ getGlobal().getLongitude()+","+ getGlobal().getLatitude()+"]";
        whereToIndex = "[";
        for (int i = 0; i<FoodServiceList.size(); i++) {
            whereToIndex += (i+1) + ",";
            destinations += ",";
            destinations += "[" + FoodServiceList.get(i).getLongitude()+",";
            destinations += FoodServiceList.get(i).getLatitude()+ "]";
        }
        whereToIndex = whereToIndex.substring(0, whereToIndex.length()-1) + "]";
        destinations += "]";
    }

    @Override
    protected String buildBody() {
        return  "{\"locations\":" + destinations + "," +
                "\"destinations\":" + whereToIndex +"," +
                "\"metrics\": [\"duration\"]}";
    }

    @Override
    protected void requestProperties(HttpURLConnection conn) {
        conn.setRequestProperty("Authorization", key);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE",  getData());
        this.listFoodServices.setViewPostFetch(getData());
    }
}
