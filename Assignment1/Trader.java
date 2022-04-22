import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class Trader {
    public String id;
    public double balance;
    // A hashmap for each trader to store <product, amount>
    public HashMap<String, Double> current_inventory;
    public ArrayList<String> current_inventory_products;
    Set<String> current_products_set;
    public String trader_details;

    public Trader(String id, double balance) {
        this.id = id;
        this.balance = balance;
        current_inventory = new HashMap<String, Double>();
        current_inventory_products = new ArrayList<String>();
        trader_details = "";

    }

    public String getID() {
        return this.id;
    }

    public double getBalance() {
        return this.balance;
    }

    public double importProduct(String product, double amount) {
        if(product == null || amount <= 0){
            return -1.0;
        }
        // If trader doesnt have the product yet
        if(current_inventory.containsKey(product) != true){
            current_inventory.put(product, amount);
        }
        else{
            // If trader already has the product
            current_inventory.put(product,
            current_inventory.get(product) + amount);
        }
        return current_inventory.get(product);
    }

    public double exportProduct(String product, double amount) {
        if(product == null || amount <= 0){
            return -1.0;
        }
        if(current_inventory.containsKey(product) != true){
            return -1.0;
        }
        else{
            // If the amount is greater than the current amount
            if(amount > current_inventory.get(product)){
                return -1.0;
            }
            else{
                // If the trader has the product and the amount is good
                current_inventory.put(product,
                current_inventory.get(product) - amount);
            }
        }
        return current_inventory.get(product);
    }

    public double getAmountStored(String product) {
        if(product == null){
            return -1.0;
        }
        if(current_inventory.containsKey(product) != true){
            return 0.0;
        }
        return current_inventory.get(product);
    }

    public List<String> getProductsInInventory() {
        current_inventory_products.clear();
        // Putting products into a set
        current_products_set = current_inventory.keySet();
        for(String product:current_products_set){
            if(current_inventory.get(product) != 0){
                // Putting products into a list from the set
                current_inventory_products.add(product);
            }
        }
        // Sorting the list alphabetically
        current_inventory_products.sort((x, y) -> x.compareTo(y));
        return current_inventory_products;
    }

    public double adjustBalance(double change) {
        this.balance = this.balance + change;
        return this.balance;
    }

    public String toString() {
        current_inventory_products.clear();
        current_products_set = current_inventory.keySet();
        for(String product:current_products_set){
            current_inventory_products.add(product);
        }
        // Sorting the traders alphabetically
        current_inventory_products.sort((x, y) -> x.compareTo(y));
        String string_balance = String.format("%.2f", balance);
        trader_details = "";
        // Getting trader details
        trader_details += id + ": $" + string_balance + " {";
        // Getting inventory details
        for(String product : current_inventory_products){
            if(current_inventory.get(product) != 0){
                String string_amount = String.format
                    ("%.2f", current_inventory.get(product));
                trader_details += product + ": "
                    + string_amount + ", ";
            }
        }
        if(current_inventory.isEmpty() != true){
            trader_details = trader_details.replaceFirst(".$","");
            trader_details = trader_details.replaceFirst(".$","");
        }
        trader_details = trader_details + "}";
        return trader_details;
    }

    public static void writeTraders(List<Trader> traders, String path) {
        if(traders == null || traders.size() == 0){
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream output = new DataOutputStream(f);
            for(Trader trader : traders){
                String this_trader = trader.toString();
                output.writeBytes(this_trader+"\n");
            }
            output.flush();
            output.close();
        }
        catch (FileNotFoundException e) {
            return;
        }
        catch(IOException e){
            return;
        }
    }

    public static void writeTradersBinary(List<Trader> traders, String path) {
        if(traders == null || traders.size() == 0){
            return;
        }
        try{
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream output = new DataOutputStream(f);
            for(Trader trader : traders){
                String trader_string = trader.toString();
                output.writeUTF(trader_string);
                output.writeUTF("\u001F");
            }
            output.close();
            return;
        }
        catch(FileNotFoundException e){
            return;
        }
        catch(IOException e){
            return;
        }
    }
}
