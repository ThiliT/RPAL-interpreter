package CSEmachine;
import parser.ASTNode;
import parser.ASTNodeType;

// Represents an Eta node in the abstract syntax tree (AST)
public class Eta extends ASTNode {
  private Delta delta;

  // Constructor
  public Eta() {
    setType(ASTNodeType.ETA);
  }

  @Override
  public String getValue() {
    return "[Closure: " + delta.getBoundVars().get(0) + ": " + delta.getIndex() + "]";
  }

  // Getter for the associated Delta
  public Delta getDelta() {
    return delta;
  }

  // Setter for the associated Delta
  public void setDelta(Delta delta) {
    this.delta = delta;
  }

  // Accepts a Copier visitor for copying the Eta node
  public Eta accept(Copier copier) {
    return copier.copy(this);
  }

}
