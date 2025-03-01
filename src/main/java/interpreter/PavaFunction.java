package interpreter;

import java.util.List;
import parser.Stmt;
import interpreter.Environment;
import interpreter.Interpreter;
import lexer.Token;

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
        // For each parameter (if any) bind the corresponding argument.
        // For now, functions have no parameters.
        interpreter.executeBlock(declaration.body, environment);
        return null;
    }
    
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
