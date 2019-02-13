package common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dish extends Model {
    private String name;
    private String description;
    private Number price;
    private Map<Ingredient, Number> recipe = new ConcurrentHashMap<>();

    public Dish(String name, String description, Number price) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    //Getters

    @Override
    public String getName() {
        return this.name;
    }

    public Number getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Map<Ingredient, Number> getRecipe() {
        return recipe;
    }

    //Setters

    @Override
    public void setName(String name) {
        notifyUpdate("DishName", this.name, name);
        this.name = name;
    }

    public void setDescription(String description) {
        notifyUpdate("DishDescription", this.description, description);
        this.description = description;
    }

    public void setPrice(Number price) {
        notifyUpdate("DishPrice", this.price, price);
        this.price = price;
    }

    public void setRecipe(Map<Ingredient, Number> recipe) {
        notifyUpdate("Recipe",this.recipe,recipe);
        this.recipe = recipe;
    }

    /**
    * Get the dish recipe ingredients as a String.
    */
   public String getIngredients(){
       StringBuilder stringBuilder = new StringBuilder();

       for(Map.Entry<Ingredient, Number> entry: this.getRecipe().entrySet()){
           Ingredient ingredient=entry.getKey();
           Number quantity = entry.getValue();

           stringBuilder.append(quantity).append(" * ").append(ingredient.getName()).append(",");

       }

       if(stringBuilder.toString().length()==0){
           return stringBuilder.toString();
       }
       return stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
   }
}
