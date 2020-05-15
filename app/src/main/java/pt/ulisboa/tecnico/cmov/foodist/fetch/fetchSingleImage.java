package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Date;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class fetchSingleImage extends fetchBaseCustom {
    private String foodService;
    private String dish;
    private String imageName;
    private ImageView view;
    private AppImage image;


    public fetchSingleImage(GlobalClass global, String foodService, String dish,
                            String imageName, ImageView view) {
        super(global, global.getURL() + "/getImages");
        this.foodService = foodService;
        this.dish = dish;
        this.imageName = imageName;
        this.view = view;
    }

    @Override
    protected String buildBody() {
        return  "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
                "\"canteen\":\""+foodService+"\"," +
                "\"menu\":\"" + dish +"\"," +
                "\"images\":[\""+imageName+"\"]}" ;
    }

    @Override
    protected void parse(String data) {

        Log.i("FETCH SINGLE IMAGE", "parsing response ...");

        try {
            JSONObject response = new JSONObject(data);
            if(!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            JSONArray images = response.getJSONArray("images");

            //add images to cache
            JSONObject obj = images.getJSONObject(0);
            String img_name = obj.getString("name");
            byte[] decodedString = Base64.getDecoder().decode(obj.getString("image")) ;
            Bitmap img_dec = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            String[] tmp = img_name.split("_");
            String timestamp = tmp[tmp.length-1];
            Long time =  Long.parseLong(timestamp);
            image = new AppImage(foodService, dish, new Date(time),
                    getGlobal().getUser().getUsername(),img_dec, false);

            getGlobal().addImageToCache(image.toString(), image);


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
        Log.i("FETCH SINGLE IMAGE", "completing carousel!");

        view.setImageBitmap(image.getImage());

        Log.i("FETCH SINGLE IMAGE", "finished fetching");
    }

}
