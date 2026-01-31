package Service;

import Model.User;
import Model.Admin;
import Model.Cashier;
import Util.FileUtil;
import java.util.List;
import java.io.File;

public class AuthService implements AuthServiceInterface {

    private List<User> users;
    private User currentUser;
    private static final String USERS_DIR = System.getProperty("user.home") + "/OneDrive/Desktop/Data/";
    private static final String USERS_FILE = USERS_DIR + "/users.csv";


    public AuthService() {
        ensureDataDir();
        loadUsers();
    }

    private void ensureDataDir() {
        File dir = Util.FileUtil.resolve(USERS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void loadUsers() {
        users = FileUtil.loadUsers(USERS_FILE);
        if (users.isEmpty()) {
            createDefaultUsers();
        }
    }

    @Override
    public java.util.List<User> getAllUsers() {
        return users;
    }

    @Override
    public boolean addCashier(String username, String password, String name) {

        for (User u : users) {
            if (u.getUsername().equals(username)) return false;
        }
        // Generate simple ids
        int next = users.size() + 1;
        String id = String.format("U%03d", next);
        String empId = String.format("EMP%03d", next);
        Cashier cashier = new Cashier(id, username, password, name, empId);
        users.add(cashier);
        FileUtil.saveUsers(USERS_FILE, users);
        return true;
    }

    private void createDefaultUsers() {
        users.add(new Admin("001", "admin", "admin123", "System Admin"));
        users.add(new Cashier("002", "cashier", "1234", "John Cashier", "EMP001"));
        FileUtil.saveUsers(USERS_FILE, users);
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.authenticate(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }
}
