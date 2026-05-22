package backend;

import static backend.Operators.*;
import java.util.*;

/*
    This class can evaluate/represent boolean expressions that is built with
*/
public class BooleanExpression
{
    public static String evaluate(String expression) {
        Stack<String> parens = new Stack<>();
        int startOuterParenIndex = -1;
        int endOuterParenIndex;

        String noParenExp = "";
        String ans;

        if (!expression.contains("(")) {
            ArrayList<String> container = new ArrayList<>();
            String temp = "";

            for (int i = 0; i < expression.length(); i++) {
                // check if there is ANY bool operator here
                for (String op : LanguageConfig.BOOL_OPERATORS) {
                    if (expression.indexOf(op, i) == i) {
                        if (!temp.isEmpty())
                            container.add(temp);
                        container.add(op);
                        temp = "";
                        i += op.length();
                        break;
                    }
                }

                if (expression.charAt(i) != ' ') {
                    temp += expression.charAt(i);
                }
            }

            container.add(temp);

            // at this point, we have a list of everything in between boolean operators and the boolean operators
            // in the same order

            // next, we have to update that list to contain true/false for each comparison expression
            for (int i = 0; i < container.size(); i++) {
                if (!LanguageConfig.isBoolOperator(container.get(i))) {
                    container.set(i, evaluateBooleanTerm(container.get(i)));
                }
            }

            // deal with any NOTs first
            // the not will apply to the next value and reverse it
            // the while loop is to deal with things like "not not true"
            while (container.contains("not")) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("not") && !container.get(i+1).equals("not")) {
                        container.set(i + 1, not(container.get(i + 1)));
                        container.remove(i);
                    }
                }
            }

            // AND has higher precedence than OR, so deal with all ANDs first, then ORs
            while (container.contains("and")) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("and")) {
                        container.set(i, and(container.get(i - 1), container.get(i + 1)));
                        container.remove(i - 1);
                        container.remove(i);

                    }
                }
            }

            while (container.size() > 1) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("or")) {
                        container.set(i, or(container.get(i - 1), container.get(i + 1)));
                        container.remove(i - 1);
                        container.remove(i);
                    }
                }
            }

            return container.get(0);
        } else {
            // contruct an expression with all the parenthesis evaluated
            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);
                if (c == '(') {
                    if (parens.isEmpty()) {
                        startOuterParenIndex = i;
                    }

                    parens.push("(");
                } else if (c == ')') {
                    parens.pop();

                    if (parens.isEmpty()) {
                        endOuterParenIndex = i;
                        noParenExp += BooleanExpression.evaluate(expression.substring(startOuterParenIndex + 1, endOuterParenIndex));
                    }
                } else if (parens.isEmpty() && c != ' ') { // any other character outside the parenthesis that is not a space
                    noParenExp += c;
                }
            }

            ans = BooleanExpression.evaluate(noParenExp);
        }

        return ans;
    }

    private static ArrayList<String> splitComparisonExpression(String expression) {
        for (String op : LanguageConfig.COMPARISON_OPERATORS) {
            if (expression.contains(op)) {
                List<String> ans = Arrays.asList(expression.split(op));
                ArrayList<String> a = new ArrayList<>(ans);
                a.add(1, op); // add the operator in between the 2 splits

                return a;
            }
        }

        // System.out.println("ERROR: UNEXPECTED COMPARISON OPERATOR");
        ScratchError.throwError(-1, "Unexpected comparison operator", -1);
        return null;
    }

    private static String evaluateBooleanTerm(String expression) {
        expression = expression.trim();

        if (expression.equals("true") || expression.equals("false")) {
            return expression;
        }

        if (expression.equals("mouse_down")) {
            System.out.println("mouse down value: " + Interpreter.getMouseDownValue());
            return Interpreter.getMouseDownValue(); // works, but not the best design
        }

        // if (expression.startsWith("key_pressed(")) {
        //     return Interpreter.getKeyPressedValue(expression);
        // }

        return evaluateComparisonExpression(expression);
    }


    private static String evaluateComparisonExpression(String expression) {

        System.out.println("expression " + expression);
        ArrayList<String> exp = splitComparisonExpression(expression);

        exp.set(0, Expression.evaluate(exp.get(0)));
        exp.set(2, Expression.evaluate(exp.get(2)));

        String left = exp.get(0);
        String right = exp.get(2);

        switch (exp.get(1)) { // operator choosing
            case ">":
                return greaterThan(left, right);
            case "<":
                return lessThan(left, right);
            case "==":
                return equal(left, right);
            case "!=":
                return notEqual(left, right);
            default:
                System.out.println("ERROR: UNEXPECTED OPERATOR");
                return null;
        }
    }
}
