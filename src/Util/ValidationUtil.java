package Util;

public class ValidationUtil {

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3;
    }
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+(\\.\\d+)?");
    }

    public static boolean isValidPrice(String price) {
        try {
            double p = Double.parseDouble(price);
            return p > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String quantity) {
        try {
            int q = Integer.parseInt(quantity);
            return q > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }}