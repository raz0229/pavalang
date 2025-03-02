package interpreter;

import java.util.List;

import interpreter.builtins.*;
import parser.Expr;
import parser.Stmt;
import lexer.Token;
import java.util.ArrayList;

public class Interpreter {

    private Environment environment = new Environment();

    public Interpreter() {
        // Define native/built-in functions
        environment.define("clock", new ClockFunction());
        environment.define("pava", new Pava()); 
        environment.define("typeof", new TypeFunction());
        environment.define("input", new InputFunction());
        environment.define("err", new ErrFunction());
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt stmt : statements) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            throw new RuntimeException(error.getMessage());
            
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(new Stmt.Visitor<Void>() {
            @Override
            public Void visitPrintStmt(Stmt.Print stmt) {
                Object value = evaluate(stmt.expression);
                System.out.println(stringify(value).replaceAll("\"", ""));
                return null;
            }
            @Override
            public Void visitExpressionStmt(Stmt.Expression stmt) {
                evaluate(stmt.expression);
                return null;
            }
            @Override
            public Void visitVarStmt(Stmt.Var stmt) {
                Object value = null;
                if (stmt.initializer != null) {
                    value = evaluate(stmt.initializer);
                }
                environment.define(stmt.name.lexeme, value);
                return null;
            }
            @Override
            public Void visitBlockStmt(Stmt.Block stmt) {
                executeBlock(stmt.statements, new Environment(environment));
                return null;
            }
            @Override
            public Void visitIfStmt(Stmt.If stmt) {
                Object condition = evaluate(stmt.condition);
                if (isTruthy(condition)) {
                    execute(stmt.thenBranch);
                } else if (stmt.elseBranch != null) {
                    execute(stmt.elseBranch);
                }
                return null;
            }
            @Override
            public Void visitWhileStmt(Stmt.While stmt) { // New: while statement.
                while (isTruthy(evaluate(stmt.condition))) {
                    execute(stmt.body);
                }
                return null;
            }
            @Override
            public Void visitFunctionStmt(Stmt.Function stmt) {
                PavaFunction function = new PavaFunction(stmt, environment);
                environment.define(stmt.name.lexeme, function);
                return null;
            }
            @Override
            public Void visitReturnStmt(Stmt.Return stmt) {
                Object value = null;
                if (stmt.value != null) {
                    value = evaluate(stmt.value);
                }
                throw new Return(value);
            }

        });
    }

    public void executeBlock(List<Stmt> statements, Environment newEnv) {
        Environment previous = environment;
        try {
            environment = newEnv;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            environment = previous;
        }
    }

    public Object evaluate(Expr expr) {
        return expr.accept(new Expr.Visitor<Object>() {
            @Override
            public Object visitBinaryExpr(Expr.Binary expr) {
                Object left = evaluate(expr.left);
                Object right = evaluate(expr.right);
                switch (expr.operator.lexeme) {
                    case "+":
                        if (left instanceof Double && right instanceof Double) {
                            return (Double) left + (Double) right;
                        }
                        if (left instanceof String && right instanceof String) {
                            return (String) left + (String) right;
                        }
                        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
                    case "-":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left - (Double) right;
                    case "*":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left * (Double) right;
                    case "/":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left / (Double) right;
                    case ">":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left > (Double) right;
                    case "<":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left < (Double) right;
                    case ">=":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left >= (Double) right;
                    case "<=":
                        checkNumberOperands(expr.operator, left, right);
                        return (Double) left <= (Double) right;
                    case "==":
                        return isEqual(left, right);
                    case "!=":
                        return !isEqual(left, right);
                }
                return null;
            }

            @Override
            public Object visitGroupingExpr(Expr.Grouping expr) {
                return evaluate(expr.expression);
            }

            @Override
            public Object visitLiteralExpr(Expr.Literal expr) {
                return expr.value;
            }

            @Override
            public Object visitUnaryExpr(Expr.Unary expr) {
                Object right = evaluate(expr.right);
                switch (expr.operator.lexeme) {
                    case "-":
                        checkNumberOperand(expr.operator, right);
                        return -(Double) right;
                    case "!":
                        return !isTruthy(right);
                }
                return null;
            }

            @Override
            public Object visitVariableExpr(Expr.Variable expr) {
                return environment.get(expr.name);
            }

            @Override
            public Object visitAssignExpr(Expr.Assign expr) {
                Object value = evaluate(expr.value);
                environment.assign(expr.name, value);
                return value;
            }

            @Override
            public Object visitLogicalExpr(Expr.Logical expr) {
                Object left = evaluate(expr.left);
                if (expr.operator.lexeme.equals("or")) {
                    if (isTruthy(left)) return left;
                    return evaluate(expr.right);
                } else if (expr.operator.lexeme.equals("and")) {
                    if (!isTruthy(left)) return left;
                    return evaluate(expr.right);
                }
                return null;
            }

            @Override
            public Object visitCallExpr(Expr.Call expr) {
                Object callee = evaluate(expr.callee);
                List<Object> arguments = new ArrayList<>();
                for (Expr argument : expr.arguments) {
                    arguments.add(evaluate(argument));
                }
                if (!(callee instanceof PavaCallable)) {
                    throw new RuntimeError(expr.paren, "Can only call functions.");
                }
                PavaCallable function = (PavaCallable) callee;
                
                // Disable it for now as we introduce default values of functions
                // Only check arity if the function reports non-negative arity.
                // if (function.arity() >= 0 && arguments.size() != function.arity()) {
                //     throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
                // }
                return function.call(Interpreter.this, arguments);
            }
        });
    }

    private void checkNumberOperand(lexer.Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(lexer.Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (Boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            double text = (Double) object;
            if (text == (int) text) return String.valueOf((int) text);
            return String.valueOf(text);
        }
        return object.toString();
    }
}
