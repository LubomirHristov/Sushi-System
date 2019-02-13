package common;

public class Postcode extends Model {
    private String name;
    private Number distance;

    public Postcode(String name, int distance) {
        this.name = name;
        this.distance = distance;
    }

    // Getters

    @Override
    public String getName() {
        return name;
    }

    public void setCode(String name) {
        this.name = name;
    }

    // Setters

    public Number getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
