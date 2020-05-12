package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class fetchCacheImagesCustom extends fetchBaseCustom {

	private String foodService;
	private String dish;
	private int page;
	private CarouselView carouselView;
	//change this of place, where there is no such array matches
	private ImageListener imageListener;
	private List<AppImage> cached;
	private List<AppImage> hits;
	private List<String> misses;

	public fetchCacheImagesCustom(GlobalClass global, CarouselView carouselView, String foodService, String dish, int page) {
		super(global, global.getURL() + "/checkImageNames");
		this.foodService = foodService;
		this.page = page;
		this.dish = dish;
		this.carouselView = carouselView;
		this.hits = new ArrayList<AppImage>();
		this.misses = new ArrayList<String>();
	}

	protected String buildBody() {
		return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getPassword() +"\"," +
                "\"page\":\"" + page +"\"," +
                "\"menu\":\"" + dish +"\"," +
				"\"canteen\":\""+foodService+"\"}";
	}

	protected boolean containsName(final List<AppImage> list, final String name){
		return list.stream().filter(o -> o.toString().equals(name)).findFirst().isPresent();
	}

	@Override
	protected void parse(String data) {
		Log.i("FETCHCACHEIMAGES", "parsing names ...");
		cached = getGlobal().getThumbnailsByFoodServiceDish(foodService,dish);
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

			JSONArray images = response.getJSONArray("images");

			for(int i = 0 ; i < images.length() ; i++) {
				String name = images.getString(i);

				if(containsName(cached,name)){ //hit
					hits.add(getGlobal().getImageFromCache(name));
				}else{//miss
					misses.add(name);
				}
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
		Log.i("FETCHCACHEIMAGES", "writing carousel!");
		//write what already has in cache for when the connection is slow,
		//this way there is at least something to show
		carouselView.setImageListener(new ImageListener() {
				@Override
				public void setImageForPosition(int position, ImageView imageView) {
					imageView.setImageBitmap(hits.get(position).getImage());
				}
			}
		);
		carouselView.setPageCount(hits.size());

		if(!misses.isEmpty()) {
			Log.i("FETCHCACHEIMAGES", "there are misses, accessing server again");
			fetchImagesCustom process = new fetchImagesCustom(getGlobal(), carouselView, foodService, dish, hits, misses);
			process.execute();
		}
		Log.i("FETCHCACHEIMAGES", "finished cache access");
	}

}
