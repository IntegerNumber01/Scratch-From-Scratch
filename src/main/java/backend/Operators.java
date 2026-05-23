package backend;

public class Operators {


    // new: used in Expression.java

    public static String add(String a, String b) {
        return Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
    }

    public static String subtract(String a, String b) {
        return Double.toString(Double.parseDouble(a) - Double.parseDouble(b));
    }

    public static String multiply(String a, String b) {
        return Double.toString(Double.parseDouble(a) * Double.parseDouble(b));
    }

    public static String divide(String a, String b) {
        return Double.toString(Double.parseDouble(a) / Double.parseDouble(b));
    }

    public static String modulo(String a, String b) {
        return Double.toString(Double.parseDouble(a) % Double.parseDouble(b));
    }

    public static String greaterThan(String a, String b) {
        if (Double.parseDouble(a) > Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    public static String lessThan(String a, String b) {
        if (Double.parseDouble(a) < Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    public static String equal(String a, String b) {
        if (Double.parseDouble(a) == Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    public static String notEqual(String a, String b) {
        if (Double.parseDouble(a) != Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    public static String not(String a) {
        if (!Boolean.parseBoolean(a)) {
            return "true";
        }
        return "false";
    }

    public static String and(String a, String b) {
        if (Boolean.parseBoolean(a) && Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }

    public static String or(String a, String b) {
        if (Boolean.parseBoolean(a) || Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }
}
