package CSEmachine;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import parser.ASTNode;

public class Copier {

  public Beta copy(Beta beta) {
    Beta copy = new Beta();

    if (beta.getChild() != null)
      copy.setChild(beta.getChild().accept(this));

    if (beta.getSibling() != null)
      copy.setSibling(beta.getSibling().accept(this));

    copy.setType(beta.getType());
    copy.setValue(beta.getValue());

    Stack<ASTNode> thenBodyCopy = new Stack<ASTNode>();

    for (ASTNode thenBodyElement : beta.getThenPart()) {

      thenBodyCopy.add(thenBodyElement.accept(this));

    }

    copy.setThenPart(thenBodyCopy);

    Stack<ASTNode> elseBodyCopy = new Stack<ASTNode>();

    for (ASTNode elseBodyElement : beta.getElsePart()) {

      elseBodyCopy.add(elseBodyElement.accept(this));

    }

    copy.setElsePart(elseBodyCopy);

    return copy;
  }

  public Delta copy(Delta delta) {
    Delta copy = new Delta();

    if (delta.getChild() != null)
      copy.setChild(delta.getChild().accept(this));

    if (delta.getSibling() != null)
      copy.setSibling(delta.getSibling().accept(this));

    copy.setType(delta.getType());
    copy.setValue(delta.getValue());
    copy.setIndex(delta.getIndex());

    Stack<ASTNode> bodyCopy = new Stack<ASTNode>();

    for (ASTNode bodyElement : delta.getBody()) {

      bodyCopy.add(bodyElement.accept(this));

    }

    copy.setBody(bodyCopy);

    List<String> boundVarsCopy = new ArrayList<String>();
    boundVarsCopy.addAll(delta.getBoundVars());

    copy.setBoundVars(boundVarsCopy);
    copy.setLinkedEnvironment(delta.getLinkedEnvironment());

    return copy;
  }

  public ASTNode copy(ASTNode astNode) {
    ASTNode copy = new ASTNode();

    if (astNode.getChild() != null)
      copy.setChild(astNode.getChild().accept(this));

    if (astNode.getSibling() != null)
      copy.setSibling(astNode.getSibling().accept(this));

    copy.setType(astNode.getType());
    copy.setValue(astNode.getValue());

    return copy;

  }

  public Eta copy(Eta eta) {
    Eta copy = new Eta();

    if (eta.getChild() != null)
      copy.setChild(eta.getChild().accept(this));

    if (eta.getSibling() != null)
      copy.setSibling(eta.getSibling().accept(this));

    copy.setType(eta.getType());
    copy.setValue(eta.getValue());
    copy.setDelta(eta.getDelta().accept(this));

    return copy;
  }

  public Tuple copy(Tuple tuple) {
    Tuple copy = new Tuple();

    if (tuple.getChild() != null)
      copy.setChild(tuple.getChild().accept(this));

    if (tuple.getSibling() != null)
      copy.setSibling(tuple.getSibling().accept(this));

    copy.setType(tuple.getType());
    copy.setValue(tuple.getValue());

    return copy;
  }
}
