package interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Interpreter {
    private final List<String> expressions;

    public Interpreter(List<String> expressions) {
        this.expressions = expressions;
    }

    public List<String> evaluate() {
        List<String> results = new ArrayList<>();
        for (String expression : expressions) {
            results.add(evaluateExpression(expression));
        }
        return results;
    }

    private String evaluateExpression(String expression) {
        expression = expression.trim();

        if (expression.startsWith("(") && expression.endsWith(")")) {
            // Remove outermost parentheses and evaluate inside
            return evaluateExpression(expression.substring(1, expression.length() - 1));
        }

        // Handle literals (true, false, nil, numbers, and strings)
        if (expression.equals("true") || expression.equals("false") || expression.equals("nil")) {
            return expression;
        }

        if (expression.startsWith("\"") && expression.endsWith("\"")) {
            return expression.substring(1, expression.length() - 1); // Remove quotes
        }

        // Handle numbers (integers and floats)
        if (expression.matches("-?\\d+(\\.\\d+)?")) {
            if (expression.contains(".")) {
                return String.valueOf(Double.parseDouble(expression)); // Preserve floating point precision
            } else {
                return String.valueOf(Integer.parseInt(expression)); // Remove unnecessary decimals
            }
        }

        // Handle unary operators (- and !)
        if (expression.startsWith("-")) {
            String value = evaluateExpression(expression.substring(1));
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                return String.valueOf(-Double.parseDouble(value));
            }
        }

        if (expression.startsWith("!")) {
            String value = evaluateExpression(expression.substring(1));
            if (value.equals("true")) return "false";
            if (value.equals("false")) return "true";
            return "false"; // Anything non-boolean is treated as "true"
        }

        // Handle binary operations using a stack-based evaluation
        return evaluateBinaryExpression(expression);
    }

    private String evaluateBinaryExpression(String expression) {
        Stack<Double> numberStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        Stack<String> stringStack = new Stack<>();

        String[] tokens = expression.split(" ");
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            if (token.matches("-?\\d+(\\.\\d+)?")) {
                numberStack.push(Double.parseDouble(token));
            } else if (token.startsWith("\"") && token.endsWith("\"")) {
                stringStack.push(token.substring(1, token.length() - 1));
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    evaluateTopOperator(numberStack, stringStack, operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                return "Invalid Expression: " + expression;
            }
        }

        while (!operatorStack.isEmpty()) {
            evaluateTopOperator(numberStack, stringStack, operatorStack.pop());
        }

        if (!stringStack.isEmpty()) {
            return stringStack.pop();
        } else if (!numberStack.isEmpty()) {
            double result = numberStack.pop();
            if (result % 1 == 0) {
                return String.valueOf((int) result); // Convert to integer if no decimal part
            } else {
                return String.valueOf(result);
            }
        }

        return "Invalid Expression: " + expression;
    }

    private void evaluateTopOperator(Stack<Double> numberStack, Stack<String> stringStack, String operator) {
        if (operator.equals("+")) {
            if (!stringStack.isEmpty()) {
                String b = stringStack.pop();
                String a = stringStack.isEmpty() ? "" : stringStack.pop();
                stringStack.push(a + b);
            } else {
                double b = numberStack.pop();
                double a = numberStack.pop();
                numberStack.push(a + b);
            }
        } else if (operator.equals("-")) {
            double b = numberStack.pop();
            double a = numberStack.pop();
            numberStack.push(a - b);
        } else if (operator.equals("*")) {
            double b = numberStack.pop();
            double a = numberStack.pop();
            numberStack.push(a * b);
        } else if (operator.equals("/")) {
            double b = numberStack.pop();
            double a = numberStack.pop();
            numberStack.push(a / b);
        } else if (operator.equals(">")) {
            double b = numberStack.pop();
            double a = numberStack.pop();
            stringStack.push(a > b ? "true" : "false");
        } else if (operator.equals(">=")) {
            double b = numberStack.pop();
            double a = numberStack.pop();
            stringStack.push(a >= b ? "true" : "false");
        } else if (operator.equals("==")) {
            if (!stringStack.isEmpty()) {
                String b = stringStack.pop();
                String a = stringStack.isEmpty() ? "" : stringStack.pop();
                stringStack.push(a.equals(b) ? "true" : "false");
            } else {
                double b = numberStack.pop();
                double a = numberStack.pop();
                stringStack.push(a == b ? "true" : "false");
            }
        } else if (operator.equals("!=")) {
            if (!stringStack.isEmpty()) {
                String b = stringStack.pop();
                String a = stringStack.isEmpty() ? "" : stringStack.pop();
                stringStack.push(!a.equals(b) ? "true" : "false");
            } else {
                double b = numberStack.pop();
                double a = numberStack.pop();
                stringStack.push(a != b ? "true" : "false");
            }
        }
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") ||
               token.equals(">") || token.equals(">=") || token.equals("==") || token.equals("!=");
    }

    private int precedence(String operator) {
        if (operator.equals("*") || operator.equals("/")) return 2;
        if (operator.equals("+") || operator.equals("-")) return 1;
        return 0;
    }
}
