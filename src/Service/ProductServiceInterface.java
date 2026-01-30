package Service;

import Model.Product;
import java.util.List;

public interface ProductServiceInterface {

    List<Product> getAllProducts();

    Product getProductById(String id);

    List<Product> searchProducts(String keyword);

    void addProduct(Product product);

    void updateProduct(Product updatedProduct);

    void deleteProduct(String productId);

    void addChangeListener(Runnable r);
}
