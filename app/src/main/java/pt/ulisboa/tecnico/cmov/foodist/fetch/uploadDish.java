package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.foodist.activities.MenuActivity;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class uploadDish extends fetchBase {

	private MenuActivity menuActivity;

	private String foodService ;
	private String dietary ;
	private String dishName ;
	private Double price ;

	public uploadDish(Double price, String dishname, String dietary, String foodServiceName, GlobalClass global) {
			super(global, global.getURL() + "/addMenu");
			this.foodService = foodServiceName;
			this.dishName = dishname;
			this.dietary = dietary;
			this.price = price;
	}

	@Override
	protected String buildBody() {
		return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"," +
				"\"price\":\"" +price +"\"," +
				"\"namemenu\":\"" +dishName +"\"," +
				"\"dietary\":\"" +dietary +"\"," +
				"\"namecanteen\":\""+foodService+"\"}";
	}

	@Override
	protected void parse(String data) {
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");


		} catch (JSONException e) {
			Log.e("ERROR", ": Failed to parse the json");
			e.printStackTrace();
		}
	}

	protected void onPostExecute(Void aVoid) {
		//will update the activity
		Log.i("ADDDISH", getData());
	}

}
