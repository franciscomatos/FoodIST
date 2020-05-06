package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Base64;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class prefetch extends fetchBase {

	public prefetch(GlobalClass global) {
		super(global, global.getURL() + "/prefetch");
	}

	protected String buildBody() {
		//find total nr of images it needs
		return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"nrimages\":\""+getGlobal().getNrThumbnailsLeft()+"\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"}" ;
	}

	@Override
	protected void parse(String data) {

		Log.i("PREFETCH", "parsing response ...");

		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

			JSONArray images = response.getJSONArray("images");

			for(int i = 0 ; i < images.length() ; i++) {

				JSONObject obj = images.getJSONObject(i);

				JSONObject imgJson =  	obj.getJSONObject("image");
                String img_name = imgJson.getString("name");
                String foodService =  	obj.getString("canteen");
                String dish =  	obj.getString("menu");

				byte[] decodedString = Base64.getDecoder().decode(imgJson.getString("image")) ;
				Bitmap img_dec = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

				//HACK: this could have been avoided with a bit of thinking...
				String[] tmp = img_name.split("_");

				String timestamp = tmp[tmp.length-1];
				Long time =  Long.parseLong(timestamp);
				//it will only return thumbnails
				AppImage img = new AppImage(foodService, dish, new Date(time), getGlobal().getUsername(),img_dec, true);
				getGlobal().addImageToCache(img.toString(), img);
			}

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
