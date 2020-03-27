package pt.ulisboa.tecnico.cmov.foodist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.activities.FoodServiceActivity;

public class fetchMap extends AsyncTask<Void, Void, Void> {

    private String key = "api_key=5b3ce3597851110001cf6248fb8cf9c19bae4bf08f17f4d3454d90ff";
    private String URL = "https://api.openrouteservice.org/v2/directions/foot-hiking?";
    private String response = "";
    private FoodServiceActivity foodServiceActivity;

    public fetchMap(FoodServiceActivity foodServiceActivity, ArrayList<String> startEnd) {
        buildURL(startEnd);
        this.foodServiceActivity = foodServiceActivity;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try{
            URL url  = new URL(this.URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.connect();
            Log.i("RESPONSE", httpURLConnection.getResponseMessage());
            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while(line!=null) {
                line = bufferedReader.readLine();
                response = response+line;

            }
            response  = response.substring(0, response.length()-4);
            Log.i("MYLOGS", response);
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
        foodServiceActivity.setRoute(response);
    }


    private void buildURL(ArrayList<String> startEnd) {
        URL += key;
        URL += "&start=" + startEnd.get(0) + "," + startEnd.get(1);
        URL += "&end=" + startEnd.get(2) + "," + startEnd.get(3);
    }
}
