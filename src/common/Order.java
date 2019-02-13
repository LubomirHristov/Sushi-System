package common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Order extends Model {
    private String name;
    private String status;
    private Number cost;
    private Map<Dish, Number> orderBasket = new ConcurrentHashMap<>();

    public Order(String name, Map<Dish, Number> orderBasket) {
        this.name=name;
        this.orderBasket = orderBasket;
    }

    // Getters

    @Override
    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return status;
    }

    public Number getCost() {
        return cost;
    }

    public Map<Dish, Number> getOrderBasket() {
        return orderBasket;
    }

    // Setters

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCost(Number cost) {
        this.cost = cost;
    }

    public void setOrderBasket(Map<Dish, Number> orderBasket) {
        this.orderBasket = orderBasket;
    }

    /**
     * Evaluate the cost of a given order.
     * */
    public Number evaluateCost(Order order){
        Integer totalPrice=0;

        for(Map.Entry<Dish,Number>entry:order.getOrderBasket().entrySet()){
            Dish dish=entry.getKey();
            Number quantity = entry.getValue();

            totalPrice+=(Integer) dish.getPrice()*(Integer)quantity;
        }
        return totalPrice;
    }

    /**
     * Get the order parts as a String.
     * */
    public String getOrderDetails(){
        StringBuilder stringBuilder = new StringBuilder();

        for(Map.Entry<Dish, Number> entry: this.getOrderBasket().entrySet()){
            Dish dish=entry.getKey();
            Number quantity = entry.getValue();

            stringBuilder.append(quantity).append(" * ").append(dish.getName()).append(",");

        }
        if(stringBuilder.toString().length()>1){
            return stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
        }else{
            return stringBuilder.toString();
        }
    }
}
