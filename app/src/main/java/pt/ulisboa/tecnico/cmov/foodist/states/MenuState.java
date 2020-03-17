package pt.ulisboa.tecnico.cmov.foodist.states;

import android.app.Application;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;

public class MenuState extends Application {

    private Menu menu = new Menu();

    /* getters */

    public List<Dish> getDishList() {
        return this.menu.getDishList();
    }

    public Dish getDish(int index) {
        return this.menu.getDish(index);
    }

    public int getCounter() {
        return this.menu.getCounter();
    }

    /* setters */

    public void addDish(Dish dish) {
        this.menu.addDish(dish);
    }
}
