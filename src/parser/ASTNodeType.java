package parser;

//AST Node Types
public enum ASTNodeType {
  IDENTIFIER("<ID:%s>"),
  STRING("<STR:'%s'>"),
  INTEGER("<INT:%s>"),
  LET("let"),
  LAMBDA("lambda"),
  WHERE("where"),
  WITHIN("within"),
  CONDITIONAL("->"),
  OR("or"),
  AND("&"),
  NOT("not"),
  GR("gr"),
  GE("ge"),
  LS("ls"),
  LE("le"),
  EQ("eq"),
  NE("ne"),
  PLUS("+"),
  MINUS("-"),
  NEG("neg"),
  MULT("*"),
  DIV("/"),
  EXP("**"),
  TRUE("<true>"),
  FALSE("<false>"),
  TAU("tau"),
  AUG("aug"),
  AT("@"),
  GAMMA("gamma"),
  NIL("<nil>"),
  DUMMY("<dummy>"),
  SIMULTDEF("and"),
  REC("rec"),
  EQUAL("="),
  FCNFORM("function_form"),
  PAREN("<()>"),
  COMMA(","),
  YSTAR("<Y*>"),
  BETA(""),
  DELTA(""),
  ETA(""),
  TUPLE("");

  private String printName;

  // Constructor for ASTNodeType
  private ASTNodeType(String name) {
    printName = name;
  }

  public String getPrintName() {
    return printName;
  }
}