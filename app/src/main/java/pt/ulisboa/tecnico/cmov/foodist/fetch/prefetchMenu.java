package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class prefetchMenu extends fetchBase {

	private String foodService;
	private String dish;

	public prefetchMenu(GlobalClass global, String foodService, String dish) {
		super(global, global.getURL() + "/prefetch");
		this.foodService = foodService;
		this.dish = dish;
	}

	protected String buildBody() {
		return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"," +
                "\"menu\":\"" + dish +"\"," +
				"\"canteen\":\""+foodService+"\"}";
	}

	@Override
	protected void parse(String data) {
		Log.i("PREFETCH", "parsing names ...");
		cached = getGlobal().getThumbnailsByFoodServiceDish(foodService,dish);
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

			JSONArray images = response.getJSONArray("images");

			for(int i = 0 ; i < images.length() ; i++) {

				JSONObject obj = images.getJSONObject(i);

				String img_name =  	obj.getString("name");

				byte[] decodedString = Base64.getDecoder().decode(obj.getString("image")) ;
				Bitmap img_dec = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				//HACK: this could have been avoided with a bit of thinking...
				String[] tmp = img_name.split("_");

				String timestamp = tmp[tmp.length-1];
				Long time =  Long.parseLong(timestamp);
				//it will only return thumbnails
				AppImage img = new AppImage(foodService, dish, new Date(time), getGlobal().getUsername(),img_dec, true);
				getGlobal().addImageToCache(img.toString(), img);

		} catch (JSONException e) {
			Log.e("ERROR", ": Failed to parse the json");
			e.printStackTrace();
		}catch (Exception e) {
			Log.e("ERROR", data);
			e.printStackTrace();
		}
	}

	protected void onPostExecute(Void aVoid) {
		Log.i("PREFETCH", "finished prefetching!");
		//nothing else to do here?
	}

}
