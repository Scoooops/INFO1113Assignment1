import java.util.List;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class Trade {
    public String product;
    public double amount;
    public double price;
    public Order sellOrder;
    public Order buyOrder;
    public String trade_string;

    public Trade(String product, double amount, double price, Order sellOrder, Order buyOrder) {
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;
    }

    public String getProduct() {
        return this.product;
    }

    public double getAmount() {
        return this.amount;
    }

    public Order getSellOrder() {
        return this.sellOrder;
    }

    public Order getBuyOrder() {
        return this.buyOrder;
    }

    public double getPrice() {
        return this.price;
    }

    public String toString() {
        trade_string = "";
        // Getting details about the trade
        String string_amount = String.format("%.2f", this.amount);
        String string_price = String.format("%.2f", this.price);
        // Concatenating the string
        trade_string += this.sellOrder.getTrader().getID() + "->" +
                        this.buyOrder.getTrader().getID() + ": " + string_amount
                        + "x" + this.product + " for $" + string_price + ".";
        return trade_string;
    }

    public boolean involvesTrader(Trader trader) {
        if(trader == sellOrder.getTrader() || trader == buyOrder.getTrader()){
            return true;
        }
        else{
            return false;
        }
    }

    public static void writeTrades(List<Trade> trades, String path) {
        if(trades == null || trades.size() == 0){
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream output = new DataOutputStream(f);
            for(Trade trade : trades){
                String this_trade = trade.toString();
                output.writeBytes(this_trade+"\n");
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

    public static void writeTradesBinary(List<Trade> trades, String path) {
        if(trades == null || trades.size() == 0){
            return;
        }
        try{
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream output = new DataOutputStream(f);
            for(Trade trade : trades){
                String trade_string = trade.toString();
                output.writeUTF(trade_string);
                output.writeUTF("\u001F");
            }
            output.close();
        }
        catch(FileNotFoundException e){
            return;
        }
        catch(IOException e){
            return;
        }
    }
}
