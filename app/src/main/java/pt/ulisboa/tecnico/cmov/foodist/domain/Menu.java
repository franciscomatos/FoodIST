package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<Dish> dishList = new ArrayList<>();

    /* getters */

    public List<Dish> getDishList() {
        return this.dishList;
    }

    public Dish getDish(int index) {
        return this.dishList.get(index);
    }

    public int getCounter() {
        return this.dishList.size();
    }

    /* setters */

    public void addDish(Dish dish) {
        this.dishList.add(dish);
    }
}
