package backend;

import java.util.ArrayList;

/**
 * Resolves function-style operator calls inside an expression string.
 * <p>
 * It evaluates supported operator functions such as {@code round(...)},
 * {@code join(...)}, or {@code touching(...)} and replaces each call with
 * its resulting value. It does not evaluate the full math or boolean
 * expression by itself.
 */
public class OperatorFunctionExpression
{
    /**
     * Evaluates all operator-function calls inside the given expression.
     *
     * @param expression expression that may contain operator functions
     * @param world world used by operators that depend on global state
     * @param sprite current sprite used by sprite-specific operators
     * @return the expression with operator functions replaced by their values
     */
    public static String evaluate(String expression, World world, Sprite sprite) {
        int startFunctionIndex = -1;
        int openParenIndex = -1;
        int closeParenIndex = -1;
        String functionString;
        String ans;
        Command command;

        if (!containsOperatorFunction(expression)) {
            return expression;
        } else {
            // find the first operator function in the expression
            for (int i = 0; i < expression.length(); i++) {
                for (String op : LanguageConfig.FUNCTION_OPERATORS) {
                    if (expression.indexOf(op, i) == i) {
                        if (i + op.length() < expression.length() && expression.charAt(i + op.length()) == '(') {
                            startFunctionIndex = i;
                            openParenIndex = i + op.length();
                            break;
                        }
                    }
                }

                if (startFunctionIndex != -1) {
                    break;
                }
            }

            if (startFunctionIndex == -1 || openParenIndex == -1) {
                return expression;
            }

            closeParenIndex = findMatchingCloseParen(expression, openParenIndex);

            if (closeParenIndex == -1) {
                ScratchError.throwError(-1, "Mismatched parenthesis in operator function expression");
            }

            functionString = expression.substring(startFunctionIndex, closeParenIndex + 1);
            command = Parser.parseCommand(functionString, -1);

            if (command == null) {
                ScratchError.throwError(-1, "Invalid operator function " + functionString);
            }

            // evaluate all nested operator functions inside the args first
            for (int i = 0; i < command.getArgs().size(); i++) {
                command.setArg(i, evaluate(command.getArgs().get(i), world, sprite));
            }

            ans = evaluateOperatorFunction(command, world, sprite);

            expression = expression.substring(0, startFunctionIndex) + ans + expression.substring(closeParenIndex + 1);
            ans = evaluate(expression, world, sprite);
        }

        return ans;
    }

    /**
     * Checks whether an expression contains any supported operator function.
     *
     * @param expression expression to scan
     * @return true if an operator function call is present
     */
    private static boolean containsOperatorFunction(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            for (String op : LanguageConfig.FUNCTION_OPERATORS) {
                if (expression.indexOf(op, i) == i) {
                    if (i + op.length() < expression.length() && expression.charAt(i + op.length()) == '(') {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Finds the matching closing parenthesis for a given opening parenthesis.
     *
     * @param expression expression being scanned
     * @param openParenIndex index of the opening parenthesis
     * @return index of the matching closing parenthesis, or -1 if none exists
     */
    private static int findMatchingCloseParen(String expression, int openParenIndex) {
        int depth = 0;

        for (int i = openParenIndex; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                depth++;
            } else if (expression.charAt(i) == ')') {
                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Evaluates a numeric argument as a math expression and converts it to a double.
     *
     * @param expression numeric argument expression
     * @return numeric value of the expression
     */
    private static double evaluateNumericArg(String expression) {
        return Double.parseDouble(Expression.evaluate(expression));
    }

    /**
     * Evaluates one parsed operator function command.
     *
     * @param command parsed operator function
     * @param world world used by stateful operators
     * @param sprite current sprite context
     * @return evaluated value as a string
     */
    private static String evaluateOperatorFunction(Command command, World world, Sprite sprite) {
        ArrayList<String> temp = command.getArgs();

        switch (command.getName()) {
            case "key_pressed":
                if (temp.size() != 1) {
                    ScratchError.throwError(command.getLineNumber(), "key_pressed operator function requires exactly 1 argument");
                }
                return String.valueOf(world.getKeyPressed(temp.get(0)));

            case "attribute_of_sprite":
                if (temp.size() != 2) {
                    ScratchError.throwError(command.getLineNumber(), "attribute_of_sprite operator function requires exactly 2 arguments");
                }
                return Interpreter.getSpriteAttribute(temp.get(0), temp.get(1), command.getLineNumber());

            case "touching":
                if (temp.size() != 1) {
                    ScratchError.throwError(command.getLineNumber(), "touching operator function requires exactly 1 argument");
                }
                if (sprite == null) {
                    ScratchError.throwError(command.getLineNumber(), "touching operator function requires a current sprite context");
                }
                if (temp.get(0).equals("mouse_pointer")) {
                    return String.valueOf(sprite.isTouchingMouse(world.getMouseX(), world.getMouseY()));
                }
                return String.valueOf(world.isTouching(sprite, temp.get(0)));

            case "pick_random":
                double a = evaluateNumericArg(temp.get(0));
                double b = evaluateNumericArg(temp.get(1));
                return String.valueOf((int) (Math.random() * (b - a + 1) + a));

            case "join":
                return temp.get(0) + temp.get(1);

            case "letter_of":
                return String.valueOf(temp.get(1).charAt((int) evaluateNumericArg(temp.get(0)) - 1));

            case "length_of":
                return String.valueOf(temp.get(0).length());

            case "round":
                return String.valueOf(Math.round(evaluateNumericArg(temp.get(0))));
            case "mod":
                return String.valueOf(evaluateNumericArg(temp.get(0)) % evaluateNumericArg(temp.get(1)));


            // math time
            case "abs":
                return String.valueOf(Math.abs(evaluateNumericArg(temp.get(0))));
            case "floor":
                return String.valueOf(Math.floor(evaluateNumericArg(temp.get(0))));
            case "ceiling":
                return String.valueOf(Math.ceil(evaluateNumericArg(temp.get(0))));
            case "sqrt":
                return String.valueOf(Math.sqrt(evaluateNumericArg(temp.get(0))));
            case "sin":
                return String.valueOf(Math.sin(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "cos":
                return String.valueOf(Math.cos(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "tan":
                return String.valueOf(Math.tan(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "asin":
                return String.valueOf(Math.asin(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "acos":
                return String.valueOf(Math.acos(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "atan":
                return String.valueOf(Math.atan(Math.toRadians(evaluateNumericArg(temp.get(0)))));
            case "ln":
                return String.valueOf(Math.log(evaluateNumericArg(temp.get(0))));
            case "log":
                return String.valueOf(Math.log10(evaluateNumericArg(temp.get(0))));
            case "e^":
                return String.valueOf(Math.exp(evaluateNumericArg(temp.get(0))));
            case "10^":
                return String.valueOf(Math.pow(10, evaluateNumericArg(temp.get(0))));

            default:
                ScratchError.throwError(command.getLineNumber(), "Unknown operator function " + command.getName());
                return "";
        }
    }
}
