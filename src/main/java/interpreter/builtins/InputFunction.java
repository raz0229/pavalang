package interpreter.builtins;

import java.util.List;
import java.util.Scanner;
import interpreter.*;

public class InputFunction implements PavaCallable {
    // Use a static Scanner so that input is only opened once.
    private static final Scanner scanner = new Scanner(System.in);
    
    // For input, we allow 0 or 1 argument, so we return -1 to signal variable arity.
    @Override
    public int arity() {
        return -1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        String prompt = "";
        if (arguments.size() == 1) {
            prompt = arguments.get(0).toString();
        }
        System.out.print(prompt);
        // Read one line from standard input.
        String line = scanner.nextLine();
        return line;
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
