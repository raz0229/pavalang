package lexer;

public class LexicalError extends RuntimeException {
    public LexicalError(String message) {
        super(message);
    }
}
