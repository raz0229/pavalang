package parser;

import lexer.Token;
import lexer.TokenType;

public class Parser {
    private final String source;

    public Parser(String source) {
        this.source = source;
    }

    public String parseToken(Token tk) {
        switch (tk.getType()) {
            case TokenType.TRUE:
                return "true";
                //break;
            case TokenType.FALSE:
                return "false";
                //break;
            case TokenType.NIL:
                return "nil";
                //break;
            default:
                return "";
                //break;
        }
    }
    
}
