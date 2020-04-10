package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.foodist.activities.ListFoodServicesActivity;
import pt.ulisboa.tecnico.cmov.foodist.activities.MenuActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
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
        Log.i("Location",  getGlobal().getLongitude()+","+ getGlobal().getLatitude());

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
        Log.i("Location", destinations);
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
