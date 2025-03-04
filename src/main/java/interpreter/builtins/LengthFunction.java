package interpreter.builtins;

import java.util.List;
import interpreter.*;
import lexer.Token;

public class LengthFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof String) {
            return (double) ((String) arg).length();
        } else if (arg instanceof List) {
            return (double) ((List<?>)arg).size();
        }
        throw new RuntimeError(new Token(null, "NIL", "NIL", 0), "Argument to length() must be a string.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
