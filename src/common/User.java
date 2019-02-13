package common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User extends Model {
    private String username;
    private String password;
    private String address;
    private Postcode postcode;
    private Map<Dish, Number> basket = new ConcurrentHashMap<>();

    public User(String username, String password, String address, Postcode postcode) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    // Getters

    @Override
    public String getName() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public Postcode getPostcode() {
        return postcode;
    }

    public Map<Dish, Number> getBasket() {
        return basket;
    }

    // Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostcode(Postcode postcode) {
        this.postcode = postcode;
    }

    public void setBasket(Map<Dish, Number> basket) {
        this.basket = basket;
    }
}
