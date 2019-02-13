package common;

public class Supplier extends Model {
    private String name;
    private Number distance;

    public Supplier(String name, int distance) {
        this.name = name;
        this.distance = distance;
    }

    // Getters

    @Override
    public String getName() {
        return this.name;
    }

    public Number getDistance() {
        return distance;
    }

    // Setters

    @Override
    public void setName(String name) {
        notifyUpdate("name",this.name,name);
        this.name = name;
    }

    public void setDistance(int distance) {
        notifyUpdate("name",this.distance,distance);
        this.distance = distance;
    }
}
