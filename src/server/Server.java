package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.*;

public class Server implements ServerInterface {

    private StockManagement stockManagement = StockManagement.getINSTANCE();
    private static final Server serverINSTANCE = new Server();
    private List<Supplier> supplierList = new ArrayList<>();
    private List<Drone> droneList = new ArrayList<>();
    private List<Staff> staffList = new ArrayList<>();
    private List<Order> orderList = new ArrayList<>();
    private List<Postcode> postcodeList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<UpdateListener> updateListenerList = new ArrayList<>();
    private HashMap<Staff, Thread> runningStaffThreads=new HashMap<>();
    private HashMap<Drone, Thread> runningDroneThreads=new HashMap<>();
    private ServerComms serverComms= new ServerComms();
    private int droneID = 0;
    private boolean restockingIngredients=true;
    private boolean restockingDishes = true;

    private Server() {
    }

    public static Server getServerINSTANCE() {
        return serverINSTANCE;
    }

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        Configuration configuration = new Configuration(filename);
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        this.restockingIngredients=enabled;
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        this.restockingDishes=enabled;
    }

    public boolean getRestockingIngredientsIsEnabled() {
        return restockingIngredients;
    }

    public boolean getRestockingDishesIsEnabled() {
        return restockingDishes;
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        serverINSTANCE.stockManagement.setDishStockAvailable(dish, (Integer) stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        serverINSTANCE.stockManagement.setIngredientStockAvailable(ingredient, (Integer) stock);
    }

    @Override
    public List<Dish> getDishes() {
        return serverINSTANCE.stockManagement.getDishes();
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish newDish = new Dish(name, description, price); // Create the new dish
        return stockManagement.addDish(newDish, restockThreshold, restockAmount);
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        serverINSTANCE.stockManagement.getDishRestockDetails().remove(dish);
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        dish.getRecipe().put(ingredient, quantity);
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.getRecipe().remove(ingredient);
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        dish.setRecipe(recipe);
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        serverINSTANCE.stockManagement.setDishRestockThreshold(dish, (Integer) restockThreshold);
        serverINSTANCE.stockManagement.setDishRestockAmount(dish, (Integer) restockAmount);
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return serverINSTANCE.stockManagement.getDishRestockThreshold(dish);
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return serverINSTANCE.stockManagement.getDishRestockAmount(dish);
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRecipe();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return serverINSTANCE.stockManagement.getDishStockLevels();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return serverINSTANCE.stockManagement.getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        Ingredient newIngredient = new Ingredient(name, unit, supplier);
        return serverINSTANCE.stockManagement.addIngredient(newIngredient, restockThreshold, restockAmount);
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        serverINSTANCE.stockManagement.removeIngredient(ingredient);
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        serverINSTANCE.stockManagement.setIngredientRestockThreshold(ingredient, (Integer) restockThreshold);
        serverINSTANCE.stockManagement.setIngredientRestockAmount(ingredient, (Integer) restockAmount);
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return serverINSTANCE.stockManagement.getIngredientRestockThreshold(ingredient);
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return serverINSTANCE.stockManagement.getIngredientRestockAmount(ingredient);
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return serverINSTANCE.stockManagement.getIngredientStockLevels();
    }

    @Override
    public List<Supplier> getSuppliers() {
        return serverINSTANCE.supplierList;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier newSupplier = new Supplier(name, (Integer) distance);
        serverINSTANCE.supplierList.add(newSupplier);
        return newSupplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        serverINSTANCE.supplierList.remove(supplier);
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    /**
     * Get the relevant supplier object by its name
     * @param name supplier name
     * @return the existing supplier object
     */
    public Supplier getSupplierByName(String name) {
        for (Supplier supplier : serverINSTANCE.supplierList) {
            if (supplier.getName().equals(name)) {
                return supplier;
            }
        }
        return null;
    }

    @Override
    public List<Drone> getDrones() {
        return serverINSTANCE.droneList;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone newDrone = new Drone(++droneID, (Integer) speed); // Create drone runnable instance
        Thread drone = new Thread(newDrone); // Make a thread with the runnable drone object
        runningDroneThreads.put(newDrone,drone); // Put the pair in the map
        serverINSTANCE.droneList.add(newDrone);
        drone.start();
        return newDrone;
    }

    /**
     * Remove a drone. Stop the thread.
     * @param drone drone to remove
     * @throws UnableToDeleteException
     */
    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        this.runningDroneThreads.get(drone).interrupt(); //interrupt the drone thread
        serverINSTANCE.droneList.remove(drone);

        try {
            this.runningDroneThreads.get(drone).join(); // wait the drone thread to finish and end
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    @Override
    public List<Staff> getStaff() {
        return serverINSTANCE.staffList;
    }

    @Override
    public Staff addStaff(String name) {
        Staff r = new Staff(name); // Create runnable staff object
        Thread newStaff = new Thread(r); // Create a thread with the runnable object
        runningStaffThreads.put(r,newStaff); // put it in the hash map
        serverINSTANCE.staffList.add(r);
        newStaff.start();

        return r;
    }

    /**
     * Remove a staff thread and stop it.
     * @param staff staff member to remove
     * @throws UnableToDeleteException
     */
    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        this.runningStaffThreads.get(staff).interrupt(); // interrupt the thread
        serverINSTANCE.staffList.remove(staff);

        try {
            this.runningStaffThreads.get(staff).join(); // wait the thread to finish and then end
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public List<Order> getOrders() {
        return serverINSTANCE.orderList;
    }

    /**
     * Get a list with the orders that are for delivery
     * @return list of remaining orders
     */
    public synchronized List<Order> getRemainingOrders(){
        List<Order> remainingOrders = new ArrayList<>();

        for(Order order: serverINSTANCE.orderList){
            if(orderIsSatisfied(order) && order.getStatus().equals("For delivery")){
                remainingOrders.add(order);
            }
        }
        return remainingOrders;
    }

    /**
     * Check if order can be made
     * @param order order to be checked
     * @return true or false
     */
    public boolean orderIsSatisfied(Order order){
        for(Map.Entry<Dish, Number> entry:order.getOrderBasket().entrySet()){
            Dish dish = entry.getKey();
            Number quantity = entry.getValue();

            if(StockManagement.getINSTANCE().getDishStockAvailable(dish).intValue()<quantity.intValue()){
                return false;
            }
        }
        if(order.getStatus().equals("In progress")){
            order.setStatus("For delivery");
        }
        return true;
    }

    /**
     * Get a specific order object by its name and elements
     * @param name name of order
     * @param elements content of order
     * @return order object
     */
    public Order getOrderByName(String name,String elements){
        for(Order order:orderList){
            List<String> queriedElements=Arrays.asList(elements.split(","));
            List<String> checkedElement=Arrays.asList(order.getOrderDetails().split(","));
            Collections.sort(queriedElements);
            Collections.sort(checkedElement);

            if(order.getName().equals(name)&&queriedElements.equals(checkedElement)){
                return order;
            }
        }
        return null;
    }

    /**
     * Get all orders by a given user
     * @param user given user
     * @return list of his/her orders
     */
    public List<Order> getUserOrder(User user){
        List<Order> orders=new ArrayList<>();
        for(Order order: this.getOrders()){
            if(order.getName().equals(user.getName())){
                orders.add(order);
            }
        }
        return orders;
    }

    /**
     * Add a new order from the client
     * @param name name of order/ client
     * @param orderBasket content of order
     */
    public synchronized void addOrder(String name, Map<Dish, Number> orderBasket) {
        Order newOrder = new Order(name, orderBasket);
        newOrder.setCost(newOrder.evaluateCost(newOrder));
        newOrder.setStatus("In progress");
        serverINSTANCE.orderList.add(newOrder);
    }

    @Override
    public synchronized void removeOrder(Order order) throws UnableToDeleteException {
        serverINSTANCE.orderList.remove(order);
    }

    @Override
    public Number getOrderDistance(Order order) {
        return serverINSTANCE.getPostcodeByCode(order.getName()).getDistance();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getStatus().equals("Complete");
    }

    @Override
    public synchronized String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public List<Postcode> getPostcodes() {
        return serverINSTANCE.postcodeList;
    }

    @Override
    public void addPostcode(String code, Number distance) {
        Postcode newPostcode = new Postcode(code, (Integer) distance);
        serverINSTANCE.postcodeList.add(newPostcode);
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        serverINSTANCE.postcodeList.remove(postcode);
    }

    /**
     * Get a specific postcode object by its code
     * @param code code of postcode
     * @return postcode
     */
    public Postcode getPostcodeByCode(String code) {
        for (Postcode postcode : serverINSTANCE.postcodeList) {
            if (postcode.getName().equals(code)) {
                return postcode;
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        return serverINSTANCE.userList;
    }

    /**
     * Get specific user by its name
     * @param name name of user
     * @return user object
     */
    public User getUserByName(String name) {
        for (User user : this.userList) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }


    public void addUser(String username, String password, String address, Postcode postcode) {
        User user = new User(username, password, address, postcode);
        serverINSTANCE.userList.add(user);
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        serverINSTANCE.userList.remove(user);
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        this.updateListenerList.add(listener);
    }

    @Override
    public void notifyUpdate() {
        this.updateListenerList.forEach(updateListener -> updateListener.updated(new UpdateEvent()));
    }
}
