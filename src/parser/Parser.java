package parser;

import java.util.Stack;

import Standardize.Standardize;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;

//Class for building AST using recursive descent parsing
public class Parser {
  private Scanner s;
  private Token currentToken;
  Stack<ASTNode> stack;

  //Constructor
  public Parser(Scanner s) {
    this.s = s;
    stack = new Stack<ASTNode>();
  }

  //Build AST
  public Standardize buildAST() {
    startParse();
    return new Standardize(stack.pop());
  }

  //Start parsing
  public void startParse() {
    read();
    E();
    if (currentToken != null)
      throw new ParseException("Expected EOF.");
  }

  //Read the next token
  private void read() {
    do {
      currentToken = s.readNextToken();
    }

    //Skip DELETE tokens
    while (isCurrentTokenType(TokenType.DELETE));

    //Process non-DELETE tokens
    if (null != currentToken) {
      if (currentToken.getType() == TokenType.IDENTIFIER) {
        CreateTerminal(ASTNodeType.IDENTIFIER, currentToken.getValue());
      }

      else if (currentToken.getType() == TokenType.INTEGER) {
        CreateTerminal(ASTNodeType.INTEGER, currentToken.getValue());
      }

      else if (currentToken.getType() == TokenType.STRING) {
        CreateTerminal(ASTNodeType.STRING, currentToken.getValue());
      }
    }
  }

  // Check if the current token matches the specified type and value
  private boolean isCurrentToken(TokenType type, String value) {
    if (currentToken == null)
      return false;

    if (currentToken.getType() != type || !currentToken.getValue().equals(value))
      return false;

    return true;
  }

  // Check if the current token has the specified type
  private boolean isCurrentTokenType(TokenType type) {
    if (currentToken == null)
      return false;

    if (currentToken.getType() == type)
      return true;

    return false;
  }

  // Building an N-ary AST node
  private void build_tree(ASTNodeType type, int ariness) {
    ASTNode node = new ASTNode();
    node.setType(type);

    // Pop children from the stack
    while (ariness > 0) {

      ASTNode child = stack.pop();
      if (node.getChild() != null)
        child.setSibling(node.getChild());
      node.setChild(child);
      ariness--;
    }
    stack.push(node);

  }
  // Create a terminal node and push it onto the stack
  private void CreateTerminal(ASTNodeType type, String value) {
    ASTNode node = new ASTNode();

    node.setType(type);
    node.setValue(value);
    stack.push(node);

  }

  // Expressions
  private void E() {
    if (isCurrentToken(TokenType.RESERVED, "let")) {

      read();
      D();

      if (!isCurrentToken(TokenType.RESERVED, "in"))
        throw new ParseException("E:  'in' expected");

      read();
      E();
      build_tree(ASTNodeType.LET, 2);
      //E->’let’ D ’in’ E
    }

    else if (isCurrentToken(TokenType.RESERVED, "fn")) {
      int treesToPop = 0;

      read();

      while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)) {
        VB();
        treesToPop++;

      }

      if (treesToPop == 0)
        throw new ParseException("E: at least one 'Vb' expected");

      if (!isCurrentToken(TokenType.OPERATOR, "."))
        throw new ParseException("E: '.' expected");

      read();
      E();

      build_tree(ASTNodeType.LAMBDA, treesToPop + 1);
      //E -> ’fn’ Vb+ ’.’ E
    }

    else
      EW();
      //E -> EW
  }

  private void EW() {
    T();
    //Ew->T
    if (isCurrentToken(TokenType.RESERVED, "where")) {

      read();
      DR();
      build_tree(ASTNodeType.WHERE, 2);
      //Ew ->T ’where’ Dr
    }

  }

    // Tuple expressions
    private void T() {
      TA();
      //T -> TA
      int treesToPop = 0;
      while (isCurrentToken(TokenType.OPERATOR, ",")) {
        read();
        TA();
        treesToPop++;
      }

      if (treesToPop > 0)
        build_tree(ASTNodeType.TAU, treesToPop + 1);
      //T -> TA ( ',' TA)+
      }

    private void TA() {
      TC();
      //TA -> TC
      while (isCurrentToken(TokenType.RESERVED, "aug")) {
        read();
        TC();
        build_tree(ASTNodeType.AUG, 2);
        //TA -> TA 'aug' TC
      }

    }

    private void TC() {
      B();
      //TC -> B
      if (isCurrentToken(TokenType.OPERATOR, "->")) {
        read();
        TC();

        if (!isCurrentToken(TokenType.OPERATOR, "|"))
          throw new ParseException("TC: '|' expected");

        read();
        TC();
        build_tree(ASTNodeType.CONDITIONAL, 3);
        //TC -> B '->' TC '|' TC
      }
    }

  // Boolean Expressions
  private void B() {
    BT();
    //B -> BT
    while (isCurrentToken(TokenType.RESERVED, "or")) {
      read();
      BT();
      build_tree(ASTNodeType.OR, 2);
      //B -> B 'or' BT
    }

  }

  private void BT() {
    BS();
    //BT -> BS
    while (isCurrentToken(TokenType.OPERATOR, "&")) {
      read();
      BS();
      build_tree(ASTNodeType.AND, 2);
      //BT -> BT '&' BS
    }

  }

  private void BS() {

    if (isCurrentToken(TokenType.RESERVED, "not")) {
      read();
      BP();
      build_tree(ASTNodeType.NOT, 1);
      //BS -> 'not' BP
    }

    else
      BP();
      //BS -> BP
  }

  private void BP() {
    A();
    //BP -> A
    if (isCurrentToken(TokenType.RESERVED, "gr") || isCurrentToken(TokenType.OPERATOR, ">")) { 
      read();
      A();
      build_tree(ASTNodeType.GR, 2);
      //BP -> A ( 'gr' | '>') A
    }

    else if (isCurrentToken(TokenType.RESERVED, "ge") || isCurrentToken(TokenType.OPERATOR, ">=")) { 
      read();
      A();
      build_tree(ASTNodeType.GE, 2);
      //BP -> A ( 'ge' | '>=') A
    }

    else if (isCurrentToken(TokenType.RESERVED, "ls") || isCurrentToken(TokenType.OPERATOR, "<")) { 
      read();
      A();
      build_tree(ASTNodeType.LS, 2);
      //BP -> A ( 'ls' | '<') A
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "le") || isCurrentToken(TokenType.OPERATOR, "<=")) { 
      read();
      A(); 
      build_tree(ASTNodeType.LE, 2);
      //BP -> A ( 'le' | '<=') A
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "eq")) { 
      read();
      A();
      build_tree(ASTNodeType.EQ, 2);
      //BP -> A  'eq' A
    }

    else if (isCurrentToken(TokenType.RESERVED, "ne")) { 
      read();
      A();
      build_tree(ASTNodeType.NE, 2);
      //BP -> A  'ne' A
    }

  }

  // Arithmetic Expressions
  private void A() {

    if (isCurrentToken(TokenType.OPERATOR, "+")) { 
      read();
      AT();
      //A -> '+' AT
    }
    
    else if (isCurrentToken(TokenType.OPERATOR, "-")) { 
      read();
      AT();
      build_tree(ASTNodeType.NEG, 1);
      //A -> '-' AT
    }

    else
      AT();
      //A -> AT

    boolean plus = true;
    while (isCurrentToken(TokenType.OPERATOR, "+") || isCurrentToken(TokenType.OPERATOR, "-")) {

      if (currentToken.getValue().equals("+"))
        plus = true;

      else if (currentToken.getValue().equals("-"))
        plus = false;

      read();
      AT();

      if (plus) 
        build_tree(ASTNodeType.PLUS, 2);
        //A -> A '+' AT

      else 
        build_tree(ASTNodeType.MINUS, 2);
        //A -> A '+' AT
    }

  }

  private void AT() {
    AF();
    //AT -> AF
    boolean mult = true;
    while (isCurrentToken(TokenType.OPERATOR, "*") || isCurrentToken(TokenType.OPERATOR, "/")) {

      if (currentToken.getValue().equals("*"))
        mult = true;

      else if (currentToken.getValue().equals("/"))
        mult = false;

      read();
      AF();

      if (mult)
        build_tree(ASTNodeType.MULT, 2);
        //AT -> AT '*' AF

      else
      build_tree(ASTNodeType.DIV, 2);
      //AT -> AT '/' AF
    }

  }

  private void AF() {
    AP();
    //AF -> AP
    if (isCurrentToken(TokenType.OPERATOR, "**")) {
      read();
      AF();
      build_tree(ASTNodeType.EXP, 2);
      //AF -> AP '**' AF
    }

  }

  private void AP() {
    R();
    //AP -> R
    while (isCurrentToken(TokenType.OPERATOR, "@")) {
      read();

      if (!isCurrentTokenType(TokenType.IDENTIFIER))
        throw new ParseException("AP: expected Identifier");

      read();
      R();
      build_tree(ASTNodeType.AT, 3);
      //AP -> AP '@' '<IDENTIFIER>' R
    }

  }


  // Rators and Rands
  private void R() {
    RN();
    //R-> RN
    read();

    while (isCurrentTokenType(TokenType.INTEGER) ||
          isCurrentTokenType(TokenType.STRING) ||
          isCurrentTokenType(TokenType.IDENTIFIER) ||
          isCurrentToken(TokenType.RESERVED, "true") ||
          isCurrentToken(TokenType.RESERVED, "false") ||
          isCurrentToken(TokenType.RESERVED, "nil") ||
          isCurrentToken(TokenType.RESERVED, "dummy") ||
          isCurrentTokenType(TokenType.L_PAREN)) {

      RN();
      build_tree(ASTNodeType.GAMMA, 2);
      //R -> R RN
      read();
    }

  }

  private void RN() {
    if (isCurrentTokenType(TokenType.IDENTIFIER) ||
        isCurrentTokenType(TokenType.INTEGER) ||
        isCurrentTokenType(TokenType.STRING)) {
          //Rn ->’<IDENTIFIER>’ | <INTEGER>’ | ’<STRING>’
    }

    else if (isCurrentToken(TokenType.RESERVED, "true")) {
      CreateTerminal(ASTNodeType.TRUE, "true");
      //RN -> 'true'
    }

    else if (isCurrentToken(TokenType.RESERVED, "false")) {
      CreateTerminal(ASTNodeType.FALSE, "false");
      //RN -> 'false'
    }

    else if (isCurrentToken(TokenType.RESERVED, "nil")) {
      CreateTerminal(ASTNodeType.NIL, "nil");
      //RN -> 'nil'
    }

    else if (isCurrentTokenType(TokenType.L_PAREN)) {
      read();
      E();

      if (!isCurrentTokenType(TokenType.R_PAREN))
        throw new ParseException("RN: ')' expected");
      //RN -> '(' E ')'
    }

    else if (isCurrentToken(TokenType.RESERVED, "dummy")) {
      CreateTerminal(ASTNodeType.DUMMY, "dummy");
      //RN -> 'dummy'
    }

  }


  // Definitions
  private void D() {
    DA();
    //D -> DA
    if (isCurrentToken(TokenType.RESERVED, "within")) { 
      read();
      D();
      build_tree(ASTNodeType.WITHIN, 2);
      //D -> DA 'WITHIN' D
    }

  }

  private void DA() {
    DR();
    //DA -> DR
    int treesToPop = 0;
    while (isCurrentToken(TokenType.RESERVED, "and")) { 
      read();
      DR();
      treesToPop++;
    }

    if (treesToPop > 0)
    build_tree(ASTNodeType.SIMULTDEF, treesToPop + 1);
    //DA -> DR ( 'and' DR )+

  }

  private void DR() {
    if (isCurrentToken(TokenType.RESERVED, "rec")) {
      read();
      DB();
      build_tree(ASTNodeType.REC, 1);
      //DR -> 'rec' DB
    }

    else {
      DB();
      //DR -> DB
    }

  }


  private void DB() {
    if (isCurrentTokenType(TokenType.L_PAREN)) {
      D();
      read();

      if (!isCurrentTokenType(TokenType.R_PAREN))
        throw new ParseException("DB: ')' expected");
      //DB -> '(' D ')'
      read();
    }

    else if (isCurrentTokenType(TokenType.IDENTIFIER)) {
      read();

      if (isCurrentToken(TokenType.OPERATOR, ",")) {
        read();
        VL();

        if (!isCurrentToken(TokenType.OPERATOR, "="))
          throw new ParseException("DB: = expected.");
          build_tree(ASTNodeType.COMMA, 2);

        read();
        E();
        build_tree(ASTNodeType.EQUAL, 2);
        //DB -> Vl ’=’ E
      }

      else {

        if (isCurrentToken(TokenType.OPERATOR, "=")) {
          read();
          E();
          build_tree(ASTNodeType.EQUAL, 2);

        }

        else {
          int treesToPop = 0;

          while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)) {
            VB();
            treesToPop++;
          }

          if (treesToPop == 0)
            throw new ParseException("E: at least one 'Vb' expected");

          if (!isCurrentToken(TokenType.OPERATOR, "="))
            throw new ParseException("DB: = expected.");

          read();
          E();

          build_tree(ASTNodeType.FCNFORM, treesToPop + 2);
          //D -> ’<IDENTIFIER>’ VB+ ’=’ E
        }
      }
    }
  }

  // Variables
  private void VB() {

    if (isCurrentTokenType(TokenType.IDENTIFIER)) {
      // VB -> ’<IDENTIFIER>’
      read();

    }

    else if (isCurrentTokenType(TokenType.L_PAREN)) {
      read();

      if (isCurrentTokenType(TokenType.R_PAREN)) {
        CreateTerminal(ASTNodeType.PAREN, "");
        //VB -> ’(’ ’)’
        read();
      }

      else {
        VL();

        if (!isCurrentTokenType(TokenType.R_PAREN))
          throw new ParseException("VB: ')' expected");
        //VB -> ’(’ VL ’)’
        read();
      }
    }

  }


  private void VL() {
    if (!isCurrentTokenType(TokenType.IDENTIFIER))
      throw new ParseException("VL: Identifier expected");

    else {
      read();
      int treesToPop = 0;

      while (isCurrentToken(TokenType.OPERATOR, ",")) { 
        read();

        if (!isCurrentTokenType(TokenType.IDENTIFIER))
          throw new ParseException("VL: Identifier expected");

        read();
        treesToPop++;
      }

      if (treesToPop > 0)
      build_tree(ASTNodeType.COMMA, treesToPop + 1);
      //VL -> ’<IDENTIFIER>’ list ’,’
    }

  }



}
