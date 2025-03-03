package interpreter.builtins;

import interpreter.*;
import java.util.List;

public class ExitFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof Double) {
            int code = (int) Math.round((Double) arg);
            System.exit(code);
        }
        throw new RuntimeError(null, "Argument to exit() must be a number.");
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}

