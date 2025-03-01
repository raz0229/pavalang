package parser;

public class SyntaxError extends RuntimeException {
    public SyntaxError(int line, String message) {
        super("[line " + line + "] " + message);
    }
}

