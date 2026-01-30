package Service;

import Model.User;
import java.util.List;

public interface AuthServiceInterface {
    boolean login(String username, String password);

    void logout();

    User getCurrentUser();

    boolean isAdmin();

    List<User> getAllUsers();

    boolean addCashier(String username, String password, String name);
}
