package interpreter.builtins;

import java.util.List;
import interpreter.*;
import lexer.Token;

public class FromAsciiCodeFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof Double) {
            int code = (int) Math.round((Double) arg);
            return Character.toString((char) code);
        }
        throw new RuntimeError(new Token(null, "NIL", "NIL", 0), " fromAsciiCode() expects a number.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
