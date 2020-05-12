package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu {

    private List<Dish> dishList = new ArrayList<>();
    private List<Dish> constraintDishList = new ArrayList<>();
    private List<Dish.DishCategory> constraints = new ArrayList<>();
    private Map<Integer, Integer> overallRatings = new HashMap<>();


    public Menu() {
        this.constraints.add(Dish.DishCategory.FISH);
        this.constraints.add(Dish.DishCategory.MEAT);
        this.constraints.add(Dish.DishCategory.VEGETARIAN);
        this.constraints.add(Dish.DishCategory.VEGAN);

        // ratings initialization
        for(int i = 1; i <= 5; i++)
            this.overallRatings.put(i,0);
    }

    public Menu(List<Dish.DishCategory> constraints) {
        this.constraints = constraints;
        // ratings initialization
        for(int i = 1; i <= 5; i++)
            this.overallRatings.put(i,0);
    }
    /* getters */

    public List<Dish> getDishList() {
        return this.dishList;
    }
    public void clear(){ //FIXME: dunno how to deal with the constraints
        dishList.clear();
        constraintDishList.clear();
        //constraints.clear();

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

    public Map<Integer, Integer> getRatings() {
        return this.overallRatings;
    }

    /* setters */

    public void addDish(Dish dish) {
        this.dishList.add(dish);
        updateConstraintDishes();
    }

    public List<Dish.DishCategory> getConstraints() {
        return this.constraints;
    }

    public void addConstraint(Dish.DishCategory category) {
        this.constraints.add(category);
    }

    public void removeConstraint(Dish.DishCategory category) {
        this.constraints.remove(category);
    }

    public void updateConstraints(List<Dish.DishCategory> categories) { this.constraints = categories; }

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

    /* others */
    public void addRating(Integer classification) {
        Integer current = this.overallRatings.get(classification);
        this.overallRatings.put(classification, current+1);
    }

    public Double computeRatingAverage() {
        Double total = 0.0;
        Integer counter = 0;
        for(Map.Entry<Integer, Integer> classification: overallRatings.entrySet()) {
            total += (classification.getValue() * classification.getKey());
            counter += classification.getValue();
        }
        if(total == 0.0 && counter == 0) return 0.0;
        return total / counter;
    }

    public Integer computeNumberOfRatings() {
        Integer counter = 0;
        for(Map.Entry<Integer, Integer> classification: overallRatings.entrySet()) {
            counter += classification.getValue();
        }
        return counter;
    }
}
