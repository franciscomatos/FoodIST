package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.util.ArrayList;
import java.util.List;

public class User {

    public enum UserCourse
    {
        // This will call enum constructor with one
        // String argument
        MEIC("MEIC"), LEIC("LEIC");

        // declaring private variable for getting values
        private String course;

        // getter method
        public String getCourse()
        {
            return this.course;
        }

        // enum constructor - cannot be public or protected
        private UserCourse(String course)
        {
            this.course = course;
        }
    }

    private String name;
    private String istNumber;
    private UserCourse course;
    private List<Dish.DishCategory> dietaryConstraints = new ArrayList<>();

    public User(String name, String istNumber, UserCourse course) {
        this.name = name;
        this.istNumber = istNumber;
        this.course = course;
        this.dietaryConstraints.add(Dish.DishCategory.FISH);
        this.dietaryConstraints.add(Dish.DishCategory.MEAT);
        this.dietaryConstraints.add(Dish.DishCategory.VEGETARIAN);
        this.dietaryConstraints.add(Dish.DishCategory.VEGAN);
    }

    public List<Dish.DishCategory> getDietaryConstraints() {
        return this.dietaryConstraints;
    }

    public void addConstraint(Dish.DishCategory category) {
        this.dietaryConstraints.add(category);
    }

    public void removeConstraint(Dish.DishCategory category) {
        this.dietaryConstraints.remove(category);
    }

    public boolean containsConstraint(Dish.DishCategory category) {
        return this.dietaryConstraints.contains(category);
    }



}