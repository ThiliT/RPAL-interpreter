package parser;

import java.util.Stack;

import Standardize.Standardize;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;

// Uses recursive descent parsing
public class Parser {
  private Scanner s;
  private Token currentToken;
  Stack<ASTNode> stack;

  public Parser(Scanner s) {
    this.s = s;
    stack = new Stack<ASTNode>();
  }

  public Standardize buildAST() {
    startParse();
    return new Standardize(stack.pop());
  }

  public void startParse() {
    read();
    E();
    if (currentToken != null)
      throw new ParseException("Expected EOF.");
  }

  private void read() {
    do {
      currentToken = s.readNextToken();
    }

    while (isCurrentTokenType(TokenType.DELETE));
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

  private boolean isCurrentToken(TokenType type, String value) {
    if (currentToken == null)
      return false;

    if (currentToken.getType() != type || !currentToken.getValue().equals(value))
      return false;

    return true;
  }

  private boolean isCurrentTokenType(TokenType type) {
    if (currentToken == null)
      return false;

    if (currentToken.getType() == type)
      return true;

    return false;
  }

  // Building an N-ary ast node
  private void build_tree(ASTNodeType type, int ariness) {
    ASTNode node = new ASTNode();
    node.setType(type);

    while (ariness > 0) {

      ASTNode child = stack.pop();
      if (node.getChild() != null)
        child.setSibling(node.getChild());
      node.setChild(child);
      ariness--;
    }
    stack.push(node);

  }

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
    }

    else
      EW();
  }


  private void EW() {
    T();

    if (isCurrentToken(TokenType.RESERVED, "where")) {

      read();
      DR();
      build_tree(ASTNodeType.WHERE, 2);
    }

  }

    // Tuple expressions
    private void T() {
      TA();

      int treesToPop = 0;
      while (isCurrentToken(TokenType.OPERATOR, ",")) {
        read();
        TA();
        treesToPop++;
      }

      if (treesToPop > 0)
        build_tree(ASTNodeType.TAU, treesToPop + 1);

      }

    private void TA() {
      TC();

      while (isCurrentToken(TokenType.RESERVED, "aug")) {
        read();
        TC();
        build_tree(ASTNodeType.AUG, 2);
      }

    }
  
    private void TC() {
      B();

      if (isCurrentToken(TokenType.OPERATOR, "->")) {
        read();
        TC();
        
        if (!isCurrentToken(TokenType.OPERATOR, "|"))
          throw new ParseException("TC: '|' expected");
        
        read();
        TC();
        build_tree(ASTNodeType.CONDITIONAL, 3);
      }
    }

  // Boolean Expressions
  private void B() {
    BT();

    while (isCurrentToken(TokenType.RESERVED, "or")) {
      read();
      BT();
      build_tree(ASTNodeType.OR, 2);
    }

  }

  private void BT() {
    BS();

    while (isCurrentToken(TokenType.OPERATOR, "&")) {
      read();
      BS(); 
      build_tree(ASTNodeType.AND, 2);
    }

  }

  private void BS() {

    if (isCurrentToken(TokenType.RESERVED, "not")) { 
      read();
      BP();
      build_tree(ASTNodeType.NOT, 1);
    }
    
    else
      BP();
  }

  private void BP() {
    A();

    if (isCurrentToken(TokenType.RESERVED, "gr") || isCurrentToken(TokenType.OPERATOR, ">")) { 
      read();
      A();
      build_tree(ASTNodeType.GR, 2);
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "ge") || isCurrentToken(TokenType.OPERATOR, ">=")) { 
      read();
      A();
      build_tree(ASTNodeType.GE, 2);
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "ls") || isCurrentToken(TokenType.OPERATOR, "<")) { 
      read();
      A(); 
      build_tree(ASTNodeType.LS, 2);
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "le") || isCurrentToken(TokenType.OPERATOR, "<=")) { 
      read();
      A(); 
      build_tree(ASTNodeType.LE, 2);
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "eq")) { 
      read();
      A();
      build_tree(ASTNodeType.EQ, 2);
    }

    else if (isCurrentToken(TokenType.RESERVED, "ne")) { 
      read();
      A();
      build_tree(ASTNodeType.NE, 2);
    }

  }

  // Arithmetic Expressions
  private void A() {

    if (isCurrentToken(TokenType.OPERATOR, "+")) { 
      read();
      AT(); 
    }
    
    else if (isCurrentToken(TokenType.OPERATOR, "-")) { 
      read();
      AT(); 
      build_tree(ASTNodeType.NEG, 1);
    }
    
    else
      AT(); 

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

      else 
        build_tree(ASTNodeType.MINUS, 2);
    }

  }

  private void AT() {
    AF();

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

      else
      build_tree(ASTNodeType.DIV, 2);
    }

  }

  private void AF() {
    AP();

    if (isCurrentToken(TokenType.OPERATOR, "**")) {
      read();
      AF();
      build_tree(ASTNodeType.EXP, 2);
    }

  }

  private void AP() {
    R();

    while (isCurrentToken(TokenType.OPERATOR, "@")) {
      read();
    
      if (!isCurrentTokenType(TokenType.IDENTIFIER))
        throw new ParseException("AP: expected Identifier");

      read();
      R();
      build_tree(ASTNodeType.AT, 3);
    }

  }


  // Rators and Rands
  private void R() {
    RN(); 
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
      read();
    }

  }

  private void RN() {
    if (isCurrentTokenType(TokenType.IDENTIFIER) ||
        isCurrentTokenType(TokenType.INTEGER) ||
        isCurrentTokenType(TokenType.STRING)) {

    }
    
    else if (isCurrentToken(TokenType.RESERVED, "true")) {
      CreateTerminal(ASTNodeType.TRUE, "true");
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "false")) { 
      CreateTerminal(ASTNodeType.FALSE, "false");
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "nil")) { 
      CreateTerminal(ASTNodeType.NIL, "nil");
    }
    
    else if (isCurrentTokenType(TokenType.L_PAREN)) {
      read();
      E();

      if (!isCurrentTokenType(TokenType.R_PAREN))
        throw new ParseException("RN: ')' expected");
    }
    
    else if (isCurrentToken(TokenType.RESERVED, "dummy")) { 
      CreateTerminal(ASTNodeType.DUMMY, "dummy");
    }

  }


  // Definitions
  private void D() {
    DA();

    if (isCurrentToken(TokenType.RESERVED, "within")) { 
      read();
      D();
      build_tree(ASTNodeType.WITHIN, 2);
    }

  }

  private void DA() {
    DR();

    int treesToPop = 0;
    while (isCurrentToken(TokenType.RESERVED, "and")) { 
      read();
      DR(); 
      treesToPop++;
    }

    if (treesToPop > 0)
    build_tree(ASTNodeType.SIMULTDEF, treesToPop + 1);
  
  }

  private void DR() {
    if (isCurrentToken(TokenType.RESERVED, "rec")) { 
      read();
      DB(); 
      build_tree(ASTNodeType.REC, 1);
    }
    
    else { 
      DB(); 
    }

  }

  private void DB() {
    if (isCurrentTokenType(TokenType.L_PAREN)) { 
      D();
      read();

      if (!isCurrentTokenType(TokenType.R_PAREN))
        throw new ParseException("DB: ')' expected");
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
        }
      }
    }
  }

  // Variables
  private void VB() {

    if (isCurrentTokenType(TokenType.IDENTIFIER)) { 
      read();
    }
    
    else if (isCurrentTokenType(TokenType.L_PAREN)) {
      read();

      if (isCurrentTokenType(TokenType.R_PAREN)) { 
        CreateTerminal(ASTNodeType.PAREN, "");
        read();
      }
      
      else { 
        VL();

        if (!isCurrentTokenType(TokenType.R_PAREN))
          throw new ParseException("VB: ')' expected");

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
    }

  }



}
