package interpreter.builtins;


import interpreter.*;
import lexer.Token;

import java.util.List;

public class GetAsciiCodeFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof String) {
            String s = (String) arg;
            if (s.length() != 1) {
                throw new RuntimeError(new Token(null, "NIL", "NIL", 0), " getAsciiCode() expects a single character.");
            }
            return (double) s.charAt(0);  // Return as a Double.
        }
        throw new RuntimeError(new Token(null, "NIL", "NIL", 0), " getAsciiCode() expects a string.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
