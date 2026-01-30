package Service;

import Model.Sale;
import java.util.List;

public interface SaleServiceInterface {

    Sale createNewSale(String cashierId);

    void saveSale(Sale sale);

    List<Sale> getAllSales();

    double getTotalRevenue();
}
