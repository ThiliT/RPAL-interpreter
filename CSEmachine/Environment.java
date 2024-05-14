package CSEmachine;

import java.util.HashMap;
import java.util.Map;

import parser.ASTNode;

public class Environment {

  private Environment parent;
  private Map<String, ASTNode> nameValues;

  public Environment() {
    nameValues = new HashMap<String, ASTNode>();
  }

  public void setParent(Environment parent) {
    this.parent = parent;
  }
  public Environment getParent() {
    return parent;
  }


  // Finding the binding of the given key in the environment's mappings
  public ASTNode lookup(String key) {
    ASTNode returnVal = null;
    Map<String, ASTNode> map = nameValues;

    returnVal = map.get(key);

    if (returnVal != null)
      return returnVal.accept(new Copier());

    if (parent != null)
      return parent.lookup(key);
    else
      return null;
  }

  public void addMapping(String key, ASTNode value) {
    nameValues.put(key, value);
  }
}