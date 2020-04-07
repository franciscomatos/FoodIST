package pt.ulisboa.tecnico.cmov.foodist;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.foodist.activities.ListFoodServicesActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class fetchMenu extends AsyncTask<Void, Void, Void> {

	private ListFoodServicesActivity listActivity;

		private String data = "";
		private GlobalClass global;
		private String URL ;
		private Map<String, FoodService> newFoodServices = new  HashMap<String, FoodService>();

	public fetchMenu(ListFoodServicesActivity list, GlobalClass global) {
			this.global = global;
			this.listActivity = list;
			this.URL =  global.getURL() + "/getCanteens";
	}

	@Override
	protected Void doInBackground(Void... voids){
		try{
			java.net.URL url  = new URL(this.URL);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.setDoOutput(true);
			//httpURLConnection.setDoInput(true);



			OutputStream out = httpURLConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.write("{\"username\":\"" + global.getUsername() + "\"," +
					"\"password\":\"" + global.getPassword() +"\"," +
					"\"campus\":\""+ global.getCampus()+"\"}");
			writer.flush();
			writer.close();

			httpURLConnection.connect();
			Log.i("RESPONSE2", httpURLConnection.getResponseMessage());
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

		//FIXME: currently does not parse current queue
		//parse the result and add it to the GlobalClass
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasnt ok");

			JSONArray foodServices = response.getJSONArray("canteens");
			for(int i=0;i<foodServices.length();i++){
				JSONObject canteen = foodServices.getJSONObject(i);
				JSONObject openhours = canteen.getJSONObject("openhours");
				JSONObject coords = canteen.getJSONObject("coords");

				newFoodServices.put(canteen.getString("name"),new FoodService(
											canteen.getString("name"),
											canteen.getString("type"),
											openhours.getString("open"),
											openhours.getString("close"),
											coords.getDouble("lat"),
											coords.getDouble("lng"),
											new Menu()
									));
			}

		} catch (JSONException e) {
			Log.e("ERROR", ": Failed to parse the json");
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Void aVoid) {
		Log.i("FETCHMENU", this.data);
	}

}
