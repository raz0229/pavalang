package interpreter.builtins;

import java.util.List;
import interpreter.*;

public class ClockFunction implements PavaCallable {
    @Override
    public int arity() {
        return 0;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // Returns current time in seconds.
        return (double) (System.currentTimeMillis() / 1000);
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
