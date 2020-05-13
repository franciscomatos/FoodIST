package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class rateMenu extends fetchBaseCustom {
    private String foodService ;
    private String dish;
    private String rate;

    public rateMenu(GlobalClass global, String foodService, String dish, String rate) {
        super(global, global.getURL() + "/register");
        this.foodService = foodService;
        this.dish = dish;
        this.rate = rate;
    }

    @Override
    protected String buildBody() {
        return "{" + "\"namemenu\":\"" + dish + "\"," +
                    "\"namecanteen\":\"" + foodService + "\"," +
                    "\"username\":\"" + getGlobal().getUsername() + "\"," +
                    "\"password\":\"" + getGlobal().getPassword() + "\"," +
                    "\"rate\":\"" + rate + "\"" +
                "}";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE:", getData());
        Log.i("ACTION", "going to rate");

    }
}
