package interpreter;

import java.util.List;

public interface PavaCallable {
    int arity(); // Number of arguments taken
    Object call(Interpreter interpreter, List<Object> arguments);
}
