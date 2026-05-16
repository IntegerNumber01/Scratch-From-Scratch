package backend;

public class ScratchValue {
    //sets value to whatever is passed in constructor
    Object value  ;
    boolean isScratchBool;

    public ScratchValue(String x)
    {
        try 
        {
            value = Double.parseDouble(x);
        } 
        
        catch (NumberFormatException e) 
        {
            value = x ; 
        }
        this.isScratchBool = false;
    }

    public ScratchValue(String x, boolean b) {
        value = x;
        isScratchBool = b;
    }

    public void setValue(Object o)
    {
        value = o;
    }

    public Object getValue()
    {
        return value;
    }

    // helper method to differentiate between the type Double and String and Boolean
    public boolean isNumber()
    {
        return value instanceof Double ; 
    }

    public boolean isString()
    {
        return value instanceof String ; 
    }

    public double toNumber()
    {
        if (isNumber())
        {
            return (Double) value;
        }
        return 0;
    }

}
