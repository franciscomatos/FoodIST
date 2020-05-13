package pt.ulisboa.tecnico.cmov.foodist.fetch;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class login extends fetchBaseCustom {
    private String username ;
    private String password;
    private boolean error = true;

    public login(GlobalClass global, String username, String password) {

        super(global, global.getURL() + "/login");
        this.username = username;
        this.password = password;
    }

    @Override
    protected String buildBody() {
        return "{\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"}";
    }

    @Override
    public void parse(String data) {
        try {
            JSONObject response = new JSONObject(data);
            if(!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            //will only change if valid
            getGlobal().getUser().setUsername(username);
            getGlobal().getUser().setPassword(password);
            error = false;

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

        if(getGlobal().isConnected() && !error){
            Log.i("ACTION", "going to prefetch");
            prefetch process = new prefetch(getGlobal());
            process.execute();
        }

    }
}
