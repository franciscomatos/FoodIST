package pt.ulisboa.tecnico.cmov.foodist;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class fetchData extends AsyncTask<Void, Void, Void> {

    private ListFoodServicesView listFoodServices;

    //private String key = "AIzaSyAVuqTJjlLwltDajwlrHBwgqpm58hQFeQw"; OLD KEY
    private String key = "5b3ce3597851110001cf6248fb8cf9c19bae4bf08f17f4d3454d90ff";
    private String URL = "https://api.openrouteservice.org/v2/matrix/foot-hiking";
    private String data = "";
    private HashMap<String, String> params = new HashMap<String, String>();
    private StringBuilder sbParams;
    private String destinations;
    private GlobalClass global;
    private String whereToIndex;

    public fetchData(ListFoodServicesView list, GlobalClass global) {
        this.listFoodServices = list;
        this.global = global;

        /* OLD GOOGLE API
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
        url += global.getLatitude()+","+global.getLongitude();
        url += "&destinations=";
        ArrayList<FoodService> FoodServiceList = global.getCampusFoodServices(global.getCampus());
        for (int i = 0; i<FoodServiceList.size(); i++) {
           url += FoodServiceList.get(i).getLatitude()+",";
           url += FoodServiceList.get(i).getLongitude()+ "|";
        }
        url = url.substring(0, url.length()-1); //remove last "|"
        url += "&mode=walking";
        url += "&key=" + key;
        Log.i("URL", url);
        this.URL = url;*/

        //createURL();
        createDestinations();
        createParams();
    }

    private void createDestinations() {
        ArrayList<FoodService> FoodServiceList = global.getCampusFoodServices(global.getCampus());
        destinations = "[" + "["+global.getLongitude()+","+global.getLatitude()+"]";
        whereToIndex = "[";
        for (int i = 0; i<FoodServiceList.size(); i++) {
            whereToIndex += (i+1) + ",";
            destinations += ",";
            destinations += "[" + FoodServiceList.get(i).getLongitude()+",";
            destinations += FoodServiceList.get(i).getLatitude()+ "]";
        }
        whereToIndex = whereToIndex.substring(0, whereToIndex.length()-1) + "]";
        destinations += "]";
        Log.i("MYLOGS", destinations);
    }

    private void createParams() {
        params.put("locations","[]");
        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    @Override
    protected Void doInBackground(Void... voids){
        try{
            URL url  = new URL(this.URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Authorization", key);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            //httpURLConnection.setDoInput(true);


            String paramsString = sbParams.toString();

            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write("{\"locations\":" + destinations + "," +
                           "\"destinations\":" + whereToIndex +"," +
                            "\"metrics\": [\"duration\"]}");
            writer.flush();
            writer.close();

            httpURLConnection.connect();
            Log.i("RESPONSE", httpURLConnection.getResponseMessage());
            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while(line!=null) {
                line = bufferedReader.readLine();
                data = data+line;

            }
        } catch (MalformedURLException e) {
            Log.e("ERROR", ": couldnt connect1");
            e.printStackTrace();

        } catch (IOException e) {
            Log.e("ERROR", ": couldnt connect2");
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE", data);
        this.listFoodServices.setView(data);
    }

}
