package pt.ulisboa.tecnico.cmov.foodist.domain;

import java.text.DecimalFormat;

import static java.lang.Math.round;
import java.util.HashMap;
import java.util.Map;

public class Dish {

    public enum DishCategory
    {
        // This will call enum constructor with one
        // String argument
        FISH("Fish"), MEAT("Meat"), VEGETARIAN("Vegetarian"), VEGAN("Vegan");

        // declaring private variable for getting values
        private String category;

        // getter method
        public String getCategory()
        {
            return this.category;
        }

        // enum constructor - cannot be public or protected
        private DishCategory(String category)
        {
            this.category = category;
        }
    }

    public static final int SCALE = 1000;

    private String name;
    private Double price;
    private DishCategory category;
    private Map<Integer, Integer> ratings = new HashMap<>();

    public Dish(String name, Double price, DishCategory category) {
        this.name = name;
        this.price = price;
        this.category = category;

        // ratings initialization
        for(int i = 1; i <= 5; i++)
            this.ratings.put(i,0);
    }

    /* getters */

    public String getName() {
        return this.name;
    }

    public Double getPrice() {
        return this.price;
    }

    public long getPriceLong() {
        return round(getPrice() * SCALE);
    }

    public DishCategory getCategory() { return this.category; }
    public String getCategoryString() {
        return this.category.getCategory();
    }

    public Map<Integer, Integer> getRatings() {
        return this.ratings;
    }

    /* setters*/

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        String printPrice =  new DecimalFormat("#.##").format(price);
        return  name + " - " +
                category + " - " +
                printPrice + "€\n";
    }
    /* others */
    public void addRating(Integer classification) {
        Integer current = this.ratings.get(classification);
        this.ratings.put(classification, current+1);
    }

    public Double computeRatingAverage() {
        Double total = 0.0;
        Integer counter = 0;
        for(Map.Entry<Integer, Integer> classification: ratings.entrySet()) {
            total += (classification.getValue() * classification.getKey());
            counter += classification.getValue();
        }
        if(total == 0.0 && counter == 0) return 0.0;
        return total / counter;
    }

    public Integer computeNumberOfRatings() {
        Integer counter = 0;
        for(Map.Entry<Integer, Integer> classification: ratings.entrySet()) {
            counter += classification.getValue();
        }
        return counter;
    }

}
