package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pt.ulisboa.tecnico.cmov.foodist.activities.MenuActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class uploadImage extends fetchBase {

	private MenuActivity menuActivity;

	private String foodService ;
	private Menu menu;

	public uploadImage(MenuActivity activity, Menu m, String foodServiceName, GlobalClass global) {
		super(global, global.getURL() + "/getMenus");
		this.menu = m;
		this.foodService = foodServiceName;
		this.menuActivity =  activity;
	}

	@Override
	protected String buildBody() {
		return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"," +
				"\"canteen\":\""+foodService+"\"}";
	}

	@Override
	protected void parse(String data) {
		Log.i("FETCHMENU", getData());
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

			JSONArray dishes = response.getJSONArray("menus");

			for(int i = 0 ; i < dishes.length() ; i++) {

				JSONObject dish = dishes.getJSONObject(i);
				this.menu.addDish(
						new Dish(dish.getString("name"),
								dish.getDouble("price"),
								Dish.DishCategory.valueOf(dish.getString("dietary").toUpperCase()))
				);
			}
		} catch (JSONException e) {
			Log.e("ERROR", ": Failed to parse the json");
			e.printStackTrace();
		}catch (Exception e) {
			Log.e("ERROR", data);
			Log.e("ERROR", Dish.DishCategory.MEAT.getCategory());
			e.printStackTrace();
		}
	}

	protected void onPostExecute(Void aVoid) {
		//will update the activity
		Log.i("FETCHMENU", getData());
		menuActivity.updateDishes();
	}

}
