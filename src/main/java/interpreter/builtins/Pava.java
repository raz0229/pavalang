package interpreter.builtins;

import java.util.List;
import interpreter.*;


public class Pava implements PavaCallable {
    @Override
    public int arity() {
        return 0;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return "Akhtar Lava";
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}