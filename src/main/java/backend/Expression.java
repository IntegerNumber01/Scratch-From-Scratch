package backend;

import java.util.ArrayList;
import java.util.Stack;

public class Expression
{
    private String expression;

    public Expression(String e) {
        this.expression = e;
    }

    public String evaluate() {
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
                if (container.get(i).equals("-") && (i == 0 || LanguageConfig.isMathOperator(container.get(i - 1).charAt(0)))) {
                    container.set(i + 1, "-" + container.get(i + 1));
                    container.remove(i);
                }
            }

            double t;

            while (container.contains("*") || container.contains("/")) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("*")) {
                        t = Double.parseDouble(container.get(i - 1)) * Double.parseDouble(container.get(i + 1));
                        container.remove(i-1);
                        container.remove(i-1);
                        container.set(i-1, Double.toString(t));

                    } else if ( container.get(i).equals("/")) {
                        t = Double.parseDouble(container.get(i - 1)) / Double.parseDouble(container.get(i + 1));

                        container.remove(i-1);
                        container.remove(i-1);
                        container.set(i-1, Double.toString(t));
                    }
                }
            }

            // at this point, we have a list of numbers with only +/- operators, all * / division has been calculated
            while (container.size() > 1) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).equals("+")) {
                        t = Double.parseDouble(container.get(i - 1)) + Double.parseDouble(container.get(i + 1));
                        container.remove(i-1);
                        container.remove(i-1);
                        container.set(i-1, Double.toString(t));

                    } else if ( container.get(i).equals("-")) {
                        t = Double.parseDouble(container.get(i - 1)) - Double.parseDouble(container.get(i + 1));

                        container.remove(i-1);
                        container.remove(i-1);
                        container.set(i-1, Double.toString(t));
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
                        Expression a = new Expression(expression.substring(startOuterParenIndex + 1, endOuterParenIndex));
                        noParenExp += a.evaluate();
                    }
                } else if (parens.isEmpty() && c != ' ') { // any other character outside the parenthesis that is not a space
                    noParenExp += c;
                }
            }

            Expression b = new Expression(noParenExp);
            ans = b.evaluate();
        }

        return ans;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String toString() {
        return expression.trim();
    }

}
