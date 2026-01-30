package Service;


import Model.Product;
import Util.FileUtil;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements ProductServiceInterface {

    private List<Product> products;
    private final List<Runnable> listeners = new ArrayList<>();
    private static final String PRODUCTS_FILE = "Data/products.csv";

    public ProductService() {
        loadProducts();
    }

    private void loadProducts() {
        products = FileUtil.loadProducts(PRODUCTS_FILE);
        if (products.isEmpty()) {
            createSampleProducts();
        }
    }

    private void createSampleProducts() {
        products.add(new Product("P001", "Milk", 2.50, 100, "Dairy"));
        products.add(new Product("P002", "Bread", 1.50, 50, "Bakery"));
        products.add(new Product("P003", "Eggs", 3.00, 200, "Dairy"));
        products.add(new Product("P004", "Apple", 0.50, 300, "Fruits"));
        FileUtil.saveProducts(PRODUCTS_FILE, products);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product getProductById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> results = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    product.getId().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(product);
            }
        }
        return results;
    }

    public void addProduct(Product product) {
        products.add(product);
        FileUtil.saveProducts(PRODUCTS_FILE, products);
        notifyListeners();
    }

    public void updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                FileUtil.saveProducts(PRODUCTS_FILE, products);
                notifyListeners();
                return;
            }
        }
    }

    public void deleteProduct(String productId) {
        products.removeIf(p -> p.getId().equals(productId));
        FileUtil.saveProducts(PRODUCTS_FILE, products);
        notifyListeners();
    }

    public void addChangeListener(Runnable r) {
        listeners.add(r);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try { r.run(); } catch (Exception ignored) {}
        }
    }
}
