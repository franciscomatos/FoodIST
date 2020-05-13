package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class registerUser extends fetchBaseCustom {
    private String username ;
    private String password;
    private String email;
    private String ist;
    private String level;
    private boolean error = true;

    public registerUser(GlobalClass global, String username, String password, String email, String ist, String level) {

        super(global, global.getURL() + "/register");
        this.username = username;
        this.password = password;
        this.email = email;
        this.ist = ist;
        this.level = level;
    }
    @Override
    protected String buildBody() {

        return "{\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"ist\":\"" + ist + "\"," +
                "\"level\":\""+level+"\"}"; //check if this is the correct method
    }

    @Override
    public void parse(String data) {
        try {
            JSONObject response = new JSONObject(data);
            if(!response.getString("status").equals("OK"))
                throw new JSONException("Json wasn't ok");

            //REPLACE FOR new User
            //will only change if valid
            getGlobal().setUser(new User(username, email, ist, password, new AnnotationStatus(level)));
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
