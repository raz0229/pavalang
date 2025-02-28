package parser;

import lexer.Token;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitPrintStmt(Print stmt);
        R visitExpressionStmt(Expression stmt);
        R visitVarStmt(Var stmt);
    }

    public static class Print extends Stmt {
        public final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Var extends Stmt {
        public final Token name;
        public final Expr initializer; // may be null if no initializer is provided
        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
