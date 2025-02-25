package parser;

import lexer.Token;
import lexer.TokenType;

public class Parser {
    private final String source;

    public Parser(String source) {
        this.source = source;
    }

    public Object parseToken(Token tk) {
        switch (tk.getType()) {
            case TokenType.TRUE:
                //Expr expression = Expr.Literal(45.7);
                return new Expr.Literal(true).toString();
                //break;
            case TokenType.FALSE:
                return new Expr.Literal(false).toString();
                //break;
            case TokenType.NIL:
                return new Expr.Literal("nil").toString();
                //break;
            case TokenType.NUMBER:
            case TokenType.STRING:
                return new Expr.Literal(tk.getLiteral()).toString();
            
            case TokenType.LEFT_PAREN:
                return new Expr.Literal("(").toString() + "group ";
            case TokenType.RIGHT_PAREN:
                return new Expr.Literal(")").toString();
                

            default:
                return new Expr.Literal("").toString();
                //break;
        }
    }
    
}
