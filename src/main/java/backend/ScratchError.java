package backend;

/**
 * This class is responsible for handling errors that occur during the parsing and execution of Scratch code. It provides a static method throwError() that takes in the line number, error message, and optionally the column number and length of the error.
 */
public class ScratchError {
    /**
     * calls main throwError method with the given parameters, assumes other parameters are -1
     * @param number line number where the error occurs
     * @param errorMsg error message to display
     */
    public static void throwError(int number, String errorMsg) {
        throwError(number, errorMsg, -1, -1); // call the main throwError method
    }

    /**
     * calls main throwError method with the given parameters, assumes other parameters are -1
     * @param number line number where the error occurs
     * @param errorMsg error message to display
     * @param col column number where the error occurs, used for pointing to the error in the line of code
     */
    public static void throwError(int number, String errorMsg, int col) {
        throwError(number, errorMsg, col, -1); // call the main throwError method
    }

    /**
     * prints the point where the failure occurs in the Scratch code. Printed with a carrot pointing there.
     * If a method is incorrect, it should put carrots under the entirety of the method
     * @param number line number where the error occurs
     * @param errorMsg error message to display
     * @param col column number where the error occurs, used for pointing to the error in the line of code
     * @param length length of the error, used for pointing to the error in the line of code
     */
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
