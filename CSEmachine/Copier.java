package CSEmachine;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import parser.ASTNode;

// Copier class responsible for creating deep copies of ASTNode subclasses
public class Copier {

  // Method to create a deep copy of a Beta node
  public Beta copy(Beta beta) {
    Beta copy = new Beta();  // Create a new Beta node

    if (beta.getChild() != null)   // Copy the child node
      copy.setChild(beta.getChild().accept(this));

    if (beta.getSibling() != null)   // Copy the sibling node
      copy.setSibling(beta.getSibling().accept(this));

    copy.setType(beta.getType());
    copy.setValue(beta.getValue());

    // Copy the then part of the Beta node
    Stack<ASTNode> thenBodyCopy = new Stack<ASTNode>();
    for (ASTNode thenBodyElement : beta.getThenPart()) {
      thenBodyCopy.add(thenBodyElement.accept(this));
    }

    copy.setThenPart(thenBodyCopy);

    // Copy the else part of the Beta node
    Stack<ASTNode> elseBodyCopy = new Stack<ASTNode>();
    for (ASTNode elseBodyElement : beta.getElsePart()) {
      elseBodyCopy.add(elseBodyElement.accept(this));
    }

    copy.setElsePart(elseBodyCopy);

    return copy;
  }

  // Method to create a deep copy of a Delta node
  public Delta copy(Delta delta) {
    Delta copy = new Delta();   // Create a new Delta node

    if (delta.getChild() != null)      // Copy the child node
      copy.setChild(delta.getChild().accept(this));

    if (delta.getSibling() != null)     // Copy the sibling node
      copy.setSibling(delta.getSibling().accept(this));

    copy.setType(delta.getType());
    copy.setValue(delta.getValue());
    copy.setIndex(delta.getIndex());

    // Copy the body of the Delta node
    Stack<ASTNode> bodyCopy = new Stack<ASTNode>();
    for (ASTNode bodyElement : delta.getBody()) {
      bodyCopy.add(bodyElement.accept(this));
    }

    copy.setBody(bodyCopy);

    // Copy the list of bound variables
    List<String> boundVarsCopy = new ArrayList<String>();
    boundVarsCopy.addAll(delta.getBoundVars());

    copy.setBoundVars(boundVarsCopy);
    copy.setLinkedEnvironment(delta.getLinkedEnvironment());

    return copy;
  }

  // Method to create a deep copy of an ASTNode
  public ASTNode copy(ASTNode astNode) {
    ASTNode copy = new ASTNode();  // Create a new ASTNode

    if (astNode.getChild() != null)      // Copy the child node
      copy.setChild(astNode.getChild().accept(this));

    if (astNode.getSibling() != null)      // Copy the sibling node
      copy.setSibling(astNode.getSibling().accept(this));

    copy.setType(astNode.getType());
    copy.setValue(astNode.getValue());

    return copy;

  }

  // Method to create a deep copy of an Eta node
  public Eta copy(Eta eta) {
    Eta copy = new Eta();  // Create a new Eta node

    if (eta.getChild() != null)       // Copy the child node
      copy.setChild(eta.getChild().accept(this));

    if (eta.getSibling() != null)    // Copy the sibling node
      copy.setSibling(eta.getSibling().accept(this));

    copy.setType(eta.getType());
    copy.setValue(eta.getValue());
    copy.setDelta(eta.getDelta().accept(this));

    return copy;
  }

  // Method to create a deep copy of a Tuple node
  public Tuple copy(Tuple tuple) {
    Tuple copy = new Tuple();  // Create a new Tuple node

    if (tuple.getChild() != null)            // Copy the child node
      copy.setChild(tuple.getChild().accept(this));

    if (tuple.getSibling() != null)        // Copy the sibling node
      copy.setSibling(tuple.getSibling().accept(this));

    copy.setType(tuple.getType());
    copy.setValue(tuple.getValue());

    return copy;
  }
}
