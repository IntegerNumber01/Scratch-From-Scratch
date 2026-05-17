package backend;

public class ScratchValue {
    //sets value to whatever is passed in constructor
    String strval ; 
    double  doubleval ; 
    boolean bool;
    boolean isBoolean ; 

    public ScratchValue(String x)
    {
        try 
        {
            doubleval = Double.parseDouble(x);
        } 
        
        catch (NumberFormatException e) 
        {
            strval = x ; 
        }
        
        isBoolean = false ; 
    }

    public ScratchValue(boolean b) 
    {
        bool = b;
        isBoolean = true ; 
    }


    public void setValue(String str) 
    {
        try 
        {
            doubleval = Double.parseDouble(str);
            strval = null;
        } 

        catch (NumberFormatException e) 
        {
            strval = str;
            doubleval = 0.0;
        }
    }


    public Object getValue() 
    {
        if (isBoolean()) return bool;
        if (isString()) return strval;
        return doubleval;
    }

    public boolean isNumber() 
    {
        return !isString() && !isBoolean();
    }    

    public boolean isString() 
    {
        return strval != null;
    }

    public boolean isBoolean()
    {
        return isBoolean ; 
    }


    public double toNumber() 
    {
        if (isNumber()) 
        {
            return doubleval;
        }
        // try parsing the string as a number (Scratch does this)
        try 
        {
            return Double.parseDouble(strval);
        } 
        catch (NumberFormatException e) 
        {
            return 0;
        }
    }

}
