package CSEmachine;
import java.util.Stack;

import parser.ASTNode;
import parser.ASTNodeType;

// Represents a Beta node for evaluating conditionals
public class Beta extends ASTNode {

  private Stack<ASTNode> thenPart;
  private Stack<ASTNode> elsePart;

  // Constructor
  public Beta() {

    setType(ASTNodeType.BETA);
    thenPart = new Stack<ASTNode>();
    elsePart = new Stack<ASTNode>();

  }

  // Getters and setters 
  public Stack<ASTNode> getThenPart() {
    return thenPart;
  }

  public Stack<ASTNode> getElsePart() {
    return elsePart;
  }

  public void setThenPart(Stack<ASTNode> thenPart) {
    this.thenPart = thenPart;
  }

  public void setElsePart(Stack<ASTNode> elsePart) {
    this.elsePart = elsePart;
  }

  // Accept method for visitor pattern
  public Beta accept(Copier copier) {
    return copier.copy(this);
  }

}
