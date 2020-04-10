package pt.ulisboa.tecnico.cmov.foodist.fetch;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class login extends fetchBase {
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
}
