package backend;
import java.lang.Math;

public class Operators {
    public static ScratchValue add(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((double)sv1.getValue() + (double)sv2.getValue()));
            return sv3 ; 
        }
        return new ScratchValue(0);
    }

    public static ScratchValue subtract(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((double)sv1.getValue() - (double)sv2.getValue()));
            return sv3 ; 
        }
        return new ScratchValue(0);
    }

    public static ScratchValue multiply(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((double)sv1.getValue() * (double)sv2.getValue()));
            return sv3 ; 
        }
        return new ScratchValue(0);
    }

    public static ScratchValue divide(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((double)sv1.getValue() / (double)sv2.getValue()));
            return sv3 ; 
        }
        return new ScratchValue(0);
    }

    public static ScratchValue modulo(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            ScratchValue sv3 = new ScratchValue(((double)sv1.getValue() % (double)sv2.getValue()));
            return sv3 ; 
        }
        return new ScratchValue(0);
    }
    
    public static ScratchValue randomVal(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if ((double)sv1.getValue() >= (double)sv2.getValue()) {
                ScratchValue sv3 = new ScratchValue(Math.random() * ((double)sv1.getValue() - (double)sv2.getValue() + 1) + (double)sv2.getValue());
                return sv3;
            } else {
                ScratchValue sv3 = new ScratchValue(Math.random() * ((double)sv2.getValue() - (double)sv1.getValue() + 1) + (double)sv1.getValue());
                return sv3;
            }
        }
        return new ScratchValue(0);
    }

    public static boolean greaterThan(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if ((double)sv1.getValue() > (double)sv2.getValue()) {
                return true;
            }   
        }
        return false;
    }

    public static boolean lessThan(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if ((double)sv1.getValue() < (double)sv2.getValue()) {
                return true;
            } 
        }
        return false;
    }

    public static boolean equal(ScratchValue sv1, ScratchValue sv2) {
        if (sv1.isNumber() && sv2.isNumber()) {
            if ((double)sv1.getValue() == (double)sv2.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean and(ScratchValue sv1, ScratchValue sv2) {
        
    }
}
