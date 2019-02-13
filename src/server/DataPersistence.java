package server;

import common.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class DataPersistence {
    private static final DataPersistence dataPersistenceINSTANCE = new DataPersistence();
    private static final Server server = Server.getServerINSTANCE();
    private static final StockManagement stockManagement = StockManagement.getINSTANCE();

    public static DataPersistence getDataPersistenceINSTANCE() {
        return dataPersistenceINSTANCE;
    }

    /**
     * Attach shutDownHook so that data is written on window close.
     * In a new thread get all data lists, convert them as string in the provided format for the configuration and write them in a file.
     */
    public void saveConfiguration(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("savedConfiguration.txt", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (writer != null) {
                for(Supplier supplier: server.getSuppliers()){
                    writer.println("SUPPLIER:"+supplier.getName()+":"+supplier.getDistance());
                }
                writer.println();

                for(Ingredient ingredient: stockManagement.getIngredients()){
                    writer.println("INGREDIENT:"+ingredient.getName()+":"+ingredient.getUnitOfMeasurement()+":"+ingredient.getSupplier().getName()+":"+ stockManagement.getIngredientRestockThreshold(ingredient)+":"+stockManagement.getIngredientRestockAmount(ingredient));
                }

                writer.println();

                for(Dish dish: stockManagement.getDishes()){
                    writer.println("DISH:"+stockManagement.getDishes().get(stockManagement.getDishes().indexOf(dish)).getName()+":"+stockManagement.getDishes().get(stockManagement.getDishes().indexOf(dish)).getDescription()+":"+stockManagement.getDishes().get(stockManagement.getDishes().indexOf(dish)).getPrice()+":"+stockManagement.getDishRestockThreshold(dish)+":"+stockManagement.getDishRestockAmount(dish)+":"+stockManagement.getDishes().get(stockManagement.getDishes().indexOf(dish)).getIngredients());
                }

                writer.println();

                for(Postcode postcode: server.getPostcodes()){
                    writer.println("POSTCODE:"+postcode.getName()+":"+postcode.getDistance());
                }

                for(User user: server.getUsers()){
                    writer.println("USER:"+user.getName()+":"+user.getPassword()+":"+user.getAddress()+":"+user.getPostcode().getName());
                }

                writer.println();

                for(Order order: server.getOrders()){
                    writer.println("ORDER:"+order.getName()+":"+order.getOrderDetails());
                }

                writer.println();

                for(Ingredient ingredient:stockManagement.getIngredients()){
                    writer.println("STOCK:"+ingredient.getName()+":"+stockManagement.getIngredientStockAvailable(ingredient));
                }

                for(Dish dish :stockManagement.getDishes()){
                    writer.println("STOCK:"+dish.getName()+":"+stockManagement.getDishStockAvailable(dish));
                }

                writer.println();

                for(Staff staff:server.getStaff()){
                    writer.println("STAFF:"+staff.getName());
                }

                writer.println();

                for(Drone drone: server.getDrones()){
                    writer.println("DRONE:"+drone.getSpeed());
                }

                writer.close();
            }
        }));
    }
}
