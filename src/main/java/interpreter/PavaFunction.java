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
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        
        return null;  // Default return `nil`
    }
    
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
