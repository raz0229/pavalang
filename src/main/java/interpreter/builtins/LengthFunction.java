package interpreter.builtins;

import java.util.List;
import interpreter.*;

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
        }
        throw new RuntimeError(null, "Argument to length() must be a string.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
