package interpreter;

import java.util.List;
import java.util.ArrayList;
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

    private String evaluateExpression(String expr) {
        expr = expr.trim();
        
        // Handle literals (boolean, nil, numbers, and strings)
        if (expr.equals("true") || expr.equals("false") || expr.equals("nil")) {
            return expr;
        }
        if (expr.matches("-?\\d+\\.0")) { // Convert 10.0 -> 10 and -73.0 -> -73
            return expr.substring(0, expr.length() - 2);
        }
        if (expr.matches("-?\\d+\\.\\d+")) { // Numeric values
            return expr;
        }
        if (!expr.startsWith("(")) { // Simple string literal
            return expr;
        }
        
        // Handle grouped expressions recursively
        if (expr.startsWith("(group ")) {
            String inner = expr.substring(7, expr.length() - 1).trim();
            return evaluateExpression(inner);
        }
        
        // Handle unary NOT (!)
        if (expr.startsWith("(! ")) {
            String inner = expr.substring(3, expr.length() - 1).trim();
            return evaluateExpression(inner).equals("false") ? "true" : "false";
        }
        
        // Handle unary negation (-)
        if (expr.startsWith("(- ")) {
            String inner = expr.substring(3, expr.length() - 1).trim();
            double value = Double.parseDouble(evaluateExpression(inner));
            return value == (int) value ? String.valueOf((int) -value) : String.valueOf(-value);
        }
        
        // Handle binary operations (+, -, *, /, >, >=, ==, !=)
        if (expr.startsWith("(")) {
            String[] parts = expr.substring(1, expr.length() - 1).split(" ", 2);
            String operator = parts[0];
            String operands = parts[1];
            
            Stack<String> stack = new Stack<>();
            for (String token : operands.split(" ")) {
                stack.push(evaluateExpression(token));
            }
            
            return evaluateBinary(operator, stack);
        }
        
        return expr;
    }
    
    private String evaluateBinary(String operator, Stack<String> stack) {
        if (stack.size() < 2) return "error";
        
        String right = stack.pop();
        String left = stack.pop();
        
        switch (operator) {
            case "+": return left + right;
            case "-": {
                double result = Double.parseDouble(left) - Double.parseDouble(right);
                return result == (int) result ? String.valueOf((int) result) : String.valueOf(result);
            }
            case "*": {
                double result = Double.parseDouble(left) * Double.parseDouble(right);
                return result == (int) result ? String.valueOf((int) result) : String.valueOf(result);
            }
            case "/": {
                double result = Double.parseDouble(left) / Double.parseDouble(right);
                return result == (int) result ? String.valueOf((int) result) : String.valueOf(result);
            }
            case "==": return left.equals(right) ? "true" : "false";
            case "!=": return !left.equals(right) ? "true" : "false";
            case ">": return Double.parseDouble(left) > Double.parseDouble(right) ? "true" : "false";
            case ">=": return Double.parseDouble(left) >= Double.parseDouble(right) ? "true" : "false";
        }
        
        return "error";
    }
}