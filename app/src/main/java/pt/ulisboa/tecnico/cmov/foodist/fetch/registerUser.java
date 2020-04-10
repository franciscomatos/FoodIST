package pt.ulisboa.tecnico.cmov.foodist.fetch;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class registerUser extends fetchBase {
    private String foodService ;
    private String currentTime;

    public registerUser(GlobalClass global) {
        super(global, global.getURL() + "/queue");
    }

    @Override
    protected String buildBody() {
        return "{\"username\":\"" + getGlobal().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getPassword() + "\"," +
                "\"level\":\"" + "1" + "\"," +
                "\"minutes\":\"" +  "[\"Fish\",\"Meat\",\"Vegetarian\",\"Vegan\"]" +"\" }";
    }
}
