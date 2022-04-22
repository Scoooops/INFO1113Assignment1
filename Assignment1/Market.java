import java.util.List;
import java.util.ArrayList;

public class Market {
    public static List<Order> buy_book;
    public static List<Order> sell_book;
    public int best_offer_index;
    public Order best_offer;
    public List<Trade> completed_trades;
    public List<Trade> total_trades;
    public int trades_made;
    public boolean products_match;

    public Market() {
        buy_book = new ArrayList<Order>();
        sell_book = new ArrayList<Order>();
        completed_trades = new ArrayList<Trade>();
        total_trades = new ArrayList<Trade>();
    }

    // A function to handle changing trader values (balances and amounts)
    public void append_trader_values(Order order1, Order order2,
        double order_amount, String order_product, double sale_total){
        order1.getTrader().adjustBalance(sale_total);
        order2.getTrader().adjustBalance(0.0 - sale_total);
        order2.getTrader().importProduct(order_product, order_amount);
    }

    // A function to handle a trader where the offer amount is greater
    // than the current amount of the order
    public List<Trade> offer_amount_greater_than_order(List<Trade>
        completed_trades, Order order1, Order order2, List<Order> book1,
        List<Order> book2, double order_price, String order_product,
        double order_amount, boolean is_buy){
        double sale_total = order_price*order_amount;
        Trade new_trade = new Trade(order1.getProduct(), order_amount,
            order_price, order1, order2);
        append_trader_values
            (order1, order2, order_amount, order_product, sale_total);
        if(is_buy){
            if(order1.getAmount() == order2.getAmount()){
                // If order amounts are equal, close the sell order as well
                order1.close();
                book2.remove(order1);
            }
            // Close the buy order
            order1.adjustAmount(0.0 - order2.getAmount());
            order2.close();
            book1.remove(order2);
        }
        else{
            if(order2.getAmount() == order1.getAmount()){
                // If order amounts are equal, close the sell roder as well
                order2.close();
                book2.remove(order2);
            }
            // Close the sell order
            order2.adjustAmount(0.0 - order1.getAmount());
            order1.close();
            book1.remove(order1);
        }
        completed_trades.add(new_trade);
        total_trades.add(new_trade);
        return completed_trades;
    }

    // A function to handle a trader where the offer amount is less
    // than the current amount of the order
    public List<Trade> offer_amount_lower_than_order(List<Trade>
    completed_trades, List<Order> book, Order order1, Order order2,
    double order_price, String order_product, double order_amount){
        double sale_total = order_price*order_amount;
        Trade new_trade = new Trade(order1.getProduct(), order_amount,
            order_price, order1, order2);
        append_trader_values(order1, order2, order_amount, order_product,
            sale_total);
        completed_trades.add(new_trade);
        total_trades.add(new_trade);
        return completed_trades;
    }



    public List<Trade> placeSellOrder(Order order) {
        trades_made = 0;
        if(order == null || order.isBuy() == true){
            return null;
        }
        completed_trades.clear();
        while(true){
            products_match = false;
            double highest_price = 0.0;
            // If its the first time in the loop, take the amount from the seller
            if(trades_made == 0){
                order.getTrader().exportProduct
                    (order.getProduct(), order.getAmount());
            }
            if(buy_book.size() == 0){
                sell_book.add(order);
                break;
            }
            // Finding the highest price and the earliest order
            for(Order buy_order : buy_book){
                if((buy_order.getPrice() > highest_price) &&
                    (buy_order.getPrice() >= order.getPrice())
                    && (buy_order.getProduct().equals(order.getProduct()))){
                    best_offer_index = buy_book.indexOf(buy_order);
                    highest_price = buy_order.getPrice();
                }
            }
            // If no buy order is offering a good price
            if(highest_price == 0.0){
                if(order.isClosed() == false){
                    sell_book.add(order);
                    break;
                }
            }
            best_offer = buy_book.get(best_offer_index);
            if(best_offer.getProduct().equals(order.getProduct())){
                products_match = true;
                String order_product = order.getProduct();
                double order_price = best_offer.getPrice();
                trades_made += 1;
                // If the amount offered is greater than or equal to
                if(best_offer.getAmount() >= order.getAmount()){
                    double order_amount = order.getAmount();
                    offer_amount_greater_than_order(completed_trades, order,
                        best_offer, sell_book, buy_book,
                        order_price, order_product, order_amount, false);
                    return completed_trades;
                }
                else{
                    // If the amount offered is less
                    double order_amount = best_offer.getAmount();
                    offer_amount_lower_than_order(completed_trades,
                        buy_book, order, best_offer, order_price,
                        order_product, order_amount);
                    order.adjustAmount(0.0 - best_offer.getAmount());
                    best_offer.close();
                    buy_book.remove(best_offer);
                }
            }
            if(products_match == false){
                return completed_trades;
            }
        }
        return completed_trades;
    }

    public List<Trade> placeBuyOrder(Order order) {
        if(order == null || order.isBuy() == false){
            return null;
        }
        completed_trades.clear();
        while(true){
            products_match = false;
            double lowest_price = 100000000.0;
            if(sell_book.size() == 0){
                buy_book.add(order);
                break;
            }
            // Finding the lowest price and the earliest order
            for(Order sell_order : sell_book){
                if((sell_order.getPrice() < lowest_price) &&
                    (sell_order.getPrice() <= order.getPrice())
                    && (sell_order.getProduct().equals(order.getProduct()))){
                    best_offer_index = sell_book.indexOf(sell_order);
                    lowest_price = sell_order.getPrice();
                }
            }
            if(lowest_price == 100000000.0){
                buy_book.add(order);
                break;
            }
            best_offer = sell_book.get(best_offer_index);
            if(best_offer.getProduct().equals(order.getProduct())){
                products_match = true;
                String order_product = order.getProduct();
                double order_price = best_offer.getPrice();
                trades_made += 1;
                // If the amount offered is greater than or equal to
                if(best_offer.getAmount() >= order.getAmount()){
                    double order_amount = order.getAmount();
                    offer_amount_greater_than_order(completed_trades,
                        best_offer, order, buy_book,
                        sell_book, order_price, order_product,
                        order_amount, true);
                    return completed_trades;
                }
                else{
                    // If the amount offered is less
                    double order_amount = best_offer.getAmount();
                    offer_amount_lower_than_order(completed_trades,
                        sell_book, best_offer, order, order_price,
                        order_product, order_amount);
                    order.adjustAmount(0.0 - best_offer.getAmount());
                    best_offer.close();
                    sell_book.remove(best_offer);
                }
            }
            if(products_match == false){
                return completed_trades;
            }
        }
        return completed_trades;
    }

    public boolean cancelBuyOrder(String order) {
        if(order == null){
            return false;
        }
        // Remove the order from the buy book
        for(Order current_order : buy_book){
            if(current_order.getID().equals(order)){
                current_order.close();
                buy_book.remove(current_order);
                return true;
            }
        }
        return false;
    }

    public boolean cancelSellOrder(String order) {
        if(order == null){
            return false;
        }
        // Remove the order from the sell book
        for(Order current_order : sell_book){
            if(current_order.getID().equals(order)){
                current_order.close();
                sell_book.remove(current_order);
                return true;
            }
        }
        return false;
    }

    public List<Order> getSellBook() {
        return sell_book;
    }

    public List<Order> getBuyBook() {
        return buy_book;
    }

    public List<Trade> getTrades() {
        return total_trades;
    }

    public static List<Trade> filterTradesByTrader(List<Trade> trades,
        Trader trader) {
        if(trades == null || trader == null){
            return null;
        }
        // If the trader has made the trade, add it to the list
        List<Trade> trades_by_trader = new ArrayList<Trade>();
        for(Trade trade : trades){
            if(trade.involvesTrader(trader)){
                trades_by_trader.add(trade);
            }
        }
        return trades_by_trader;
    }

    public static List<Trade> filterTradesByProduct(List<Trade> trades,
        String product) {
        if(trades == null || product == null){
            return null;
        }
        // If the trade has been made with the product, add it to the list
        List<Trade> trades_by_product = new ArrayList<Trade>();
        trades_by_product.clear();
        for(Trade trade : trades){
            if(trade.getProduct().equals(product)){
                trades_by_product.add(trade);
            }
        }
        return trades_by_product;
    }
}
