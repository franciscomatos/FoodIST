package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<Dish> dishList = new ArrayList<>();
    private List<Dish> constraintDishList = new ArrayList<>();
    private List<Dish.DishCategory> constraints = new ArrayList<>();

    public Menu() {
        this.constraints.add(Dish.DishCategory.FISH);
        this.constraints.add(Dish.DishCategory.MEAT);
        this.constraints.add(Dish.DishCategory.VEGETARIAN);
        this.constraints.add(Dish.DishCategory.VEGAN);
    }
    /* getters */

    public List<Dish> getDishList() {
        return this.dishList;
    }

    public List<Dish> getConstraintDishList() { return this.constraintDishList; }

    public Dish getDish(int index) {
        return this.dishList.get(index);
    }

    public Dish getConstraintDish(int index) { return this.constraintDishList.get(index); }

    public int getCounter() {
        return this.dishList.size();
    }

    public int getConstrainedCounter() { return this.constraintDishList.size(); }

    /* setters */

    public void addDish(Dish dish) {
        this.dishList.add(dish);
        updateConstraintDishes();
    }

    public void addConstraint(Dish.DishCategory category) {
        this.constraints.add(category);
    }

    public void removeConstraint(Dish.DishCategory category) {
        this.constraints.remove(category);
    }


    public void updateConstraintDishes() {
        constraintDishList.clear();
        for(Dish d: dishList) {
            if(this.constraints.contains(d.getCategory())){
                this.constraintDishList.add(d);
            }
        }
    }

    public boolean containsConstraint(Dish.DishCategory category) {
        return this.constraints.contains(category);
    }
}
