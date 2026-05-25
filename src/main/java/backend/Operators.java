package backend;

public class Operators {

    // new: used in Expression.java
    /**
     * adds (a + b)
     * @param a
     * @param b
     * @return
     */
    public static String add(String a, String b) {
        return Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
    }

    /**
     * subtracts (a - b)
     * @param a
     * @param b
     * @return
     */ 
    public static String subtract(String a, String b) {
        return Double.toString(Double.parseDouble(a) - Double.parseDouble(b));
    }

    /**
     * multiplies (a * b)
     * @param a
     * @param b
     * @return
     */
    public static String multiply(String a, String b) {
        return Double.toString(Double.parseDouble(a) * Double.parseDouble(b));
    }

    /**
     * divides (a / b)
     * @param a
     * @param b
     * @return
     */
    public static String divide(String a, String b) {
        return Double.toString(Double.parseDouble(a) / Double.parseDouble(b));
    }

    /**
     * modulo (a % b)
     * @param a
     * @param b
     * @return
     */
    public static String modulo(String a, String b) {
        return Double.toString(Double.parseDouble(a) % Double.parseDouble(b));
    }

    /**
     * greater than (a > b)
     * @param a
     * @param b
     * @return
     */
    public static String greaterThan(String a, String b) {
        if (Double.parseDouble(a) > Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * less than (a < b)
     * @param a
     * @param b
     * @return
     */
    public static String lessThan(String a, String b) {
        if (Double.parseDouble(a) < Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * equals (a == b)
     * @param a
     * @param b
     * @return
     */
    public static String equal(String a, String b) {
        if (Double.parseDouble(a) == Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * not equal (a != b)
     * @param a
     * @param b
     * @return
     */
    public static String notEqual(String a, String b) {
        if (Double.parseDouble(a) != Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * not (!a)
     * @param a
     * @return
     */
    public static String not(String a) {
        if (!Boolean.parseBoolean(a)) {
            return "true";
        }
        return "false";
    }

    /**
     * and (a && b)
     * @param a
     * @param b
     * @return
     */
    public static String and(String a, String b) {
        if (Boolean.parseBoolean(a) && Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * or (a || b)
     * @param a
     * @param b
     * @return
     */
    public static String or(String a, String b) {
        if (Boolean.parseBoolean(a) || Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }
}
