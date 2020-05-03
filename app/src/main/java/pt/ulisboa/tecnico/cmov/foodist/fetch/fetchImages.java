package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class fetchImages extends fetchBase {

	private String foodService;
	private String dish;
	private int page;
	private CarouselView carouselView;
	//change this of place, where there is no such array matches
	private ImageListener imageListener;
	private List<String> misses ;
	private List<AppImage> hits ;

    public fetchImages(GlobalClass global, CarouselView carouselView, String foodService, String dish, List<AppImage> hits, List<String> misses) {
		super(global, global.getURL() + "/getImages");
		this.foodService = foodService;
		this.page = page;
		this.dish = dish;
		this.carouselView = carouselView;
		this.misses = misses;
		this.hits = hits;
	}
	@Override
	protected String buildBody() {
		String img = String.join("\",\"",misses);

        return  "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"," +
				"\"canteen\":\""+foodService+"\"," +
				"\"menu\":\"" + dish +"\"," +
				"\"images\":[\""+img+"\"]}" ;
	}
	@Override
	protected void parse(String data) {

		Log.i("FETCHIMAGES", "parsing response ...");

		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

			JSONArray images = response.getJSONArray("images");

			//add images to cache and display them?

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
				hits.add(img);
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
		//will update the activity
		Log.i("FETCHIMAGES", "completing carousel!");

		carouselView.setImageListener(new ImageListener() {
				@Override
				public void setImageForPosition(int position, ImageView imageView) {
					imageView.setImageBitmap(hits.get(position).getImage());
				}
			}
		);
		carouselView.setPageCount(hits.size());

		Log.i("FETCHIMAGES", "finished fetching");
	}

}
