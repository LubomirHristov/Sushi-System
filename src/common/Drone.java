package common;

import server.Server;

import java.util.Map;

public class Drone extends Model implements Runnable {
    private int id;
    private Number speed;
    private String status;

    public Drone(int id, int speed) {
        this.id = id;
        this.speed = speed;
    }

    // Getters

    @Override
    public String getName() {
        return "Drone" + id;
    }

    public Number getSpeed() {
        return speed;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Check if ingredient is below threshold
     *
     * @param ingredient ingredient to be checked
     * @return true or false
     */
    private boolean hasToFetchIngredient(Ingredient ingredient) {
        return (Integer) StockManagement.getINSTANCE().getIngredientStockAvailable(ingredient) < (Integer) StockManagement.getINSTANCE().getIngredientRestockThreshold(ingredient);
    }

    /**
     * Get ingredient stock.
     *
     * @param ingredient ingredient that should be supplied
     */
    private void fetchIngredient(Ingredient ingredient) {
        status = "Fetching ingredients";
        try {
            Thread.sleep(timeToSupplier(ingredient.getSupplier()).longValue());
            StockManagement.getINSTANCE().setIngredientStockAvailable(ingredient, (Integer) StockManagement.getINSTANCE().getIngredientRestockThreshold(ingredient) + (Integer) StockManagement.getINSTANCE().getIngredientRestockAmount(ingredient));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Calculates the time to reach a given supplier.
     *
     * @param supplier given supplier
     * @return time in milliseconds
     */
    private Number timeToSupplier(Supplier supplier) {
        return (supplier.getDistance().intValue() / this.getSpeed().intValue()) * 10000;
    }

    /**
     * Calculates the time to reach a given user
     *
     * @param user given user
     * @return time in milliseconds
     */
    private Number timeToUser(User user) {
        return (user.getPostcode().getDistance().intValue() / this.getSpeed().intValue()) * 10000;
    }

    /**
     * Checks if there are any deliveries that should be made.
     *
     * @return true or false
     */
    private boolean shouldMakeDelivery() {
        return !Server.getServerINSTANCE().getRemainingOrders().isEmpty();
    }

    /**
     * Reduces the dishes available in the restaurant when the order has been completed.
     *
     * @param order order that has been completed
     */
    private void reduceDishStock(Order order) {
        for (Map.Entry<Dish, Number> entry : order.getOrderBasket().entrySet()) {
            Dish dish = entry.getKey();
            Number quantity = entry.getValue();

            StockManagement.getINSTANCE().setDishStockAvailable(dish, StockManagement.getINSTANCE().getDishStockAvailable(dish).intValue() - quantity.intValue());
        }
    }

    /**
     * Make the delivery.
     */
    private void makeDelivery() {
        if (shouldMakeDelivery()) {
            status = "Delivering...";

            if (Server.getServerINSTANCE().getRemainingOrders().size() > 0) {
                Order orderToDeliver = Server.getServerINSTANCE().getRemainingOrders().get(0); // Always get the first element of the list.(Status is reset.)

                if (!orderToDeliver.getStatus().equals("Complete")) {
                    orderToDeliver.setStatus("Complete");
                    try {
                        this.reduceDishStock(orderToDeliver);
                        Thread.sleep(this.timeToUser(Server.getServerINSTANCE().getUserByName(orderToDeliver.getName())).longValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (Server.getServerINSTANCE().getRestockingIngredientsIsEnabled()) {
                for (Ingredient ingredient : StockManagement.getINSTANCE().getIngredients()) {
                    status = "Monitoring ingredients";
                    if (hasToFetchIngredient(ingredient)) {
                        fetchIngredient(ingredient);
                    }
                }
            }

            if (shouldMakeDelivery()) {
                makeDelivery();
            } else {
                status = "Idle";
            }
        }
    }
}

