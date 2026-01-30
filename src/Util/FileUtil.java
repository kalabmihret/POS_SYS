package Util;


import Model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final File BASE_DIR = new File(System.getProperty("user.home") + "/OneDrive/Desktop/possystem 1/");

    static {
        if (!BASE_DIR.exists()) {
            BASE_DIR.mkdirs();
        }
    }

    private static File resolveFile(String filename) {
        File f = new File(filename);
        if (f.isAbsolute()) return f;
        String rel = filename.replace("/", File.separator).replace("\\", File.separator);
        return new File(BASE_DIR, rel);
    }

    public static File resolve(String filename) {
        return resolveFile(filename);
    }

    public static List<User> loadUsers(String filename) {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resolveFile(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String name = parts[3];
                    String role = parts[4];

                    if (role.equals("ADMIN")) {
                        users.add(new Admin(id, username, password, name));
                    } else if (role.equals("CASHIER")) {
                        String employeeId = parts.length > 5 ? parts[5] : "";
                        users.add(new Cashier(id, username, password, name, employeeId));
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return users;
    }

    public static void saveUsers(String filename, List<User> users) {
        try {
            File file = resolveFile(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (User user : users) {
                    pw.println(user.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Product> loadProducts(String filename) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resolveFile(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int quantity = Integer.parseInt(parts[3]);
                    String category = parts[4];

                    products.add(new Product(id, name, price, quantity, category));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return products;
    }

    public static void saveProducts(String filename, List<Product> products) {
        try {
            File file = resolveFile(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (Product product : products) {
                    pw.println(product.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Sale> loadSales(String filename) {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resolveFile(filename)))) {
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String saleId = parts[0];
                    String cashierId = parts[1];
                    double total = Double.parseDouble(parts[2]);
                    LocalDateTime dateTime = LocalDateTime.parse(parts[3], formatter);

                    Sale sale = new Sale(saleId, cashierId);
                    sale.setTotalAmount(total);
                    sale.setDateTime(dateTime);
                    // Note: Products in sale are not loaded from CSV for simplicity
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return sales;
    }


    public static void saveSales(String filename, List<Sale> sales) {
        try {
            File file = resolveFile(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (Sale sale : sales) {
                    pw.println(sale.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}