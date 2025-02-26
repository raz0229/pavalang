package interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.*;

public class Interpreter {
    private final List<String> expressions;

    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    private List<String> extractQuotedStrings(String input) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);
        
        while (matcher.find()) {
            result.add(matcher.group(1)); // Extract content inside quotes
        }
        
        return result;
    }

    public Interpreter(List<String> expressions) {
        this.expressions = expressions;
    }

    public List<String> evaluate() {
        List<String> results = new ArrayList<>();
        
        for (String expression : expressions) {
            results.add(evaluateExpression(expression, false));
        }
        
        return results;
    }

    // The boolean flag "preserveWhiteSpace" is used here as a marker for string operations.
    private String evaluateExpression(String expr, boolean preserveWhiteSpace) {
        
        if (!preserveWhiteSpace)
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
            return evaluateExpression(inner, preserveWhiteSpace);
        }
        
        // Handle unary NOT (!)
        if (expr.startsWith("(! ")) {
            String inner = expr.substring(3, expr.length() - 1).trim();
            String evaluated = evaluateExpression(inner, preserveWhiteSpace);
            return (evaluated.equals("false") || evaluated.equals("nil")) ? "true" : "false";
        }
        
        // Handle binary operations (+, -, *, /, >, >=, <, <=, ==, !=) with proper recursive parsing
        if (expr.startsWith("(")) {
            int spaceIndex = expr.indexOf(" ", 1);
            String operator = expr.substring(1, spaceIndex);
            String operands = expr.substring(spaceIndex + 1, expr.length() - 1).trim();
            List<String> parsedOperands;
            boolean isString = false;
            
            // Handle strings differently if quotes are present.
            // run else case if string and number are
            // being compared
           //if (operands.contains("\"")) {

           // If-block would run only for string concat
           // since character " appears more than two times
           // for parsed string concat
            if (countOccurrences(operands, '\"') > 2) {
                parsedOperands = extractQuotedStrings(operands);
                isString = true;
            } else {
                parsedOperands = parseOperands(operands);
            }

            

            if (parsedOperands.size() != 1)
                return evaluateBinary(operator, parsedOperands, isString);
        }

        // Handle unary negation (-)
        if (expr.startsWith("(- ")) {
            String inner = expr.substring(3, expr.length() - 1).trim();
            double value = Double.parseDouble(evaluateExpression(inner, preserveWhiteSpace));
            System.out.println("handle unary");
            return value == (int) value ? String.valueOf((int) -value) : String.valueOf(-value);
        }
        
        return expr;
        
    }
    
    private List<String> parseOperands(String operands) {
        List<String> parsedOperands = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < operands.length(); i++) {
            char c = operands.charAt(i);
            
            if (c == '(') stack.push(c);
            if (c == ')') stack.pop();
            
            if (c == ' ' && stack.isEmpty()) {
                parsedOperands.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            parsedOperands.add(current.toString().trim());
        }
        return parsedOperands;
    }
    
    // The boolean flag "preserveWhiteSpace" here indicates that we're dealing with string operands.
    private String evaluateBinary(String operator, List<String> operands, boolean preserveWhiteSpace) {
        if (operands.size() < 2) return "error";
        
        String left = evaluateExpression(operands.get(0), preserveWhiteSpace);
        String right = evaluateExpression(operands.get(1), preserveWhiteSpace);

        // If dealing with strings and the operator is "+", perform string concatenation.
        if (preserveWhiteSpace && operator.equals("+")) {
            String result = left;
            for (int i = 1; i < operands.size(); i++) {
                result += evaluateExpression(operands.get(i), preserveWhiteSpace);
            }
            return result;
        }
        
        // Handle equality operators with strict type checking.
        if (operator.equals("==") || operator.equals("!=")) {
            boolean leftIsNumeric = isNumeric(left);
            boolean rightIsNumeric = isNumeric(right);

            boolean result;
            if (leftIsNumeric && rightIsNumeric) {
                double leftNum = Double.parseDouble(left);
                double rightNum = Double.parseDouble(right);
                result = leftNum == rightNum;
            } else if (!leftIsNumeric && !rightIsNumeric) {
                result = left.equals(right);
            } else {
                // different types
                result = false;
            }
            if (operator.equals("==")) {
                return result ? "true" : "false";
            } else { // "!="
                return result ? "false" : "true";
            }
        }
        
        try {
            double leftNum = Double.parseDouble(left);
            double rightNum = Double.parseDouble(right);
            
            switch (operator) {
                case "+": return formatResult(leftNum + rightNum);
                case "-": return formatResult(leftNum - rightNum);
                case "*": return formatResult(leftNum * rightNum);
                case "/": return formatResult(leftNum / rightNum);
                case ">": return leftNum > rightNum ? "true" : "false";
                case "<": return leftNum < rightNum ? "true" : "false";
                case ">=": return leftNum >= rightNum ? "true" : "false";
                case "<=": return leftNum <= rightNum ? "true" : "false";
            }
        } catch (NumberFormatException e) {
            if (operator.equals("+")) {
                return left + right;
            }
        }
        
        // handle errors here
        if (operator.equals("-")) {
            throw new RuntimeException(operands + ": Expected operand type followed by \'-\' to be: NUMBER");
        }
        return "error";
    }
    
    private String formatResult(double result) {
        return result == (int) result ? String.valueOf((int) result) : String.valueOf(result);
    }
    
    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
