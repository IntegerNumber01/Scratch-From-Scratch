package backend;

public class ScratchValue {
    //sets value to whatever is passed in constructor
    private Object value ; 

    public ScratchValue(double num)
    {
        value = num ; 
    }

    public ScratchValue(String word)
    {
        value = word ; 
    }

    public double toNumber()
    {
        if(isNumber())
        {
            return value ; 
        } try {
            return (double)
        }
    }

    // helper method to differentiate between the type Double and String
    public boolean isNumber()
    {
        return value instanceof Double ; 
    }


    
}
