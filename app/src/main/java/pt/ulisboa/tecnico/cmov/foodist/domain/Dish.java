package pt.ulisboa.tecnico.cmov.foodist.domain;

public class Dish {

    public static final int SCALE = 1000;

    private String name;
    private Double price;
    private String category;

    public Dish(String name, Double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    /* getters */

    public String getName() {
        return this.name;
    }

    public Double getPrice() {
        return this.price;
    }

    public long getPriceLong() {
        return Math.round(getPrice() * SCALE);
    }

    public String getCategory() {
        return this.category;
    }

    /* setters*/

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
