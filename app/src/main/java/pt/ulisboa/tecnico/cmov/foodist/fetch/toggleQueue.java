package pt.ulisboa.tecnico.cmov.foodist.fetch;

import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class toggleQueue extends fetchBase {

    private String foodService ;
    private String currentTime;

    public toggleQueue(GlobalClass global,  String foodServiceName, String currentTime) {
        super(global, global.getURL() + "/queue");
        this.foodService = foodServiceName;
        this.currentTime = currentTime;
    }

    @Override
    protected String buildBody() {
        return "{\"canteen\":\"" + foodService + "\"," +
                "\"username\":\"" + getGlobal().getUsername() + "\"," +
                "\"password\":\"" + getGlobal().getPassword() + "\"," +
                "\"minutes\":\"" + currentTime +"\" }";
    }
}
