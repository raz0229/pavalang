package interpreter;

import lexer.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;
    public RuntimeError(Token token, String message) {
        super( "[line "+token.getLine()+ "]" + message);
        this.token = token;
    }
}
