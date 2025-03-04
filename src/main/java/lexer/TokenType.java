package lexer;

public enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    STAR, DOT, COMMA, PLUS, MINUS, SEMICOLON, EQUAL,
    BANG, LESS, GREATER, SLASH,

    // Two-character tokens
    EQUAL_EQUAL, BANG_EQUAL, LESS_EQUAL, GREATER_EQUAL, COMMENT,

    // Reserved Keywords
    AND, CLASS, ELSE, FALSE, FOR, FUN, IF, NIL, OR, PRINT,
    RETURN, SUPER, THIS, TRUE, VAR, WHILE, IMPORT, EXPORT,

    // Identifiers and literals
    IDENTIFIER, STRING, NUMBER,

    // End of file
    EOF
}
