import java.util.List;
import java.util.ArrayList;

public class Order {
    public String product;
    public boolean buy;
    public double amount;
    public double price;
    public Trader trader;
    public String id;
    public String[] current_order;
    public boolean is_closed;
    public String string_buy;

    public Order(String product, boolean buy, double amount, double price, Trader trader, String id) {
        this.product = product;
        this.buy = buy;
        this.amount = amount;
        this.price = price;
        this.trader = trader;
        this.id = id;
        is_closed = false;
    }

    public String getProduct() {
        return this.product;
    }

    public boolean isBuy() {
        return this.buy;
    }

    public double getAmount() {
        return this.amount;
    }

    public Trader getTrader() {
        return this.trader;
    }

    public void close() {
        is_closed = true;
    }

    public boolean isClosed() {
        return is_closed;
    }

    public double getPrice() {
        return this.price;
    }

    public String getID() {
        return this.id;
    }

    public void adjustAmount(double change) {
        if(change < 0){
            if(change > this.amount){
                return;
            }
            this.amount += change;
            return;
        }
        this.amount += change;
        return;
    }

    public String toString() {
        String order_string = "";
        if(this.buy){
            string_buy = "BUY";
        }
        else{
            string_buy = "SELL";
        }
        String string_amount = String.format("%.2f", this.amount);
        String string_price = String.format("%.2f", this.price);

        order_string += this.id + ": " + string_buy + " " + string_amount
        + "x" + this.product + " @ $" + string_price;
        return order_string;
    }

}
