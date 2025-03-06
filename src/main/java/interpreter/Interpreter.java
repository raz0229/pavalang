package interpreter;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import interpreter.builtins.*;
import parser.*;
import lexer.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Interpreter {

    private Environment environment = new Environment();
    private final Map<String, Module> modules = new HashMap<>();

    public Interpreter() {
        // Define native/built-in functions
        environment.define("clock", new ClockFunction());
        environment.define("pava", new Pava());
        environment.define("typeof", new TypeFunction());
        environment.define("input", new InputFunction());
        environment.define("err", new ErrFunction());
        environment.define("string", new StringFunction());
        environment.define("number", new NumberFunction());
        environment.define("shell", new ShellFunction());
        environment.define("length", new LengthFunction());
        environment.define("exit", new ExitFunction());
        environment.define("getAsciiCode", new GetAsciiCodeFunction());
        environment.define("fromAsciiCode", new FromAsciiCodeFunction());
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
                System.out.print(stringify(value));
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

            @Override
            public Void visitImportStmt(Stmt.Import stmt) {
                // Extract the module path (remove quotes if necessary).
                String path = stmt.path.literal.toString();
                if (path.startsWith("\"") && path.endsWith("\"")) {
                    path = path.substring(1, path.length() - 1);
                }
                // Append ".pava" if not present.
                if (!path.endsWith(".pava")) {
                    path = path + ".pava";
                }

                // Attempt to find the module in /usr/share/pava first, then in the
                // working directory.

                // workaround for snap package
                String libraryBaseDir = System.getenv("PAVA_LIB_DIR");
                if (libraryBaseDir == null || libraryBaseDir.isEmpty()) {
                    // Fallback to the original hardcoded path (optional)
                    libraryBaseDir = "/usr/share/pava";
                }

                java.nio.file.Path modulePath = java.nio.file.Path.of(libraryBaseDir, path);
                if (!java.nio.file.Files.exists(modulePath)) {
                    modulePath = java.nio.file.Path.of(path);
                    if (!java.nio.file.Files.exists(modulePath)) {
                        throw new RuntimeException("Module file not found: " + path);
                    }
                }

                String modulePathStr = modulePath.toString();

                // Check module cache.
                Module module;
                if (modules.containsKey(modulePathStr)) {
                    module = modules.get(modulePathStr);
                } else {
                    try {
                        String source = java.nio.file.Files.readString(modulePath);
                        Lexer lexer = new Lexer(source);
                        List<Token> tokens = lexer.scanTokens();
                        Parser parser = new Parser(tokens);
                        List<Stmt> moduleStatements = parser.parse();
                        // Create a new interpreter for the module.
                        Interpreter moduleInterpreter = new Interpreter();
                        moduleInterpreter.interpret(moduleStatements);
                        // Assume the module file ends with an export statement.
                        // Retrieve the exported module name.
                        Object exportNameObj = moduleInterpreter.environment
                                .get(new Token(TokenType.IDENTIFIER, "__export__", null, 0));
                        if (!(exportNameObj instanceof String)) {
                            throw new RuntimeException("Module did not export a valid name.");
                        }
                        String exportName = (String) exportNameObj;
                        // Create a Module object from the moduleInterpreter's environment.
                        module = new Module(exportName, moduleInterpreter.environment);
                        modules.put(path, module);
                    } catch (Exception e) {
                        throw new RuntimeException("Error loading module: " + e.getMessage());
                    }
                }
                // Bind the module in the current environment by its exported name.
                environment.define(module.name, module);
                return null;
            }

            @Override
            public Void visitExportStmt(Stmt.Export stmt) {
                // When executing an export, simply store the exported module name in a special
                // variable.
                environment.define("__export__", stmt.name.lexeme);
                return null;
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
                    if (isTruthy(left))
                        return left;
                    return evaluate(expr.right);
                } else if (expr.operator.lexeme.equals("and")) {
                    if (!isTruthy(left))
                        return left;
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
                // throw new RuntimeError(expr.paren, "Expected " + function.arity() + "
                // arguments but got " + arguments.size() + ".");
                // }
                return function.call(Interpreter.this, arguments);
            }

            @Override
            public Object visitGetExpr(Expr.Get expr) {
                Object object = evaluate(expr.object);
                if (object instanceof Module) {
                    return ((Module) object).env.get(expr.name);
                }
                if (object instanceof Environment) {
                    return ((Environment) object).get(expr.name);
                }
                throw new RuntimeError(expr.name, "Only modules have properties.");
            }

            @Override
            public Object visitArrayExpr(Expr.Array expr) {
                List<Object> elements = new ArrayList<>();
                for (Expr element : expr.elements) {
                    elements.add(evaluate(element));
                }
                return elements;
            }

            @Override
            public Object visitIndexExpr(Expr.Index expr) {
                Object array = evaluate(expr.array);
                Object indexObj = evaluate(expr.index);
                if (!(indexObj instanceof Double)) {
                    throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index must be a number.");
                }
                int index = (int) Math.floor((Double) indexObj);
                if (array instanceof List) {
                    List<?> list = (List<?>) array;
                    if (index < 0 || index >= list.size()) {
                        throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index out of bounds.");
                    }
                    return list.get(index);
                } else if (array instanceof String) {
                    String s = (String) array;
                    if (index < 0 || index >= s.length()) {
                        throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index out of bounds.");
                    }
                    return Character.toString(s.charAt(index));
                } else {
                    throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Only arrays and strings can be indexed.");
                }
            }

            @Override
            public Object visitIndexAssignExpr(Expr.IndexAssign expr) {
                Object arrayObj = evaluate(expr.array);
                Object indexObj = evaluate(expr.index);
                if (!(indexObj instanceof Double)) {
                    throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index must be a number.");
                }
                int index = (int) Math.floor((Double) indexObj);
                Object value = evaluate(expr.value);
                if (arrayObj instanceof List) {
                    List<Object> list = (List<Object>) arrayObj;
                    if (index < 0 || index >= list.size()) {
                        throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index out of bounds.");
                    }
                    list.set(index, value);
                    return value;
                } else if (arrayObj instanceof String) {
                    String s = (String) arrayObj;
                    if (index < 0 || index >= s.length()) {
                        throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Index out of bounds.");
                    }
                    if (!(value instanceof String) || ((String) value).length() != 1) {
                        throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "String assignment requires a single character.");
                    }
                    char[] chars = s.toCharArray();
                    chars[index] = ((String) value).charAt(0);
                    String newStr = new String(chars);
                    // If the array is stored in a variable, update its binding.
                    if (expr.array instanceof Expr.Variable) {
                        Token varName = ((Expr.Variable) expr.array).name;
                        environment.assign(varName, newStr);
                    }
                    return newStr;
                } else {
                    throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Only arrays and strings can be indexed.");
                }
            }

            @Override
            public Object visitArrayFixedSizeExpr(Expr.ArrayFixedSize expr) {
                Object sizeObj = evaluate(expr.size);
                if (!(sizeObj instanceof Double)) {
                    throw new RuntimeError(new Token(TokenType.NIL, "nil", null, 0), "Array size must be a number.");
                }
                int size = (int) Math.floor((Double) sizeObj);
                List<Object> arr = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    arr.add(null);
                }
                return arr;
            }
        });
    }

    private void checkNumberOperand(lexer.Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(lexer.Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (Boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null)
            return "nil";
        if (object instanceof Double) {
            double text = (Double) object;
            if (text == (int) text)
                return String.valueOf((int) text);
            return String.valueOf(text);
        }
        if (object instanceof String) {
            return processEscapes((String) object);
        }
        return object.toString();
    }

    private String processEscapes(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        i++; // skip next char
                        break;
                    case 't':
                        sb.append('\t');
                        i++;
                        break;
                    case '"':
                        sb.append('"');
                        i++;
                        break;
                    case '\\':
                        sb.append('\\');
                        i++;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
