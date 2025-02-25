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
            String evaluated = evaluateExpression(inner);
            return (evaluated.equals("false") || evaluated.equals("nil")) ? "true" : "false";
        }
        
        // Handle unary negation (-)
        if (expr.startsWith("(- ")) {
            String inner = expr.substring(3, expr.length() - 1).trim();
            double value = Double.parseDouble(evaluateExpression(inner));
            return value == (int) value ? String.valueOf((int) -value) : String.valueOf(-value);
        }
        
        // Handle binary operations (+, -, *, /, >, >=, ==, !=) with proper recursive parsing
        if (expr.startsWith("(")) {
            int spaceIndex = expr.indexOf(" ", 1);
            String operator = expr.substring(1, spaceIndex);
            String operands = expr.substring(spaceIndex + 1, expr.length() - 1).trim();
            
            List<String> parsedOperands = parseOperands(operands);
            return evaluateBinary(operator, parsedOperands);
        }
        
        return expr;
    }
    
    private List<String> parseOperands(String operands) {
        List<String> parsedOperands = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder current = new StringBuilder();

        for (char c : operands.toCharArray()) {
            if (c == '(') stack.push(c);
            if (c == ')') stack.pop();
            
            if (c == ' ' && stack.isEmpty()) {
                parsedOperands.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) parsedOperands.add(current.toString());
        return parsedOperands;
    }
    
    private String evaluateBinary(String operator, List<String> operands) {
        if (operands.size() < 2) return "error";
        
        String left = evaluateExpression(operands.get(0));
        String right = evaluateExpression(operands.get(1));
        
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
