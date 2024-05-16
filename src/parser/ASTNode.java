package parser;
import CSEmachine.Copier;

//Represents a node in an abstract syntax tree (AST)

public class ASTNode {
  private ASTNodeType type;
  private String value;
  private ASTNode child;
  private ASTNode sibling;

  public String getName() {
    return type.name();
  }

  public ASTNodeType getType() {
    return type;
  }

  public ASTNode getChild() {
    return child;
  }

  public ASTNode getSibling() {
    return sibling;
  }

  public String getValue() {
    return value;
  }


  public void setType(ASTNodeType type) {
    this.type = type;
  }

  public void setChild(ASTNode child) {
    this.child = child;
  }

  public void setSibling(ASTNode sibling) {
    this.sibling = sibling;
  }

  public void setValue(String value) {
    this.value = value;
  }


  public ASTNode accept(Copier copier) {
    return copier.copy(this);
  }

}
