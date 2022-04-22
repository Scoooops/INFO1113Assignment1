import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Exception;

public class Exchange {
    public static List<Trade> all_trades;
    public static List<Trader> traders;
    public List<Trader> traders_ordered;
    public List<Trade> current_trades;
    public List<Trade> current_trade_list;
    public Order current_order;
    public List<String> trader_ids;
    public boolean trader_exists;
    public boolean trader_has_trades;
    public boolean order_exists;
    public boolean trader_has_item;
    public boolean product_has_been_traded;
    public boolean items_in_inventory;
    public List<String> inventory_ls;
    public short order_id_counter;
    public String hex_counter;
    public boolean is_buy;
    public boolean is_import;
    public boolean is_binary;
    public double product_amount;
    public double price;
    public double import_result;
    public double export_result;

    public void run() {
        Market market = new Market();
        current_trades = new ArrayList<Trade>();
        all_trades = new ArrayList<Trade>();
        trader_ids = new ArrayList<String>();
        inventory_ls = new ArrayList<String>();
        traders = new ArrayList<Trader>();
        traders_ordered = new ArrayList<Trader>();
        Scanner input = new Scanner(System.in);
        order_id_counter = 0;

        while(true){    //Loop to handle system inputs
            trader_exists = false;
            trader_has_trades = false;
            order_exists = false;
            product_has_been_traded = false;
            is_binary = false;
            items_in_inventory = false;
            trader_has_item = false;
            System.out.print("$ ");
            // Checking for input
            if(input.hasNextLine() == false){
                continue;
            }
            // Take the command and put it into a list word by word
            String command = input.nextLine();
            String[] command_ls = command.trim().split("\\s+");

            if(command_ls[0].equalsIgnoreCase("exit")){
                System.out.println("Have a nice day.\n");
                return;
            }


            else if(command_ls[0].equalsIgnoreCase("add")){
                for(Trader trader : traders){
                    // If trader already exists
                    if(trader.getID().equals(command_ls[1])){
                        System.out.println
                        ("Trader with given ID already exists.");
                        trader_exists = true;
                        break;
                    }
                }
                if(trader_exists == false){
                    double balance = Double.parseDouble(command_ls[2]);
                    // Checking for negative balance
                    if(balance < 0){
                        System.out.println
                        ("Initial balance cannot be negative.");
                        continue;
                    }
                    // Adding trader to the market
                    Trader new_trader =
                    new Trader(command_ls[1], balance);
                    traders.add(new_trader);
                    System.out.println("Success.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("balance")){
                for(Trader trader : traders){
                    // If trader exists, print their balance
                    if(trader.getID().equals((command_ls[1]))){
                        System.out.println("$" + String.format
                            ("%.2f", trader.getBalance()));
                        trader_exists = true;
                        break;
                    }
                }
                // If they dont exist
                if(trader_exists == false){
                    System.out.println("No such trader in the market.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("inventory")){
                items_in_inventory = false;
                for(Trader trader : traders){
                    // Trader exists
                    if(trader.getID().equals(command_ls[1])){
                        inventory_ls = trader.getProductsInInventory();
                        if(inventory_ls.size() != 0){
                            // For each item, print its name
                            for(String product : inventory_ls){
                                if(trader.getAmountStored(product) > 0){
                                    items_in_inventory = true;
                                    System.out.println(product);
                                }
                            }
                        }
                        if(inventory_ls.size() > 0){
                            inventory_ls.clear();
                        }
                        trader_exists = true;
                        break;
                    }
                }
                // If has no items but does exist
                if(items_in_inventory == false && trader_exists){
                    System.out.println
                    ("Trader has an empty inventory.");
                }
                // If trader doesnt exist
                if(trader_exists == false){
                    System.out.println("No such trader in the market.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("amount")){
                for(Trader trader : traders){
                    // Trader exists
                    if(trader.getID().equals(command_ls[1])){
                        // Find amount stored of given product
                        if(trader.getAmountStored(command_ls[2]) != 0.0){
                            String amount = String.format
                            ("%.2f", trader.getAmountStored(command_ls[2]));
                            System.out.println(amount);
                        }
                        // If product is not in inventory
                        else{
                            System.out.println("Product not in inventory.");
                        }
                        trader_exists = true;
                        break;
                    }
                }
                // If trader doesnt exist
                if(trader_exists == false){
                    System.out.println("No such trader in the market.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("sell") ||
                    command_ls[0].equalsIgnoreCase("buy")){
                // Setting a boolean to buy (true) or sell (false)
                if(command_ls[0].equalsIgnoreCase("buy")){
                    is_buy = true;
                }
                else{
                    is_buy = false;
                }
                if(current_trades.size() > 0){
                    current_trades.clear();
                }
                product_amount = Double.parseDouble(command_ls[3]);
                price = Double.parseDouble(command_ls[4]);
                // Bad values given
                if(product_amount < 0.0 || price < 0.0){
                    System.out.println
                        ("Order could not be placed onto the market.");
                        continue;
                }
                for(Trader trader : traders){
                    if(trader.getID().equals(command_ls[1])){
                        // Trader exists
                        trader_exists = true;
                        if(is_buy == false){
                            // Checking seller has enough of the product
                            if(trader.getAmountStored(command_ls[2]) == -1.0
                            || trader.getAmountStored(command_ls[2])
                            < product_amount){
                                System.out.println
                                ("Order could not be placed onto the market.");
                                break;
                            }
                        }
                        trader_has_item = true;
                        // Creating a hex counter for each order
                        hex_counter = String.format
                        ("%1$04X", order_id_counter);
                        order_id_counter += 1;
                        current_order = new Order
                            (command_ls[2], is_buy, product_amount,
                            price, trader, hex_counter);
                        if(is_buy){
                            // Placing buy order on the market
                            current_trades = market
                                .placeBuyOrder(current_order);
                        }
                        else{
                            for(String product : trader.getProductsInInventory()){
                                if(command_ls[2].equals(product)){
                                    if(product_amount <=
                                        trader.getAmountStored(product)){
                                        // Placing sell order on the market
                                        // Removing amount from inventory
                                        current_trades = market
                                            .placeSellOrder(current_order);
                                    }
                                }
                            }
                        }
                        trader_exists = true;
                    }
                }
                // If trader doesnt exist
                if(trader_exists == false){
                    System.out.println("No such trader in the market.");
                    continue;
                }
                if(trader_has_item == false){
                    continue;
                }
                if(trader_exists){
                    // Discerning how much was sold/bought
                    if(current_order.isClosed()){
                        if(is_buy){
                            System.out.println
                            ("Product bought in entirety, trades as follows:");
                        }
                        else{
                            System.out.println
                            ("Product sold in entirety, trades as follows:");
                        }
                        // Printing each trade
                        for(Trade trade : current_trades){
                            all_trades.add(trade);
                            System.out.println(trade.toString());
                        }
                    }
                    else if(current_trades.size() > 0){
                        if(is_buy){
                            System.out.println
                            ("Product bought in part, trades as follows:");
                        }
                        else{
                            System.out.println
                            ("Product sold in part, trades as follows:");
                        }
                        // Printing each trade
                        for(Trade trade : current_trades){
                            all_trades.add(trade);
                            System.out.println(trade.toString());
                        }
                    }
                    else{
                        if(is_buy){
                            System.out.println
                            ("No trades could be made, order added to buy book.");
                        }
                        else{
                            System.out.println
                            ("No trades could be made, order added to sell book.");
                        }
                    }
                }
            }

            else if(command_ls[0].equalsIgnoreCase("import") ||
                command_ls[0].equalsIgnoreCase("export")){
                    // Setting a boolean to import (true) or export (false)
                if(command_ls[0].equalsIgnoreCase("import")){
                    is_import = true;
                }
                else{
                    is_import = false;
                }
                product_amount = Double.parseDouble(command_ls[3]);
                String product = command_ls[2];
                trader_exists = false;
                for(Trader trader : traders){
                    // If trader doesnt exist
                    if(trader.getID() == null){
                        System.out.println("No such trader in the market");
                        break;
                    }
                    if(trader.getID().equals(command_ls[1])){
                        trader_exists = true;
                        if(is_import){
                            // Making sure values are ok
                            import_result = trader.importProduct
                                (product, product_amount);
                            if(import_result == -1.0){
                                System.out.println
                                ("Could not import product into market.");
                            }
                        }
                        else{
                            // Making sure values are ok
                            export_result = trader.exportProduct
                                (product, product_amount);
                            if(export_result == -1.0){
                                System.out.println
                                ("Could not export product out of market.");
                            }
                        }
                        // If values are ok
                        if((is_import && import_result != -1.0) ||
                            (is_import == false && export_result != -1.0)){
                            System.out.print("Trader now has ");
                            if(trader.getAmountStored(product) > 0){
                                System.out.print(String.format
                                ("%.2f", trader.getAmountStored(product)));
                            }
                            else{
                                System.out.print("no");
                            }
                            System.out.println
                            (" units of " + product + ".");
                        }
                    }
                }
                // If trader doesnt exist
                if(trader_exists == false){
                    System.out.println("No such trader in the market.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("cancel")){
                if(command_ls[1].equalsIgnoreCase("sell")){
                    // If order exists, then cancel it
                    for(Order order : market.sell_book){
                        if(command_ls[2].equals(order.getID())){
                            order.getTrader().importProduct
                                (order.getProduct(), order.getAmount());
                            market.cancelSellOrder(order.getID());
                            order_exists = true;
                            System.out.println
                            ("Order successfully cancelled.");
                            break;
                        }
                    }
                    // If order is not in the sell book
                    if(order_exists == false){
                        System.out.println("No such order in sell book.");
                    }
                }


                else if(command_ls[1].equalsIgnoreCase("buy")){
                    // If order exists, then cancel it
                    for(Order order : market.buy_book){
                        if(command_ls[2].equals(order.getID())){
                            market.cancelBuyOrder(order.getID());
                            order_exists = true;
                            System.out.println
                            ("Order successfully cancelled.");
                            break;
                        }
                    }
                    // If order is not in the buy book
                    if(order_exists == false){
                        System.out.println("No such order in buy book.");
                    }
                }
            }


            else if(command_ls[0].equalsIgnoreCase("order")){
                if(market.buy_book.size() == 0 && market.sell_book.size() == 0){
                    // If there are no active orders
                    System.out.println
                        ("No orders in either book in the market.");
                    order_exists = true;
                }
                // Checking buy and sell book for the order
                for(Order order : market.buy_book){
                    if(command_ls[1].equals(order.getID())){
                        System.out.println(order.toString());
                        order_exists = true;
                    }
                }
                for(Order order : market.sell_book){
                    if(command_ls[1].equals(order.getID())){
                        System.out.println(order.toString());
                        order_exists = true;
                    }
                }
                // If the order is not found
                if(order_exists == false){
                    System.out.println
                        ("Order is not present in either order book.");
                }
            }


            else if(command_ls[0].equalsIgnoreCase("traders")){
                trader_ids = new ArrayList<String>();
                if(trader_ids.size() > 0){
                    trader_ids.clear();
                }
                // If there are no traders in the market
                if(traders.size() == 0){
                    System.out.println("No traders in the market.");
                }
                for(Trader trader : traders){
                    trader_ids.add(trader.getID());
                }
                // Ordering the traders alphabetically and printning
                trader_ids.sort((x, y) -> x.compareTo(y));
                for(String trader_id : trader_ids){
                    System.out.println(trader_id);
                }
            }


            else if(command_ls[0].equalsIgnoreCase("trades")){
                current_trade_list = new ArrayList<Trade>();
                for(Trade trade : all_trades){
                    current_trade_list.add(trade);
                }
                if(command_ls.length == 1){
                    // If no trades have been made
                    if(all_trades.size() == 0){
                        System.out.println("No trades have been completed.");
                        continue;
                    }
                    else{
                        for(Trade trade : all_trades){
                            System.out.println(trade.toString());
                            continue;
                        }
                    }
                }
                else{
                    if(command_ls[1].equalsIgnoreCase("trader")){
                        if(all_trades.size() == 0){
                            // If trader doesnt exist
                            if(traders.size() == 0){
                                System.out.println
                                ("No such trader in the market.");
                            }
                            else{
                                // If trader hasnt made any trades
                                System.out.println
                                ("No trades have been completed by trader.");
                            }
                            continue;
                        }
                        // If trader exists, print out their trades
                        for (Trader trader : traders){
                            if(command_ls[2].equals(trader.getID())){
                                trader_exists = true;
                                List<Trade> trades_by_trader = market.filterTradesByTrader(current_trade_list, trader);
                                if(trades_by_trader != null && trades_by_trader.size() != 0){
                                    trader_has_trades = true;
                                    for(Trade trade : trades_by_trader){
                                        System.out.println(trade.toString());
                                    }
                                }
                            }
                        }
                        if(trader_has_trades == false){
                            System.out.println
                            ("No trades have been completed by trader.");
                        }
                    }



                    else if(command_ls[1].equalsIgnoreCase("product")){
                        if(all_trades.size() == 0){
                            // If no trades have been made
                            System.out.println
                            ("No trades have been completed with given product.");
                            continue;
                        }
                        // If trades have been made with the product, print them
                        List<Trade> trades_by_product = market.filterTradesByProduct(current_trade_list, command_ls[2]);
                        if(trades_by_product != null && trades_by_product.size() != 0){
                            product_has_been_traded = true;
                            for(Trade trade : trades_by_product){
                                System.out.println(trade.toString());
                            }
                        }
                        // If no trades have been made with the product
                        if(product_has_been_traded == false){
                            System.out.println
                            ("No trades have been completed with given product.");
                        }
                    }
                }
            }


            else if(command_ls[0].equalsIgnoreCase("book")){
                is_buy = false;
                if(command_ls[1].equalsIgnoreCase("buy")){
                    is_buy = true;
                }
                if(is_buy){
                    // If buy book is empty
                    if(market.buy_book.size() == 0){
                        System.out.println("The buy book is empty.");
                    }
                    else{
                        // If the buy book is not empty
                        for(Order order : market.buy_book){
                            System.out.println(order.toString());
                        }
                    }
                }
                else{
                    // If sell book is empty
                    if(market.sell_book.size() == 0){
                        System.out.println("The sell book is empty.");
                    }
                    else{
                        // If the sell book is not empty
                        for(Order order : market.sell_book){
                            System.out.println(order.toString());
                        }
                    }
                }
            }


            else if(command_ls[0].equalsIgnoreCase("save") ||
                    command_ls[0].equalsIgnoreCase("binary")){
                if(command_ls[0].equalsIgnoreCase("binary")){
                    is_binary = true;
                }
                traders_ordered.clear();
                trader_ids.clear();
                if(trader_ids.size() > 0){
                    trader_ids.clear();
                }
                for(Trader trader : traders){
                    trader_ids.add(trader.getID());
                }
                // Sort traders by alphabetical order
                trader_ids.sort((x, y) -> x.compareTo(y));
                for(String id : trader_ids){
                    for(Trader trader : traders){
                        if(id.equals(trader.getID())){
                            traders_ordered.add(trader);
                        }
                    }
                }
                try{
                    if(is_binary){
                        // Write to binary with given file paths
                        Trader.writeTradersBinary
                            (traders_ordered, command_ls[1]);
                        Trade.writeTradesBinary(all_trades, command_ls[2]);

                    }
                    else{
                        // Write to file with given file paths
                        Trader.writeTraders(traders_ordered, command_ls[1]);
                        Trade.writeTrades(all_trades, command_ls[2]);
                    }
                    System.out.println("Success.");
                }
                // Any exceptions will print the error
                catch(Exception e){
                    System.out.println("Unable to save logs to file.");
                    continue;
                }
            }


            else{
                System.out.println();
                continue;
            }

        }
    }




    public static void main(String[] args) {
        Exchange exchange = new Exchange();
        exchange.run();
    }
}
