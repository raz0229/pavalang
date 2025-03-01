package interpreter.builtins;

import java.util.List;
import interpreter.*;

public class TypeFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg == null) return "NIL";
        if (arg instanceof Double) return "NUMBER";
        if (arg instanceof String) return "STRING";
        if (arg instanceof Boolean) return "BOOLEAN";
        if (arg instanceof PavaCallable) return "FUNCTION";
        return "OBJECT";
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
