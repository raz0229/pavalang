package parser;

import java.util.List;

import lexer.Token;

public abstract class Expr {
  public interface Visitor<R> {
    R visitBinaryExpr(Binary expr);

    R visitGroupingExpr(Grouping expr);

    R visitLiteralExpr(Literal expr);

    R visitUnaryExpr(Unary expr);

    R visitVariableExpr(Variable expr);

    R visitAssignExpr(Assign expr);

    R visitLogicalExpr(Logical expr);

    R visitCallExpr(Call expr);

    R visitGetExpr(Get expr); // For property access from module (dot operator)

    R visitArrayExpr(Array expr); // array literal.

    R visitIndexExpr(Index expr); // index access.

    R visitIndexAssignExpr(IndexAssign expr); // New for index assignment.

    R visitArrayFixedSizeExpr(ArrayFixedSize expr); // New for fixed-size array.
  }

  public static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    public final Expr left;
    public final Token operator;
    public final Expr right;
  }

  public static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    public final Expr expression;
  }

  public static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    public String toString() {
      return this.value.toString();
    }

    public final Object value;
  }

  public static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    public final Token operator;
    public final Expr right;
  }

  public static class Variable extends Expr {
    public final Token name;

    public Variable(Token name) {
      this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }
  }

  public static class Assign extends Expr {
    public final Token name;
    public final Expr value;

    public Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }
  }

  public static class Logical extends Expr {
    public final Expr left;
    public final Token operator;
    public final Expr right;

    public Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }

  public static class Call extends Expr {
    public final Expr callee;
    public final Token paren; // The closing parenthesis.
    public final java.util.List<Expr> arguments;

    public Call(Expr callee, Token paren, java.util.List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }
  }

  public static class Get extends Expr {
    public final Expr object;
    public final Token name;

    public Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }
  }

  public static class Array extends Expr {
    public final List<Expr> elements;

    public Array(List<Expr> elements) {
      this.elements = elements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayExpr(this);
    }
  }

  public static class Index extends Expr {
    public final Expr array;
    public final Expr index;

    public Index(Expr array, Expr index) {
      this.array = array;
      this.index = index;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexExpr(this);
    }
  }

  public static class IndexAssign extends Expr {
    public final Expr array;
    public final Expr index;
    public final Expr value;

    public IndexAssign(Expr array, Expr index, Expr value) {
      this.array = array;
      this.index = index;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexAssignExpr(this);
    }
  }

  // Represents a fixed-size array declaration.
  public static class ArrayFixedSize extends Expr {
    public final Expr size;

    public ArrayFixedSize(Expr size) {
      this.size = size;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayFixedSizeExpr(this);
    }
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
