package common;

import server.ServerInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class StockManagement {
    private static final StockManagement INSTANCE = new StockManagement();
    private Map<Ingredient, List<Number>> ingredientRestockDetails = new ConcurrentHashMap<>();
    private Map<Dish, List<Number>> dishRestockDetails = new ConcurrentHashMap<>();
    private List<Number> restockDetails;
    private final Object lockIngredient = new Object();
    private final Object lockDish = new Object();

    private StockManagement(){}

    /**
    * Instance of the Class.
    */
    public static StockManagement getINSTANCE(){
        return INSTANCE;
    }

    //Ingredient methods

    public Ingredient addIngredient(Ingredient ingredient, Number restockThreshold, Number restockAmount){
        this.restockDetails= new CopyOnWriteArrayList<>();
        this.restockDetails.add(restockThreshold); // On first index is the threshold
        this.restockDetails.add(restockAmount); // On second index is the restock amount
        this.restockDetails.add(0); // On third index is stock available (initially it is 0)
        this.ingredientRestockDetails.put(ingredient,restockDetails);
        return ingredient;
    }

    public void removeIngredient(Ingredient ingredient) throws ServerInterface.UnableToDeleteException{
        synchronized (lockIngredient){
            this.ingredientRestockDetails.remove(ingredient);
        }
    }

    /**
     * Get an ingredient object by searching for its name.
     */
    public Ingredient getIngredientByName(String name){
        synchronized (lockIngredient){
            for(Ingredient ingredient: this.getIngredients()){
                if(ingredient.getName().equals(name)){
                    return ingredient;
                }
            }
        }
        return null;
    }

    /**
     * Get stock of all ingredients.
     * */
    public Map<Ingredient,Number> getIngredientStockLevels(){
        Map<Ingredient, Number> ingredientStockLevels = new ConcurrentHashMap<>();

        synchronized (lockIngredient){
            for (Ingredient ingredient : this.getIngredients()) {
                ingredientStockLevels.put(ingredient, this.getIngredientStockAvailable(ingredient));
            }
        }

        return ingredientStockLevels;
    }

    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity){
        synchronized (lockDish){
            dish.getRecipe().put(ingredient,quantity);
        }
    }

    public void removeIngredientFromDish(Dish dish, Ingredient ingredient){
        synchronized (lockDish){
            dish.getRecipe().remove(ingredient);
        }
    }

    //Dish methods

    public Dish addDish(Dish dish,Number restockThreshold, Number restockAmount){
        this.restockDetails=new CopyOnWriteArrayList<>();
        this.restockDetails.add(restockThreshold); // First index is threshold
        this.restockDetails.add(restockAmount); // Second index is restock amount
        this.restockDetails.add(0); // Third index is stock available (initially 0)
        this.dishRestockDetails.put(dish,restockDetails);
        return dish;
    }

    public void removeDish(Dish dish){
        synchronized (lockDish){
            this.dishRestockDetails.remove(dish);
        }
    }

    /**
     * Get the stock levels of all dishes.
     * */
    public Map<Dish, Number> getDishStockLevels(){
        Map<Dish, Number> dishStockLevels = new ConcurrentHashMap<>();

        synchronized (lockDish){
            for(Dish dish: this.getDishes()){
                dishStockLevels.put(dish,this.getDishStockAvailable(dish));
            }
        }

        return dishStockLevels;
    }

    /**
     * Get dish object by its name.
     * */
    public Dish getDishByName(String name){

        synchronized (lockDish){
            for(Dish dish: this.dishRestockDetails.keySet()){
                if(dish.getName().equals(name)){
                    return dish;
                }
            }
        }
        return null;
    }

    /**
     * Check if the given object is a dish.
     * Used to differentiate between dishes and ingredients by their names.
     * */
    public boolean checkIfIsDish(String name){
        synchronized (lockDish){
            return this.getDishes().contains(this.getDishByName(name));
        }
    }

    //Getters for ingredients

    public List<Ingredient> getIngredients() {
        synchronized (lockIngredient){
            return new ArrayList<>(ingredientRestockDetails.keySet());
        }
    }

    public Map<Ingredient, List<Number>> getIngredientRestockDetails() {
        synchronized (lockIngredient) {
            return ingredientRestockDetails;
        }
    }

    public void setIngredientRestockDetails(Map<Ingredient, List<Number>> ingredientRestockDetails) {
        synchronized (lockIngredient) {
            this.ingredientRestockDetails = ingredientRestockDetails;
        }
    }

    public void setDishRestockDetails(Map<Dish, List<Number>> dishRestockDetails) {
        this.dishRestockDetails = dishRestockDetails;
    }

    /**
     * Get the restock threshold of a given ingredient.
     * */
    public Number getIngredientRestockThreshold(Ingredient ingredient) {
        synchronized (lockIngredient) {
            return this.ingredientRestockDetails.get(ingredient).get(0);
        }
    }

    /**
     * Get the restock amount of a given ingredient.
     * */
    public Number getIngredientRestockAmount(Ingredient ingredient) {
        synchronized (lockIngredient) {
            return this.ingredientRestockDetails.get(ingredient).get(1);
        }
    }

    /**
     * Get the stock available of a given ingredient.
     * */
    public Number getIngredientStockAvailable(Ingredient ingredient) {
        synchronized (lockIngredient) {
            return this.ingredientRestockDetails.get(ingredient).get(2);
        }
    }

    //Getters for dishes

    public List<Dish> getDishes() {
        synchronized (lockDish){
            return new ArrayList<>(dishRestockDetails.keySet());
        }
    }

    public Map<Dish, List<Number>> getDishRestockDetails() {
        synchronized (lockDish) {
            return dishRestockDetails;
        }
    }

    /**
     * Get the restock threshold of a given dish.
     * */
    public Number getDishRestockThreshold(Dish dish) {
        synchronized (lockDish) {
            return this.dishRestockDetails.get(dish).get(0);
        }
    }

    /**
     * Get the restock amount of a given dish.
     * */
    public Number getDishRestockAmount(Dish dish) {
        synchronized (lockDish) {
            return this.dishRestockDetails.get(dish).get(1);
        }
    }

    /**
     * Get the stock available of a given dish.
     * */
    public Number getDishStockAvailable(Dish dish) {
        synchronized (lockDish) {
            return this.dishRestockDetails.get(dish).get(2);
        }
    }

    //Setters for ingredients

    /**
     * Set the restock threshold of a given ingredient.
     * */
    public void setIngredientRestockThreshold(Ingredient ingredient, Integer restockThreshold) {
        ingredient.notifyUpdate("IngredientRestockThreshold", this.ingredientRestockDetails.get(ingredient).get(0),restockThreshold);
        synchronized (lockIngredient) {
            this.ingredientRestockDetails.get(ingredient).set(0, restockThreshold);
        }
    }

    /**
     * Set the restock amount of a given ingredient.
     * */
    public void setIngredientRestockAmount(Ingredient ingredient, Integer restockAmount) {
        ingredient.notifyUpdate("IngredientRestockAmount", this.ingredientRestockDetails.get(ingredient).get(1),restockAmount);
        synchronized (lockIngredient) {
            this.ingredientRestockDetails.get(ingredient).set(1, restockAmount);
        }
    }

    /**
     * Set the stock available of a given ingredient.
     * */
    public void setIngredientStockAvailable(Ingredient ingredient, Integer stockAvailable) {
        ingredient.notifyUpdate("IngredientStockAvailable", this.ingredientRestockDetails.get(ingredient).get(2),stockAvailable);
        synchronized (lockIngredient) {
            this.ingredientRestockDetails.get(ingredient).set(2, stockAvailable);
        }
    }

    //Setters for dishes

    /**
     * Set the restock threshold of a given dish.
     * */
    public void setDishRestockThreshold(Dish dish, Integer restockThreshold) {
        dish.notifyUpdate("DishRestockThreshold", this.dishRestockDetails.get(dish).get(0), restockThreshold);
        synchronized (lockDish) {
            this.dishRestockDetails.get(dish).set(0, restockThreshold);
        }
    }

    /**
     * Set the restock amount of a given dish.
     * */
    public void setDishRestockAmount(Dish dish, Integer restockAmount) {
        dish.notifyUpdate("DishRestockAmount", this.dishRestockDetails.get(dish).get(1), restockAmount);
        synchronized (lockDish) {
            this.dishRestockDetails.get(dish).set(1, restockAmount);
        }
    }

    /**
     * Set the stock available of a given dish.
     * */
    public void setDishStockAvailable(Dish dish, Integer stockAvailable) {
        dish.notifyUpdate("DishStockAvailable", this.dishRestockDetails.get(dish).get(2), stockAvailable);
        synchronized (lockDish) {
            this.dishRestockDetails.get(dish).set(2, stockAvailable);
        }
    }
}
