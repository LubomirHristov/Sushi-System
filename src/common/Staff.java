package common;

import server.Server;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Staff extends Model implements Runnable {
    private String name;
    private String status;
    private StockManagement stockManagement = StockManagement.getINSTANCE();

    public Staff(String name) {
        this.name = name;
    }

    // Getters

    @Override
    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    @Override
    public void setName(String name){
        this.name=name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private boolean makeDish(Map<Ingredient, Number> recipe) {

        for (Map.Entry<Ingredient, Number> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Integer amount = (Integer) entry.getValue();

            if(enoughIngredient(ingredient)){
                stockManagement.setIngredientStockAvailable(ingredient,(Integer)stockManagement.getIngredientStockAvailable(ingredient)-amount);
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(20001, 60001));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return true;
            }
        }
        return false;
    }

    private boolean enoughIngredient(Ingredient ingredient){

        return (Integer) stockManagement.getIngredientStockAvailable(ingredient) >= (Integer) this.stockManagement.getIngredientRestockThreshold(ingredient);
    }

    private boolean shouldPrepareDish(Dish dish){
        return (Integer)this.stockManagement.getDishStockAvailable(dish)<(Integer) this.stockManagement.getDishRestockThreshold(dish) && Server.getServerINSTANCE().getRestockingDishesIsEnabled();
    }

    private boolean notEnoughDishStock(Dish dish){
        return (Integer)this.stockManagement.getDishStockAvailable(dish)<(Integer)this.stockManagement.getDishRestockThreshold(dish)+(Integer) this.stockManagement.getDishRestockAmount(dish);
    }

    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()){
            for (Dish dish: this.stockManagement.getDishes()){

                if(shouldPrepareDish(dish)){
                    status ="Working";
                    while(notEnoughDishStock(dish)&&!Thread.currentThread().isInterrupted()){
                       boolean dishIsPrepared= this.makeDish(dish.getRecipe());
                       if(dishIsPrepared){
                           this.stockManagement.setDishStockAvailable(dish, (Integer) this.stockManagement.getDishStockAvailable(dish)+1);
                       }
                    }
                }else {
                    status ="Idle";
                }
            }
        }
    }
}
