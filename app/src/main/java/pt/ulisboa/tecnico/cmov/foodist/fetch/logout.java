package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class logout extends fetchBaseCustom {
    private String username ;
    private String password;
    private boolean error = true;

    public logout(GlobalClass global, String username, String password) {

        super(global, global.getURL() + "/logout");
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
            getGlobal().setUser(null);
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
            Log.i("ACTION", "going to logout");
        }

    }
}
