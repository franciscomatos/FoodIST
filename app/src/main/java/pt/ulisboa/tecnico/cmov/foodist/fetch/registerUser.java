package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class registerUser extends fetchBaseCustom {
    private String foodService ;
    private String currentTime;

    public registerUser(GlobalClass global) {
        super(global, global.getURL() + "/register");
    }

    @Override
    protected String buildBody() {
        return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getPassword() + "\"," +
                "\"level\":\"" + "1" + "\"," +
                "\"dietary\":[\"Fish\",\"Meat\",\"Vegetarian\",\"Vegan\"] }";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE:", getData());
        Log.i("ACTION", "going to login");
        login login = new login (getGlobal());
        login.execute();
    }
}
