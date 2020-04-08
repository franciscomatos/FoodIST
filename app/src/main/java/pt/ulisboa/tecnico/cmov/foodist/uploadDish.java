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

import pt.ulisboa.tecnico.cmov.foodist.activities.MenuActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class uploadDish extends AsyncTask<Void, Void, Void> {

	private MenuActivity menuActivity;

		private String data = "";
		private GlobalClass global;
		private String URL ;
		private String foodService ;
		private String dietary ;
		private String dishName ;
		private Double price ;

	public uploadDish(Double price, String dishname, String dietary, String foodServiceName, GlobalClass global) {
			this.global = global;
			this.URL =  global.getURL() + "/addMenu";
			this.foodService = foodServiceName;
			this.dishName = dishname;
			this.dietary = dietary;
			this.price = price;
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
					"\"price\":\"" +price +"\"," +
					"\"namemenu\":\"" +dishName +"\"," +
					"\"dietary\":\"" +dietary +"\"," +
					"\"namecanteen\":\""+foodService+"\"}");
			writer.flush();
			writer.close();

			httpURLConnection.connect();
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
		Log.i("FETCHMENU", this.data);
		//parse the result and add it to the GlobalClass
		//TODO: parse rating
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");


		} catch (JSONException e) {
			Log.e("ERROR", ": Failed to parse the json");
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Void aVoid) {
		//will update the activity
		Log.i("ADDDISH", this.data);
	}

}
