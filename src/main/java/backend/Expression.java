package backend;

import java.util.ArrayList;
import java.util.Stack;
import static backend.Operators.*;

/*
    I know this is super inefficient, but it works
*/
public class Expression
{
    private static boolean isStandaloneMathOperatorToken(String token) {
        return token != null
            && token.length() == 1
            && LanguageConfig.isMathOperator(token.charAt(0));
    }

    /**
     * Evaluates a mathematical expression and returns the result as a string. The expression can ONLY contain numbers, parentheses, and the operators +, -, *, and /.
     * The expression is evaluated by first evaluating the innermost parentheses and then working outwards.
     * The operators are evaluated with the following precedence: * and / > + and -.
     * @param expression the raw expression to evaluate
     * @return String result of evaluating the expression
     */
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
                if (LanguageConfig.isMathOperator(expression.charAt(i))) {
                    if (!temp.isEmpty())
                        container.add(temp);
                    container.add(String.valueOf(expression.charAt(i)));
                    temp = "";
                } else if (expression.charAt(i) != ' ') {
                    temp += expression.charAt(i);
                }
            }

            container.add(temp);

            // at this point, we have a list of numbers and operators in the same order

            // combine negative numbers
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).equals("-")
                    && (i == 0 || isStandaloneMathOperatorToken(container.get(i - 1)))) {
                    container.set(i + 1, "-" + container.get(i + 1));
                    container.remove(i);
                }
            }

            while (container.contains("*") || container.contains("/")) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("*")) {
                        container.set(i, multiply(container.get(i - 1), container.get(i + 1)));
                        container.remove(i - 1);
                        container.remove(i);

                    } else if ( container.get(i).equals("/")) {
                        container.set(i, divide(container.get(i - 1), container.get(i + 1)));
                        container.remove(i - 1);
                        container.remove(i);
                    }
                }
            }

            // at this point, we have a list of numbers with only +/- operators, all * / division has been calculated
            while (container.size() > 1) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("+")) {
                        container.set(i, add(container.get(i - 1), container.get(i + 1)));
                        container.remove(i - 1);
                        container.remove(i);

                    } else if ( container.get(i).equals("-")) {
                        container.set(i, subtract(container.get(i - 1), container.get(i + 1)));
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
                        noParenExp += Expression.evaluate(expression.substring(startOuterParenIndex + 1, endOuterParenIndex));
                    }
                } else if (parens.isEmpty() && c != ' ') { // any other character outside the parenthesis that is not a space
                    noParenExp += c;
                }
            }

            ans = Expression.evaluate(noParenExp);
        }

        return ans;
    }

}
