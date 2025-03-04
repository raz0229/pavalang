package parser;

import lexer.Token;
import lexer.TokenType;
import parser.Expr.Array;
import parser.Expr.ArrayFixedSize;
import parser.Expr.Call;
import parser.Expr.Get;
import parser.Expr.Index;
import parser.Expr.IndexAssign;

public class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  } 

  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    return expr.left + " " + expr.operator;
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return "(var " + expr.name.lexeme + ")";
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    return expr.name + " = " + expr.value;
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  public static void main(String[] args) {
    Expr expression = new Expr.Binary(
        new Expr.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(123)),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Grouping(
            new Expr.Literal(45.67)));

    System.out.println(new AstPrinter().print(expression));
  }

  @Override
  public String visitCallExpr(Call expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
  }

  @Override
  public String visitGetExpr(Get expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitGetExpr'");
  }

  @Override
  public String visitArrayExpr(Array expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitArrayExpr'");
  }

  @Override
  public String visitIndexExpr(Index expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitIndexExpr'");
  }

  @Override
  public String visitIndexAssignExpr(IndexAssign expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitIndexAssignExpr'");
  }

  @Override
  public String visitArrayFixedSizeExpr(ArrayFixedSize expr) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'visitArrayFixedSizeExpr'");
  }
}
