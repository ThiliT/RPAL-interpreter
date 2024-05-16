package CSEmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import parser.ASTNode;
import parser.ASTNodeType;

// Delta class representing the DELTA nodes in the AST
public class Delta extends ASTNode {

  private List<String> boundVars;
  private Environment linkedEnvironment;
  private Stack<ASTNode> body;
  private int index;

   // Constructor
  public Delta() {

    setType(ASTNodeType.DELTA);
    boundVars = new ArrayList<String>();
  }

  // Method to create a deep copy of the Delta node
  public Delta accept(Copier copier) {
    return copier.copy(this);
  }

  @Override
  public String getValue() {
    return "[lambda closure: " + boundVars.get(0) + ": " + index + "]";
  }

  // Getter and setter methods
  
  public List<String> getBoundVars() {
    return boundVars;
  }

  public void addBoundVars(String boundVar) {
    boundVars.add(boundVar);
  }

  public void setBoundVars(List<String> boundVars) {
    this.boundVars = boundVars;
  }

  public Stack<ASTNode> getBody() {
    return body;
  }

  public void setBody(Stack<ASTNode> body) {
    this.body = body;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Environment getLinkedEnvironment() {
    return linkedEnvironment;
  }

  public void setLinkedEnvironment(Environment linkedEnvironment) {
    this.linkedEnvironment = linkedEnvironment;
  }
}
