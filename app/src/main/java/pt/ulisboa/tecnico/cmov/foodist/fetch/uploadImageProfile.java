package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;


public class uploadImageProfile extends fetchBaseCustom {

	public uploadImageProfile(GlobalClass global) {
		super(global, global.getURL() + "/addImageProfile");
	}
	@Override
	protected String buildBody() {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		getGlobal().getUser().getImage().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();

		String encoded = Base64.getEncoder().encodeToString(byteArray);

		return "{\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
				"\"password\":\"" + getGlobal().getUser().getPassword() +"\"," +
				"\"image\":\""+encoded+"\"}";
	}
	@Override
	protected void parse(String data) {
		Log.i("UPLOADIMAGEPROFILE", getData());
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
		Log.i("UPLOADIMAGEPROFILE", getData());
	}

}
