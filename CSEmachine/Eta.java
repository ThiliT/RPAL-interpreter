package CSEmachine;
import parser.ASTNode;
import parser.ASTNodeType;

public class Eta extends ASTNode {
  private Delta delta;

  public Eta() {
    setType(ASTNodeType.ETA);
  }

  @Override
  public String getValue() {
    return "[Closure: " + delta.getBoundVars().get(0) + ": " + delta.getIndex() + "]";
  }

  public Delta getDelta() {
    return delta;
  }

  public void setDelta(Delta delta) {
    this.delta = delta;
  }

  public Eta accept(Copier copier) {
    return copier.copy(this);
  }

}
