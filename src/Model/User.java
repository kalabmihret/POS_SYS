package Model;

public abstract class User {
    private String id;
    private String username;
    private String password;
    private String name;
    public User(String id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }
    public abstract String getRole();

    public String getId() { return id; }

    public String getUsername() { return username; }

    public String getName() { return name; }

    public String getPassword() { return password; }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return id + "," + username + "," + password + "," + name + "," + getRole();
    }
}
