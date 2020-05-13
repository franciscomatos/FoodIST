package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;

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
    private String email;
    private String password;
    private String istNumber;
    private UserCourse course;
    private AnnotationStatus status;
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

    public User(String name, String email, String istNumber, String password, AnnotationStatus status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.istNumber = istNumber;
        this.status = status;
        this.dietaryConstraints.add(Dish.DishCategory.FISH);
        this.dietaryConstraints.add(Dish.DishCategory.MEAT);
        this.dietaryConstraints.add(Dish.DishCategory.VEGETARIAN);
        this.dietaryConstraints.add(Dish.DishCategory.VEGAN);
    }

    public String getUsername() { return this.name; }

    public String getEmail() { return this.email; }

    public String getIstNumber() { return this.istNumber; }

    public String getPassword() { return this.password; }

    public AnnotationStatus getStatus() {
        return this.status;
    }

    public List<Dish.DishCategory> getDietaryConstraints() {
        return this.dietaryConstraints;
    }

    public void addConstraint(Dish.DishCategory category) {
        this.dietaryConstraints.add(category);
    }

    public void removeConstraint(Dish.DishCategory category) {
        this.dietaryConstraints.remove(category);
        System.out.println("removed constraint.");
        System.out.println("user constraints:" + this.dietaryConstraints.size());
    }

    public boolean containsConstraint(Dish.DishCategory category) {
        return this.dietaryConstraints.contains(category);
    }



}
