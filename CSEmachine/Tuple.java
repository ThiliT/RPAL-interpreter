package CSEmachine;
import parser.ASTNode;
import parser.ASTNodeType;

// Represents a Tuple node in the abstract syntax tree (AST)
public class Tuple extends ASTNode {

  // Constructor
  public Tuple() {
    setType(ASTNodeType.TUPLE);
  }

  @Override
  public String getValue() {
    ASTNode childNode = getChild();

    if (childNode == null)
      return "nil";   // Returns "nil" if the Tuple is empty

    String printValue = "(";

    // Constructs a string representation of the Tuple's values
    while (childNode.getSibling() != null) {
      printValue += childNode.getValue() + ", ";
      childNode = childNode.getSibling();
    }

    printValue += childNode.getValue() + ")";

    return printValue;
  }

  // Accepts a Copier visitor for copying the Tuple node
  public Tuple accept(Copier copier) {
    return copier.copy(this);
  }

}
