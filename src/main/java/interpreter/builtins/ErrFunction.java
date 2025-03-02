package interpreter.builtins;

import java.util.List;
import interpreter.*;

public class ErrFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        System.err.println(arguments.get(0));
        return null;
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
