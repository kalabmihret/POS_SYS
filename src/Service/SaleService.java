package Service;

import Model.Sale;
import Util.FileUtil;
import java.util.ArrayList;
import java.util.List;

public class SaleService implements SaleServiceInterface {

    private List<Sale> sales;
    private int saleCounter = 1;

    private static final String SALES_FILE = "Data/sales.csv";

    public SaleService() {
        loadSales();
    }

    private void loadSales() {
        sales = FileUtil.loadSales(SALES_FILE);
    }

    public Sale createNewSale(String cashierId) {
        String saleId = "SALE" + String.format("%04d", saleCounter++);
        return new Sale(saleId, cashierId);
    }

    public void saveSale(Sale sale) {
        sales.add(sale);
        FileUtil.saveSales(SALES_FILE, sales);
    }

    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Sale sale : sales) {
            total += sale.getTotalAmount();
        }
        return total;
    }
}
