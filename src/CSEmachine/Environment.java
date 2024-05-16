package CSEmachine;

import java.util.HashMap;
import java.util.Map;

import parser.ASTNode;

// Environment class representing the lexical environment for variable bindings

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


  // Method to lookup the binding of a variable in the environment's mappings
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
// Method to add a new mapping of a variable to a value in the environment
  public void addMapping(String key, ASTNode value) {
    nameValues.put(key, value);
  }
}