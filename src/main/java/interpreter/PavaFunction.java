package interpreter;

import java.util.List;
import parser.Stmt;

public class PavaFunction implements PavaCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    public PavaFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.params.size(); // For no-arg functions, this is 0.
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        int paramCount = declaration.params.size();
        if (arguments.size() > paramCount) {
            throw new RuntimeError(declaration.name, "Too many arguments.");
        }
        // For each parameter, bind the passed argument or, if missing, evaluate the
        // default.
        for (int i = 0; i < paramCount; i++) {
            Stmt.Function.Parameter param = declaration.params.get(i);
            Object value;
            if (i < arguments.size()) {
                value = arguments.get(i);
            } else {
                if (param.defaultValue == null) {
                    throw new RuntimeError(param.name, "Missing argument for parameter '" + param.name.lexeme + "'.");
                }
                value = interpreter.evaluate(param.defaultValue);
            }
            environment.define(param.name.lexeme, value);
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}