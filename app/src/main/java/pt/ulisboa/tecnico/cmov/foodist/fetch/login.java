package pt.ulisboa.tecnico.cmov.foodist.fetch;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import android.util.Log;

public class login extends fetchBaseCustom {
    private String foodService ;
    private String currentTime;

    public login(GlobalClass global) {
        super(global, global.getURL() + "/login");
    }

    @Override
    protected String buildBody() {
        return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getPassword() + "\"}";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(getGlobal().isConnected()){
            Log.i("ACTION", "going to prefetch");
            prefetch process = new prefetch(getGlobal());
            process.execute();
        }

    }
}
