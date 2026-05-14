package backend;

public class ScratchValue {
    //sets value to whatever is passed in constructor
    Object value  ; 

    public ScratchValue(Object x)
    {
        value = x ; 
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

}
