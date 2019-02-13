package server;

import common.Dish;
import common.Ingredient;
import common.StockManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    private Server server = Server.getServerINSTANCE();
    private Map<Dish, Number> basket = new ConcurrentHashMap<>();

    public Configuration(String filename){
        this.readLines(filename);
    }

    /**
     * Read all lines in the file and parse them depending on their starting String.
     * @param filename name of file
     */
    private void readLines(String filename){
        this.resetAllLists(); // Clear all data lists

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("SUPPLIER")){
                    addSupplierConfig(line);
                }

                if(line.startsWith("INGREDIENT")){
                    addIngredientConfig(line);
                }

                if(line.startsWith("DISH")){
                    addDishConfig(line);
                }

                if(line.startsWith("POSTCODE")){
                    addPostcodeConfig(line);
                }

                if(line.startsWith("USER")){
                    addUserConfig(line);
                }

                if(line.startsWith("ORDER")){
                    addOrderConfig(line);
                }

                if(line.startsWith("STOCK")){
                    addStockConfig(line);
                }

                if(line.startsWith("STAFF")){
                    addStaffConfig(line);
                }

                if(line.startsWith("DRONE")){
                    addDroneConfig(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDroneConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addDrone(Integer.parseInt(values[1]));
    }

    private void addStaffConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addStaff(values[1]);
    }

    private void addStockConfig(String line) {
        String[] values;
        values=line.split(":");

        // Check if a dish or ingredient should be added and add it

        if(StockManagement.getINSTANCE().checkIfIsDish(values[1])){
            StockManagement.getINSTANCE().setDishStockAvailable(StockManagement.getINSTANCE().getDishByName(values[1]),Integer.parseInt(values[2]));
        }else{
            StockManagement.getINSTANCE().setIngredientStockAvailable(StockManagement.getINSTANCE().getIngredientByName(values[1]),Integer.parseInt(values[2]));
        }
    }

    private void addOrderConfig(String line) {
        String[] values;
        String[] basketInfo;
        String[] singleDishType;
        values=line.split(":");
        basketInfo=values[2].split(",");
        for(String element: basketInfo){
            singleDishType=element.split(" \\* ");
            basket.put(StockManagement.getINSTANCE().getDishByName(singleDishType[1]),Integer.parseInt(singleDishType[0])); // parse basket details
        }
        server.addOrder(values[1],basket);
    }

    private void addUserConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addUser(values[1],values[2],values[3],server.getPostcodeByCode(values[4]));
    }

    private void addPostcodeConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addPostcode(values[1],Integer.parseInt(values[2]));
    }

    private void addDishConfig(String line) {
        String[] values;
        Map<Ingredient, Number> recipe = new ConcurrentHashMap<>();
        values=line.split(":");
        String name = values[1];
        String[] ingredientQuantities;
        server.addDish(values[1],values[2],Integer.parseInt(values[3]),Integer.parseInt(values[4]),Integer.parseInt(values[5]));
        values=values[6].split(",");
        for(String element: values){
            ingredientQuantities=element.split(" \\* ");
            recipe.put(StockManagement.getINSTANCE().getIngredientByName(ingredientQuantities[1]),Integer.parseInt(ingredientQuantities[0])); // parse and set the dish recipe
            StockManagement.getINSTANCE().getDishByName(name).setRecipe(recipe);
        }
    }

    private void addIngredientConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addIngredient(values[1],values[2],server.getSupplierByName(values[3]),Integer.parseInt(values[4]),Integer.parseInt(values[5]));
    }

    private void addSupplierConfig(String line) {
        String[] values;
        values=line.split(":");
        server.addSupplier(values[1],Integer.parseInt(values[2]));
    }

    // Clear all lists before adding the parsed data
    private void resetAllLists(){
        StockManagement.getINSTANCE().getIngredientRestockDetails().clear();
        StockManagement.getINSTANCE().getDishRestockDetails().clear();
        server.getStaff().clear();
        server.getSuppliers().clear();
        server.getPostcodes().clear();
        server.getUsers().clear();
        server.getOrders().clear();
        server.getDrones().clear();
    }
}
