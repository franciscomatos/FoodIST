package pt.ulisboa.tecnico.cmov.foodist;

public class Campus {
    private String name;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public Campus(String name, double minLatitude, double maxLatitude,
                  double minLongitude, double maxLongitude ) {
        this.name = name;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }

    public String getName() {
        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

}
