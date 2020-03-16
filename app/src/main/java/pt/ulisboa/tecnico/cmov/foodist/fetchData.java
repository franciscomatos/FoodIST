package pt.ulisboa.tecnico.cmov.foodist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class fetchData extends AsyncTask<Void, Void, Void> {

    private ListFoodServicesView listFoodServices;

    //private String key = "AIzaSyBIH4tIRgLyZYG6xLpnUOQHA6BL6No2CHY"; OLD KEY
    private String key = "AIzaSyAVuqTJjlLwltDajwlrHBwgqpm58hQFeQw";
    private String URL;
    private String data = "";

    public fetchData(ListFoodServicesView list, GlobalClass global) {
        this.listFoodServices = list;
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
        this.URL = url;
    }

    @Override
    protected Void doInBackground(Void... voids){
        try{
            String URLFull = this.URL;
            Log.i("TEST", this.URL);
            URL url  = new URL(this.URL);

            URLConnection httpURLConnection = url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE", data);
        this.listFoodServices.setView(data);
    }

    //Convert JSON-like string to view text
    public String digestResponse(String response) {
        //TODO
        return null;
    }


}
