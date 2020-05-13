package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import android.graphics.Bitmap;


public class uploadImage extends fetchBaseCustom {

	private AppImage image;

	public uploadImage(GlobalClass global, AppImage image) {
		super(global, global.getURL() + "/addImage");
		this.image = image;
	}
	@Override
	protected String buildBody() {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		image.getImage().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();

		String encoded = Base64.getEncoder().encodeToString(byteArray);

		return "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
				"\"namemenu\":\"" + image.getDish() +"\"," +
                "\"nameimage\":\"" + image.toString() +"\"," +
                "\"namecanteen\":\"" + image.getFoodService() +"\"," +
				"\"image\":\""+encoded+"\"}";
	}
	@Override
	protected void parse(String data) {
		Log.i("UPLOADIMAGE", "Image Name:"+ image.toString());
		Log.i("UPLOADIMAGE", getData());
		try {
			JSONObject response = new JSONObject(data);
			if(!response.getString("status").equals("OK"))
				throw new JSONException("Json wasn't ok");

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
		Log.i("UPLOADIMAGE", getData());
	}

}
