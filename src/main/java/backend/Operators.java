package backend;
import java.lang.Math;

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

    // public static String not(String a) {
    //     if (!Boolean.parseBoolean(a)) {
    //         return "true";
    //     }
    //     return "false";
    // }


    //


    private static double num(ScratchValue sv)
    {
        return sv.toNumber() ;
    }

    public static ScratchValue add(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue((num(sv1) + num(sv2)) + "");
            return sv3 ;
        }
        return new ScratchValue("0");
    }

    public static ScratchValue subtract(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(num(sv1) - num(sv2) + "");
            return sv3 ;
        }
        return new ScratchValue("0");
    }

    public static ScratchValue multiply(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((num(sv1) * num(sv2)) + ""));
            return sv3 ;
        }
        return new ScratchValue("0");
    }

    public static ScratchValue divide(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue((num(sv1) / num(sv2)) + "");
            return sv3 ;
        }
        return new ScratchValue("0");
    }

    public static ScratchValue modulo(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue((num(sv1) % num(sv2)) + "");
            return sv3 ;
        }
        return new ScratchValue("0");
    }

    public static ScratchValue randomVal(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if (num(sv1) >= num(sv2)) {
                ScratchValue sv3 = new ScratchValue((Math.random() * (num(sv1) - num(sv2) + 1) + num(sv2)) + "");
                return sv3;
            } else {
                ScratchValue sv3 = new ScratchValue((Math.random() * (num(sv2) - num(sv1) + 1) + num(sv1)) + "");
                return sv3;
            }
        }
        return new ScratchValue("0");
    }

    public static ScratchValue greaterThan(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if (num(sv1) > num(sv2)) {
                return new ScratchValue(true);
            }
        }
        return new ScratchValue(false) ;
    }


    // SCRATCH DOENS'T UNDERSTAND CASED LETTERS, ANY STRING WILL ALWAYS BE GREATER THAN ANY NUMBER
    public static ScratchValue lessThan(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if (num(sv1) < num(sv2)) {
                return new ScratchValue(true);
            }
        }
        return new ScratchValue(false) ;
    }

    // WHAT ABOUT STRINGS THAT ARE EQUAL
    public static ScratchValue equal(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if (num(sv1) == num(sv2)) {
                return new ScratchValue(true);
            }
        }
        return new ScratchValue(false) ;
    }

    public static ScratchValue and(ScratchValue sv1, ScratchValue sv2)
    {
        if(sv1.isBoolean() && sv2.isBoolean())
        {
            return new ScratchValue(sv1.bool && sv2.bool) ;
        }

        return new ScratchValue(false) ;
    }

    public static ScratchValue or(ScratchValue sv1, ScratchValue sv2)
    {
        if(sv1.isBoolean() && sv2.isBoolean())
        {
            return new ScratchValue(sv1.bool || sv2.bool) ;
        }

        return new ScratchValue(false) ;
    }

    public static ScratchValue not(ScratchValue sv1)
    {
        if(sv1.isBoolean())
        {
            return new ScratchValue(!sv1.bool) ;
        }

        return new ScratchValue(false) ;
    }
}
