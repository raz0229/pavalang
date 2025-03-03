package interpreter.builtins;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import interpreter.*;

public class ShellFunction implements PavaCallable {
    @Override
    public int arity() {
        return 1;
    }
    
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (!(arg instanceof String)) {
            throw new RuntimeError(null, "Argument to shell() must be a string.");
        }
        String command = (String) arg;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (output.length() > 0) output.append("\n");
                output.append(line);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeError(null, "Error executing shell command: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return "<builtin PAVA fn>";
    }
}
