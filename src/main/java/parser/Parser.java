package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lexer.Token;
import lexer.TokenType;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> parseStrings() {
        List<String> expressions = new ArrayList<>();

        while (!isAtEnd()) {
            expressions.add(new AstPrinter().print(expression())); // Keep parsing expressions until EOF
        }
        return expressions;
    }

    // Parse a series of statements.
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }
        return statements;
    }

    private Stmt statement() {
        if (match(TokenType.IMPORT))
            return importStatement();
        if (match(TokenType.EXPORT))
            return exportStatement();
        if (match(TokenType.PRINT))
            return printStatement();
        if (match(TokenType.VAR))
            return varDeclaration();
        if (match(TokenType.FUN))
            return functionDeclaration();
        if (match(TokenType.IF))
            return ifStatement();
        if (match(TokenType.FOR))
            return forStatement();
        if (match(TokenType.WHILE))
            return whileStatement();
        if (match(TokenType.LEFT_BRACE))
            return block();
        if (match(TokenType.RETURN))
            return returnStatement();

        return expressionStatement();
    }

    private Stmt importStatement() {
        // We expect a module path as a string literal.
        Token path = consume(TokenType.STRING, "Expect module path as a string.");
        consume(TokenType.SEMICOLON, "Expect ';' after import.");
        return new Stmt.Import(path);
    }

    private Stmt exportStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expect module name after 'export'.");
        consume(TokenType.SEMICOLON, "Expect ';' after export.");
        return new Stmt.Export(name);
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt.Function functionDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect function name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");
        List<Stmt.Function.Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token paramName = consume(TokenType.IDENTIFIER, "Expect parameter name.");
                Expr defaultValue = null;
                if (match(TokenType.EQUAL)) {
                    defaultValue = expression();
                }
                parameters.add(new Stmt.Function.Parameter(paramName, defaultValue));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        // Enforce: if any parameter has a default value, all parameters to its right
        // must have defaults.
        boolean foundDefaultBeforeLastParam = false;

        // check upto second-last element if it has a default value
        for (int i = 0; i < parameters.size() - 1; i++) {
            Stmt.Function.Parameter param = parameters.get(i);
            if (param.defaultValue != null) {
                foundDefaultBeforeLastParam = true;
            }
        }

        if (foundDefaultBeforeLastParam
                && parameters.get(parameters.size() - 1).defaultValue == null) {
            throw error(peek(), "Parameters with default values must be trailing.");
        }

        consume(TokenType.LEFT_BRACE, "Expect '{' before function body.");
        List<Stmt> body = blockStatements(); // Helper that returns List<Stmt>.
        return new Stmt.Function(name, parameters, body);
    }

    private List<Stmt> blockStatements() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(statement());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt forStatement() {
        Token forToken = previous(); // Capture the 'for' token for error reporting.
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

        // Initializer clause.
        Stmt initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        // Condition clause.
        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        // Increment clause.
        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

        // Body.
        Stmt body = statement();

        // If the body is a variable declaration (and not a block), report an error.
        if (body instanceof Stmt.Var) {
            throw error(forToken, "For loop body must be enclosed in a block if it is a variable declaration.");
        }

        // run for as while loop (desugar it)
        // If increment exists, execute it at the end of each loop iteration.
        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(
                    body,
                    new Stmt.Expression(increment)));
        }

        // If condition is omitted, default to 'true'.
        if (condition == null) {
            condition = new Expr.Literal(true);
        }
        body = new Stmt.While(condition, body);

        // If initializer exists, wrap everything in a block.
        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(statement());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return new Stmt.Block(statements);
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        // Check for fixed-size array declaration.
        if (match(TokenType.LEFT_BRACKET)) {
            Expr sizeExpr = expression();
            consume(TokenType.RIGHT_BRACKET, "Expect ']' after array size.");
            // Use ArrayFixedSize node as initializer.
            initializer = new Expr.ArrayFixedSize(sizeExpr);
        } else if (match(TokenType.EQUAL)) {
            initializer = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt printStatement() {
        Expr value = expression();
        // match(TokenType.SEMICOLON);
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    // assignment → or ( "=" assignment )?
    private Expr assignment() {
        Expr expr = or();
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment(); // right-associative
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Index) {
                Expr.Index indexExpr = (Expr.Index) expr;
                return new Expr.IndexAssign(indexExpr.array, indexExpr.index, value);
            }
            throw error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    // or → and ( "or" and )*
    private Expr or() {
        Expr expr = and();
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    // and → equality ( "and" equality )*
    private Expr and() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        // Prevent arithmetic on booleans/nil.
        if (isBooleanOrNil(expr))
            return expr;

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    // New call() method to support call expressions and property access.
    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenType.DOT)) {
                Token name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else if (match(TokenType.LEFT_BRACKET)) {
                Expr index = expression();
                consume(TokenType.RIGHT_BRACKET, "Expect ']' after index.");
                expr = new Expr.Index(expr, index);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {
        if (match(TokenType.LEFT_BRACKET))
            return arrayLiteral();
        if (match(TokenType.TRUE))
            return new Expr.Literal(true);
        if (match(TokenType.FALSE))
            return new Expr.Literal(false);
        if (match(TokenType.NIL))
            return new Expr.Literal(null);
        if (match(TokenType.NUMBER))
            return new Expr.Literal(previous().literal);
        if (match(TokenType.STRING))
            // Wrap the string literal in double quotes.
            // return new Expr.Literal("\"" + previous().literal + "\"");
            return new Expr.Literal(previous().literal);
        if (match(TokenType.IDENTIFIER))
            return new Expr.Variable(previous());
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw new SyntaxError(peek().getLine(), "Error at: '" + peek().getLexeme() + "': Unexpected token");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();
        throw new SyntaxError(peek().getLine(), message);
    }

    private RuntimeException error(Token token, String message) {
        return new SyntaxError(peek().getLine(), message);
    }

    // Helper to check if an expression is a boolean literal or nil.
    private boolean isBooleanOrNil(Expr expr) {
        if (expr instanceof Expr.Literal) {
            Object value = ((Expr.Literal) expr).value;
            return (value instanceof Boolean || value == null);
        } else if (expr instanceof Expr.Grouping) {
            return isBooleanOrNil(((Expr.Grouping) expr).expression);
        }
        return false;
    }

    private Expr arrayLiteral() {
        List<Expr> elements = new ArrayList<>();
        if (!check(TokenType.RIGHT_BRACKET)) {
            do {
                elements.add(expression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_BRACKET, "Expect ']' after array literal.");
        return new Expr.Array(elements);
    }
}
