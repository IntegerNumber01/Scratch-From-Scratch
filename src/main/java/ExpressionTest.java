import backend.Expression;
import backend.BooleanExpression;

public class ExpressionTest
{
    public static void main(String[] args) {
        String exp;
        // String exp = "5 + 4 + 19/2";
        // String exp = "5*8/2";
        exp = "((((((1+2)*(3+4))/((5+6)-(7/8)))+(((9*10)+(11/12))-((13+14)/(15-16/17))))*((((18+19)/(20+21))*((22-23/24)+(25*26)))-(((27+28*29)/(30+31))-((32/33)+(34*35)))))/(((((36+37)*(38-39/40))+((41/42)+(43*44)))-(((45+46)/(47+48))*((49-50/51)+(52*53))))+((((54*55)/(56+57))-((58+59)*(60/61)))+(((62+63)/(64-65/66))*((67*68)+(69/70))))))";
        // exp += "+  " + exp;
        // exp += "+  " + exp;
        // exp += "+  " + exp;

        // Expression a = new Expression("((1+2)*2) + 4+5 / (8+9)");
        // exp = "(20+21)*((22-23/24)+(25*26))";
        // exp = "(100-20)*-2";
        // exp = "10";
        // Expression a  = new Expression(exp);
        // String a = "-10";

        // System.out.println(Double.parseDouble(a));

        // System.out.println(Expression.evaluate(exp));

        exp = "10 != 5 and 5 > 3 or 2 < 1";
        exp = "not not 10!=1 and 5 > 3 or 2 < 1";
        exp = "10> 5 or 10==11 and 20> 30";

        System.out.println(BooleanExpression.evaluate(exp));
    }
}
