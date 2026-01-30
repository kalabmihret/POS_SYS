package Model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Sale {

    private String saleId;
    private String cashierId;
    private List<Product> products;
    private List<Integer> quantities;
    private double totalAmount;
    private LocalDateTime dateTime;

    public Sale(String saleId, String cashierId) {
        this.saleId = saleId;
        this.cashierId = cashierId;
        this.products = new ArrayList<>();
        this.quantities = new ArrayList<>();
        this.totalAmount = 0.0;
        this.dateTime = LocalDateTime.now();
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void addProduct(Product product, int quantity) {
        products.add(product);
        quantities.add(quantity);
        totalAmount += product.getPrice() * quantity;
        product.reduceQuantity(quantity);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getSaleId() { return saleId; }

    public String getCashierId() { return cashierId; }

    public LocalDateTime getDateTime() { return dateTime; }

    public List<Product> getProducts() { return products; }

    public List<Integer> getQuantities() { return quantities; }

    @Override
    public String toString() {
        return saleId + "," + cashierId + "," + totalAmount + "," +
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
