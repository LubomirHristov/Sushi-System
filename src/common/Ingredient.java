package common;

public class Ingredient extends Model {
    private String name;
    private String unitOfMeasurement;
    private Supplier supplier;

    public Ingredient(String name, String unitOfMeasurement, Supplier supplier) {
        this.name = name;
        this.unitOfMeasurement = unitOfMeasurement;
        this.supplier = supplier;
    }

    //Getters

    @Override
    public String getName() {
        return this.name;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    //Setters

    public void setName(String name){
        notifyUpdate("IngredientName",this.name,name);
        this.name = name;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        notifyUpdate("UnitOfMeasurement",this.unitOfMeasurement,unitOfMeasurement);
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public void setSupplier(Supplier supplier) {
        notifyUpdate("Supplier",this.supplier,supplier);
        this.supplier = supplier;
    }


}
