package Model;

public class Cashier extends User {

    private String employeeId;

    public Cashier(String id, String username, String password, String name, String employeeId) {
        super(id, username, password, name);
        this.employeeId = employeeId;
    }

    @Override
    public String getRole() {
        return "CASHIER";
    }

    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public String toString() {
        return super.toString() + "," + employeeId;
    }
}