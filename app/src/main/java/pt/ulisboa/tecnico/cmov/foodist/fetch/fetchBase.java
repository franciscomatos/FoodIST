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


public class fetchBase extends AsyncTask<Void, Void, Void> {

    private String URL;
    private String data = "";
    private HashMap<String, String> params = new HashMap<String, String>();
    private StringBuilder sbParams;
    private String destinations;
    private GlobalClass global;
    private String whereToIndex;

    public fetchBase(GlobalClass global, String URL) {
        this.global = global;
        this.URL = URL;
    }

    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected String buildBody() {
        return null;
    }
    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected void requestProperties(HttpURLConnection conn) {
        return;
    }
    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected void parse(String data) {
        return;
    }

    @Override
    protected Void doInBackground(Void... voids){
        try{
            URL url  = new URL(this.URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setRequestMethod("POST");
            //httpURLConnection.setRequestProperty("Authorization", key);
            requestProperties(httpURLConnection);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);

            String paramsString = sbParams.toString();

            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(buildBody());
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

        parse(data);
        return null;
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }


    public GlobalClass getGlobal(){
        return global;
    }

    public String getData() {
        return data;
    }
}
