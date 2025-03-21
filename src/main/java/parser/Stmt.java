package parser;

import lexer.Token;
import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitPrintStmt(Print stmt);
        R visitExpressionStmt(Expression stmt);
        R visitVarStmt(Var stmt);
        R visitBlockStmt(Block stmt);
        R visitIfStmt(If stmt);
        R visitWhileStmt(While stmt);
        R visitFunctionStmt(Function stmt);
        R visitReturnStmt(Return stmt);
        R visitImportStmt(Import stmt);
        R visitExportStmt(Export stmt);
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

    public static class Block extends Stmt {
        public final List<Stmt> statements;
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;  // Can be null if there's no else.
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class Function extends Stmt {
        public final Token name;
        public final List<Parameter> params;  // For now, empty list.
        public final List<Stmt> body;
        public Function(Token name, List<Parameter> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        // Parameter class with optional default value.
        public static class Parameter {
            public final Token name;
            public final Expr defaultValue; // Can be null if no default.
            public Parameter(Token name, Expr defaultValue) {
                this.name = name;
                this.defaultValue = defaultValue;
            }
        }
    }

    public static class Return extends Stmt {
        public final Token keyword;
        public final Expr value;
    
        public Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }
    
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    public static class Import extends Stmt {
        public final Token path;  // The module file path (as a string literal).
        public Import(Token path) {
            this.path = path;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitImportStmt(this);
        }
    }

    public static class Export extends Stmt {
        public final Token name; // The exported module name.
        public Export(Token name) {
            this.name = name;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExportStmt(this);
        }
    }
    

    public abstract <R> R accept(Visitor<R> visitor);
}
