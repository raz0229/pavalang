package interpreter.builtins;

import java.util.List;
import interpreter.*;
import lexer.Token;

public class NumberFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof String) {
            try {
                return Double.parseDouble((String) arg);
            } catch (NumberFormatException e) {
                throw new RuntimeError(new Token(null, "NIL", "NIL", 0), "Invalid number format: " + arg);
            }
        }
        throw new RuntimeError(new Token(null, "NIL", "NIL", 0), "Argument to number() must be a string.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
