package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CarouselViewPager;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class fetchCacheImages extends fetchBaseCustom {

	private String foodService;
	private String dish;
	private int page;
	private CarouselView carouselView;
	//change this of place, where there is no such array matches
	private ImageListener imageListener;
	private List<AppImage> cached;
	private List<AppImage> hits;
	private List<String> misses;
	private boolean zoomOut = false;

	public fetchCacheImages(GlobalClass global, CarouselView carouselView, String foodService, String dish, int page) {
		super(global, global.getURL() + "/checkImageNames");
		this.foodService = foodService;
		this.page = page;
		this.dish = dish;
		this.carouselView = carouselView;
		this.hits = new ArrayList<AppImage>();
		this.misses = new ArrayList<String>();
	}

	protected String buildBody() {
		return "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
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

		});


		carouselView.setPageCount(hits.size());

//		carouselView.setImageClickListener(new ImageClickListener() {
//			@Override
//			public void onClick(int position) {
//				// image in position i corresponds to hits in position i
//				String fullImageName = "F"+hits.get(position).toString().substring(1);
//				// check if full image is in cache
//				AppImage fullImage = getGlobal().getImageFromCache(fullImageName);
//
//				CarouselViewPager viewPager = carouselView.getContainerViewPager();
//				ImageView currentImage = (ImageView) viewPager.getChildAt(position);
//
//				if(fullImage == null) {
//					// if not fetch full image from server
//					fetchSingleImage process = new fetchSingleImage(getGlobal(),
//							foodService, dish, fullImageName, currentImage, false);
//
//					try {
//						process.execute().get();
//					} catch (ExecutionException e) {
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//					// now that image is in cache we can use it
//					fullImage = getGlobal().getImageFromCache(fullImageName);
//
//
//					if(zoomOut) {
//						Toast.makeText(getGlobal(), "NORMAL SIZE!", Toast.LENGTH_LONG).show();
//						currentImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//						currentImage.setAdjustViewBounds(true);
//						zoomOut =false;
//					}else{
//						Toast.makeText(getGlobal(), "FULLSCREEN!", Toast.LENGTH_LONG).show();
//						currentImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//						currentImage.setScaleType(ImageView.ScaleType.FIT_XY);
//						zoomOut = true;
//					}
//
//				}
//			}
//		});

		if(!misses.isEmpty()) {
			Log.i("FETCHCACHEIMAGES", "there are misses, accessing server again");
			fetchImages process = new fetchImages(getGlobal(), carouselView, foodService, dish, hits, misses);
			process.execute();
		}
		Log.i("FETCHCACHEIMAGES", "finished cache access");
	}

}
