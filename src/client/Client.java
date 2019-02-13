package client;

import common.UpdateListener;
import common.*;

import java.util.*;

public class Client implements ClientInterface {

    private ClientComms clientComms = new ClientComms();
    private List<UpdateListener> updateListenerList = new ArrayList<>();
    private List<Postcode> postcodeList = clientComms.getObjectPostcodes();
    private List<Order> orderList = clientComms.getOrderObjects();
    private List<User> userList = clientComms.getUserObjects();
    private List<Dish> dishList = clientComms.getDishObjects();

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        User newUser = clientComms.registerUser(String.format("REGISTER USER:%s:%s:%s:%s:%d",username,password,address,postcode.getName(),postcode.getDistance().intValue()));
        userList.add(newUser);
        return newUser;
    }

    @Override
    public User login(String username, String password) {
        for(User user: userList){
            if(user.getName().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }

    // Getters

    @Override
    public List<Postcode> getPostcodes() {
        return postcodeList;
    }

    @Override
    public List<Dish> getDishes() {
        return dishList;
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    @Override
    public Number getBasketCost(User user) {
        Integer totalPrice=0;

        for(Map.Entry<Dish,Number>entry:user.getBasket().entrySet()){
            Dish dish=entry.getKey();
            Number quantity = entry.getValue();

            totalPrice+=(Integer) dish.getPrice()*(Integer)quantity;
        }
        return totalPrice;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        userList.get(userList.indexOf(user)).getBasket().put(dish,quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        if((Integer)quantity==0){
            userList.get(userList.indexOf(user)).getBasket().remove(dish);
        }else{
            userList.get(userList.indexOf(user)).getBasket().replace(dish,quantity);
        }

    }

    /**
     * Send a message to the server to add the given user's order.
     * Add the order to the list and remove the basket from which the order was created.
     * @param user user of basket
     * @return the newly created order.
     */
    @Override
    public Order checkoutBasket(User user) {
        Order order=clientComms.addOrder(String.format("ADD ORDER:%s:%s:%d",user.getName(),user.getBasket(),this.getBasketCost(user).intValue()));
        orderList.add(order);
        this.clearBasket(user);
        return order;
    }

    @Override
    public void clearBasket(User user) {
        userList.get(userList.indexOf(user)).getBasket().clear();
    }

    @Override
    public List<Order> getOrders(User user) {
        return clientComms.getUserOrder(user);
    }

    /**
     * Get a specific order object by it's name and content.
     * The order of the content should not matter.
     * @param order given order
     * @return found order object
     */
    public Order getOrderByDetails(Order order){
        for(Order checkedOrder:orderList){
            List<String> orderBasket = Arrays.asList(order.getOrderDetails().split(","));
            List<String> checkOrderBasket = Arrays.asList(checkedOrder.getOrderDetails().split(","));
            Collections.sort(orderBasket);
            Collections.sort(checkOrderBasket);

            if(checkedOrder.getName().equals(order.getName()) && orderBasket.equals(checkOrderBasket)){
                return checkedOrder;
            }
        }
        return null;
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return this.getOrderByDetails(order).getStatus().equals("Complete");
    }

    @Override
    public String getOrderStatus(Order order) {
        return this.getOrderByDetails(order).getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.evaluateCost(order);
    }

    @Override
    public void cancelOrder(Order order) {
        clientComms.updateOrderStatus(order.getName(),order.getOrderDetails());
        this.getOrderByDetails(order).setStatus("Cancelled");
    }

    /**
     * Add a new update listener.
     * @param listener An update listener to be informed of all model changes.
     */
    @Override
    public void addUpdateListener(UpdateListener listener) {
        this.updateListenerList.add(listener);
    }

    /**
     * Notify all update listeners that this class has updated.
     */
    @Override
    public void notifyUpdate() {
        this.updateListenerList.forEach(updateListener -> updateListener.updated(new UpdateEvent()));
    }
}
