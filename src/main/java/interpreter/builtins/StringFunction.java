package interpreter.builtins;

import java.util.List;
import interpreter.*;

public class StringFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof Double) {
            return arg.toString();
        }
        throw new RuntimeError(null, "Argument to string() must be a number.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
