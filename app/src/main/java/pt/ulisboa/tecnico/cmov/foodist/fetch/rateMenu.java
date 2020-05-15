package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class rateMenu extends fetchBaseCustom {
    private String foodService ;
    private String dish;
    private Integer dishIndex;
    private Integer rate;

    public rateMenu(GlobalClass global, String foodService, String dish, Integer dishIndex, Integer rate) {
        super(global, global.getURL() + "/rateMenu");
        this.foodService = foodService;
        this.dish = dish;
        this.dishIndex = dishIndex;
        this.rate = rate;
    }

    @Override
    protected String buildBody() {
        return "{" + "\"namemenu\":\"" + dish + "\"," +
                    "\"namecanteen\":\"" + foodService + "\"," +
                    "\"username\":\"" + getGlobal().getUser().getUsername() + "\"," +
                    "\"password\":\"" + getGlobal().getUser().getPassword() + "\"," +
                    "\"rate\":\"" + rate + "\"" +
                "}";
    }

    @Override
    protected void parse(String data) {

        Log.i("FETCH SINGLE IMAGE", "parsing response ...");

        try {
            JSONObject response = new JSONObject(data);
            if (!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            getGlobal().addRating(foodService, dishIndex, rate);
            System.out.println("added rating");
        } catch (JSONException e) {
            Log.e("ERROR", ": Failed to parse the json");
            e.printStackTrace();
        }catch (Exception e) {
            Log.e("ERROR", data);
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE:", getData());
        Log.i("ACTION", "going to rate");

    }
}
