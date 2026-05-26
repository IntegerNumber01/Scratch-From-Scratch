package backend;

/**
 * This class provides static methods for performing the various operations supported by the Scratch language, such as addition, subtraction, multiplication, division, modulo, comparison operators, and logical operators.
 * Each method takes in the appropriate number of String arguments (which represent numbers or boolean values) and returns a String result.
 */
public class Operators {

    /**
     * adds (a + b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of adding a and b
     */
    public static String add(String a, String b) {
        return Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
    }

    /**
     * subtracts (a - b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of subtracting b from a
     */
    public static String subtract(String a, String b) {
        return Double.toString(Double.parseDouble(a) - Double.parseDouble(b));
    }

    /**
     * multiplies (a * b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of multiplying a and b
     */
    public static String multiply(String a, String b) {
        return Double.toString(Double.parseDouble(a) * Double.parseDouble(b));
    }

    /**
     * divides (a / b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of dividing a by b
     */
    public static String divide(String a, String b) {
        return Double.toString(Double.parseDouble(a) / Double.parseDouble(b));
    }

    /**
     * modulo (a % b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of a modulo b
     */
    public static String modulo(String a, String b) {
        return Double.toString(Double.parseDouble(a) % Double.parseDouble(b));
    }

    /**
     * Greater-than comparison ({@code a > b}).
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of checking whether {@code a} is greater than {@code b}
     */
    public static String greaterThan(String a, String b) {
        if (Double.parseDouble(a) > Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * Less-than comparison ({@code a < b}).
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of checking whether {@code a} is less than {@code b}
     */
    public static String lessThan(String a, String b) {
        if (Double.parseDouble(a) < Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * equals (a == b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of checking if a is equal to b
     */
    public static String equal(String a, String b) {
        if (Double.parseDouble(a) == Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * not equal (a != b)
     * @param a the first operand
     * @param b the second operand
     * @return String result of checking if a is not equal to b
     */
    public static String notEqual(String a, String b) {
        if (Double.parseDouble(a) != Double.parseDouble(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * not (!a)
     * @param a the operand
     * @return String result of checking if a is not true
     */
    public static String not(String a) {
        if (!Boolean.parseBoolean(a)) {
            return "true";
        }
        return "false";
    }

    /**
     * Logical AND ({@code a && b}).
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of checking whether both {@code a} and {@code b} are true
     */
    public static String and(String a, String b) {
        if (Boolean.parseBoolean(a) && Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }

    /**
     * Logical OR ({@code a || b}).
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of checking whether {@code a} or {@code b} is true
     */
    public static String or(String a, String b) {
        if (Boolean.parseBoolean(a) || Boolean.parseBoolean(b)) {
            return "true";
        }
        return "false";
    }
}
