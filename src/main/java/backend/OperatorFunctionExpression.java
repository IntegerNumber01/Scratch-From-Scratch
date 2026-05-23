package backend;

import java.util.ArrayList;

/*
    This class can evaluate/represent operator functions that are built with
*/
public class OperatorFunctionExpression
{
    public static String evaluate(String expression, World world) {
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
                command.setArg(i, evaluate(command.getArgs().get(i), world));
            }

            ans = evaluateOperatorFunction(command, world);

            expression = expression.substring(0, startFunctionIndex) + ans + expression.substring(closeParenIndex + 1);
            ans = evaluate(expression, world);
        }

        return ans;
    }

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

    private static String evaluateOperatorFunction(Command command, World world) {
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

            case "pick_random":
                double a = Double.parseDouble(temp.get(0));
                double b = Double.parseDouble(temp.get(1));
                return String.valueOf((int) (Math.random() * (b - a + 1) + a));

            case "join":
                return temp.get(0) + temp.get(1);

            case "letter_of":
                return String.valueOf(temp.get(1).charAt((int) Double.parseDouble(temp.get(0)) - 1));

            case "length_of":
                return String.valueOf(temp.get(0).length());

            case "round":
                return String.valueOf(Math.round(Double.parseDouble(temp.get(0))));
            case "mod":
                return String.valueOf(Double.parseDouble(temp.get(0)) % Double.parseDouble(temp.get(1)));


            // math time
            case "abs":
                return String.valueOf(Math.abs(Double.parseDouble(temp.get(0))));
            case "floor":
                return String.valueOf(Math.floor(Double.parseDouble(temp.get(0))));
            case "ceiling":
                return String.valueOf(Math.ceil(Double.parseDouble(temp.get(0))));
            case "sqrt":
                return String.valueOf(Math.sqrt(Double.parseDouble(temp.get(0))));
            case "sin":
                return String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "cos":
                return String.valueOf(Math.cos(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "tan":
                return String.valueOf(Math.tan(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "asin":
                return String.valueOf(Math.asin(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "acos":
                return String.valueOf(Math.acos(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "atan":
                return String.valueOf(Math.atan(Math.toRadians(Double.parseDouble(temp.get(0)))));
            case "ln":
                return String.valueOf(Math.log(Double.parseDouble(temp.get(0))));
            case "log":
                return String.valueOf(Math.log10(Double.parseDouble(temp.get(0))));
            case "e^":
                return String.valueOf(Math.exp(Double.parseDouble(temp.get(0))));
            case "10^":
                return String.valueOf(Math.pow(10, Double.parseDouble(temp.get(0))));

            default:
                ScratchError.throwError(command.getLineNumber(), "Unknown operator function " + command.getName());
                return "";
        }
    }
}
