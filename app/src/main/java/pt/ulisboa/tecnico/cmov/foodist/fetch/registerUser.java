package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONObject;

import org.json.JSONException;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class registerUser extends fetchBaseCustom {
    private String username ;
    private String password;
    private boolean error = true;

    public registerUser(GlobalClass global, String username, String password) {

        super(global, global.getURL() + "/register");
        this.username = username;
        this.password = password;
    }
    @Override
    protected String buildBody() {
        return "{\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"level\":\"" + "1" + "\"," + //FIXME: change this to the user level ?
                "\"dietary\":[\"Fish\",\"Meat\",\"Vegetarian\",\"Vegan\"] }"; //FIXME: change this to the enum
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
        Log.i("RESPONSE:", getData());
        if(!error ){ //if the register worked
            Log.i("ACTION", "going to login");
            login login = new login (getGlobal(),username,password);
            login.execute();
        }
    }
}
