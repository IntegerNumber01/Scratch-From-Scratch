package backend;

public class ScratchError {
    public static void throwError(int number, String errorMsg) {
        throwError(number, errorMsg, -1, -1); // call the main throwError method
    }

    public static void throwError(int number, String errorMsg, int col) {
        throwError(number, errorMsg, col, -1); // call the main throwError method  
    }

    public static void throwError(int number, String errorMsg, int col, int length) {
        System.out.println("⚠️  Error on line " + number + ": " + errorMsg);
 
        if (col >= 0) { // if the entire method is wrong, then put carrots for the entire method where the scratch code errors
            int count = 0; // if it doesn't then it'll just put one at a point of the error
            if (length > 0) {
                count = length;
            } else {
                count = 1;
            }
            System.out.println(" ".repeat(col) + "^".repeat(count));
        }

        System.exit(1);
    }
}
// this is a static class that has a throwError() method

// the method takes (line text, line number, error message, OPTIONAL column number)



// it should print the error line text, then do ^^^^^ in teh place that is wrong (using the colum number if there is one)

// before returning, it should call System.exit(1).

/*
Error on line x: move_to(1))
                           ^

Error on line x: Invalid function name: notmove(10)
                                        ^^^^^^^^
*/
