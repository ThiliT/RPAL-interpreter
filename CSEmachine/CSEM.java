package CSEmachine;

import java.util.Stack;

import Standardize.Standardize;
import parser.ASTNode;
import parser.ASTNodeType;

public class CSEM {

  private Stack<ASTNode> CSEStack;
  private Delta rootDelta;

  public CSEM(Standardize ast) {

    if (!ast.isStandardized())
      throw new RuntimeException("AST is not standardized!"); // AsT should be standardized earlier

    rootDelta = ast.createDeltas();
    rootDelta.setLinkedEnvironment(new Environment()); // primitive environment
    CSEStack = new Stack<ASTNode>();

  }

  // processing for arithmetic operations
  private void binaryArithmeticOp(ASTNodeType type) {
    ASTNode rand1 = CSEStack.pop();
    ASTNode rand2 = CSEStack.pop();
    ASTNode result = new ASTNode();

    result.setType(ASTNodeType.INTEGER);


    if (type == ASTNodeType.PLUS) {
      result.setValue(Integer.toString(Integer.parseInt(rand1.getValue()) + Integer.parseInt(rand2.getValue())));
    }

    else if (type == ASTNodeType.MINUS) {
      result.setValue(Integer.toString(Integer.parseInt(rand1.getValue()) - Integer.parseInt(rand2.getValue())));
    }

    else if (type == ASTNodeType.MULT) {
      result.setValue(Integer.toString(Integer.parseInt(rand1.getValue()) * Integer.parseInt(rand2.getValue())));
    }

    else if (type == ASTNodeType.DIV) {
      result.setValue(Integer.toString(Integer.parseInt(rand1.getValue()) / Integer.parseInt(rand2.getValue())));
    }

  else if (type == ASTNodeType.EXP) {
      result.setValue(Integer.toString((int) Math.pow(Integer.parseInt(rand1.getValue()), Integer.parseInt(rand2.getValue()))));
    }

    else if (type == ASTNodeType.LS) {

      if (Integer.parseInt(rand1.getValue()) < Integer.parseInt(rand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
    }

    else if (type == ASTNodeType.LE) {
      if (Integer.parseInt(rand1.getValue()) <= Integer.parseInt(rand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;

    }

    else if (type == ASTNodeType.GR) {
      if (Integer.parseInt(rand1.getValue()) > Integer.parseInt(rand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
    }

    else if (type == ASTNodeType.GE) {
      if (Integer.parseInt(rand1.getValue()) >= Integer.parseInt(rand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
  }

  CSEStack.push(result);
}

  // processing for eq, ne
  private void binaryLogicalEqNeOp(ASTNodeType type) {
    ASTNode rand1 = CSEStack.pop();
    ASTNode rand2 = CSEStack.pop();

    if (rand1.getType() == ASTNodeType.TRUE || rand1.getType() == ASTNodeType.FALSE) {
      compareTruthValues(rand1, rand2, type);
      return;
    }

    if (rand1.getType() == ASTNodeType.STRING)
      compareStrings(rand1, rand2, type);

    else if (rand1.getType() == ASTNodeType.INTEGER)
      compareIntegers(rand1, rand2, type);

  }

  private void compareTruthValues(ASTNode rand1, ASTNode rand2, ASTNodeType type) {
    if (rand1.getType() == rand2.getType())

      if (type == ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();

    else if (type == ASTNodeType.EQ)
      pushFalseNode();

    else
      pushTrueNode();
  }

  private void compareStrings(ASTNode rand1, ASTNode rand2, ASTNodeType type) {
    if (rand1.getValue().equals(rand2.getValue()))

      if (type == ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();

    else if (type == ASTNodeType.EQ)
      pushFalseNode();

    else
      pushTrueNode();
  }

  private void compareIntegers(ASTNode rand1, ASTNode rand2, ASTNodeType type) {

    if (Integer.parseInt(rand1.getValue()) == Integer.parseInt(rand2.getValue()))

      if (type == ASTNodeType.EQ)
        pushTrueNode();

      else
        pushFalseNode();

    else if (type == ASTNodeType.EQ)
      pushFalseNode();

    else
      pushTrueNode();
  }

  // processing for or, and
  private void binaryLogicalOrAndOp(ASTNodeType type) {
    ASTNode rand1 = CSEStack.pop();
    ASTNode rand2 = CSEStack.pop();

    if ((rand1.getType() == ASTNodeType.TRUE || rand1.getType() == ASTNodeType.FALSE) &&
        (rand2.getType() == ASTNodeType.TRUE || rand2.getType() == ASTNodeType.FALSE)) {
      orAndTruthValues(rand1, rand2, type);
      return;
    }

  }

  private void orAndTruthValues(ASTNode rand1, ASTNode rand2, ASTNodeType type) {

    if (type == ASTNodeType.OR) {

      if (rand1.getType() == ASTNodeType.TRUE || rand2.getType() == ASTNodeType.TRUE)
        pushTrueNode();

      else
        pushFalseNode();
    }

    else {

      if (rand1.getType() == ASTNodeType.TRUE && rand2.getType() == ASTNodeType.TRUE)
        pushTrueNode();

      else
        pushFalseNode();
    }

  }

  // processing for aug
  private void augTuples() {

    ASTNode rand1 = CSEStack.pop();
    ASTNode rand2 = CSEStack.pop();

    ASTNode childNode = rand1.getChild();

    if (childNode == null)
      rand1.setChild(rand2);
    else {

      while (childNode.getSibling() != null)
        childNode = childNode.getSibling();

      childNode.setSibling(rand2);
    }

    rand2.setSibling(null);

    CSEStack.push(rand1);
  }

  private void not() {
    ASTNode rand = CSEStack.pop();

    if (rand.getType() == ASTNodeType.TRUE)
      pushFalseNode();

    else
      pushTrueNode();
  }

  // processing for neg
  private void neg() {
    ASTNode rand = CSEStack.pop();
    ASTNode result = new ASTNode();

    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(-1 * Integer.parseInt(rand.getValue())));
    CSEStack.push(result);
  }

  private boolean evaluateKeywords(ASTNode rator, ASTNode rand, Stack<ASTNode> existingControlStack) {
    String ratorValue = rator.getValue();

    if (ratorValue.equals("Isstring")) {
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.STRING);
        return true;
    }
    
    else if (ratorValue.equals("Isinteger")) {
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.INTEGER);
        return true;
    }
    
    else if (ratorValue.equals("Isfunction")) {
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DELTA);
        return true;
    }
    
    else if (ratorValue.equals("Istruthvalue")) {
        if (rand.getType() == ASTNodeType.TRUE || rand.getType() == ASTNodeType.FALSE)
            pushTrueNode();

        else
            pushFalseNode();
        return true;
    }
    
    else if (ratorValue.equals("Null")) {
        isNullTuple(rand);
        return true;
    }
    
    else if (ratorValue.equals("Isdummy")) {
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DUMMY);
        return true;
    }
    
    else if (ratorValue.equals("Istuple")) {
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.TUPLE);
        return true;
    }
    
    else if (ratorValue.equals("Stem")) {
        stem(rand);
        return true;
    }
    
    else if (ratorValue.equals("Stern")) {
        stern(rand);
        return true;
    }
    
    else if (ratorValue.equals("Conc") || ratorValue.equals("conc")) {
        conc(rand, existingControlStack);
        return true;
    }
    
    else if (ratorValue.equals("Print") || ratorValue.equals("print")) {
        printNodeValue(rand);
        pushDummyNode();
        return true;
    }
    
    else if (ratorValue.equals("ItoS")) {
        itos(rand);
        return true;
    }
    
    else if (ratorValue.equals("Order")) {
        order(rand);
        return true;
    }
    
    else {
        return false;
    }
}

  private void checkTypeAndPushTrueOrFalse(ASTNode rand, ASTNodeType type) {
    if (rand.getType() == type)
      pushTrueNode();

    else
      pushFalseNode();
  }

  private void pushTrueNode() {

    ASTNode trueNode = new ASTNode();
    trueNode.setType(ASTNodeType.TRUE);
    trueNode.setValue("true");
    CSEStack.push(trueNode);

  }

  private void pushFalseNode() {

    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.FALSE);
    falseNode.setValue("false");
    CSEStack.push(falseNode);

  }

  private void pushDummyNode() {

    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.DUMMY);
    CSEStack.push(falseNode);

  }

  // processing for stem function
  private void stem(ASTNode rand) {

    if (rand.getValue().isEmpty())
      rand.setValue("");

    else
      rand.setValue(rand.getValue().substring(0, 1));

    CSEStack.push(rand);
  }

  // processing ofr stern function
  private void stern(ASTNode rand) {

    if (rand.getValue().isEmpty() || rand.getValue().length() == 1)
      rand.setValue("");

    else
      rand.setValue(rand.getValue().substring(1));

    CSEStack.push(rand);
  }

  // processing for conc function
  private void conc(ASTNode rand1, Stack<ASTNode> currentControlStack) {
    currentControlStack.pop();

    ASTNode rand2 = CSEStack.pop();

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.STRING);
    result.setValue(rand1.getValue() + rand2.getValue());

    CSEStack.push(result);
  }

  private void itos(ASTNode rand) {

    rand.setType(ASTNodeType.STRING);
    CSEStack.push(rand);

  }

  // processing for order function
  private void order(ASTNode rand) {

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(getNumChildren(rand)));

    CSEStack.push(result);
  }

  // processing for isNullTuple function
  private void isNullTuple(ASTNode rand) {

    if (getNumChildren(rand) == 0)
      pushTrueNode();

    else
      pushFalseNode();
  }

  // Taking nth element of the tuple
  private ASTNode getNthTupleChild(Tuple tupleNode, int n) {
    ASTNode childNode = tupleNode.getChild();

    for (int i = 1; i < n; ++i) { // index starting from 1

      if (childNode == null)
        break;

      childNode = childNode.getSibling();
    }
    return childNode;
  }

  private void processIdentifiers(ASTNode node, Environment existingEnvironment) {

    if (existingEnvironment.lookup(node.getValue()) != null) // CSE rule 1
      CSEStack.push(existingEnvironment.lookup(node.getValue()));

    else if (isReservedIdentifier(node.getValue()))
      CSEStack.push(node);
  }

  // CSE rule 3
  private void applyGamma(Delta currentDelta, ASTNode node, Environment currentEnv, Stack<ASTNode> currentControlStack) {

    ASTNode rator = CSEStack.pop();
    ASTNode rand = CSEStack.pop();

    if (rator.getType() == ASTNodeType.DELTA) {

      Delta nextDelta = (Delta) rator;
      Environment newEnv = new Environment();
      newEnv.setParent(nextDelta.getLinkedEnvironment());

      // CSE rule 4
      if (nextDelta.getBoundVars().size() == 1) {
        newEnv.addMapping(nextDelta.getBoundVars().get(0), rand);
      }
      // CSE rule 11
      else {

        for (int i = 0; i < nextDelta.getBoundVars().size(); i++) {
          newEnv.addMapping(nextDelta.getBoundVars().get(i), getNthTupleChild((Tuple) rand, i + 1));
        }
      }

      controlStack(nextDelta, newEnv);
      return;
    }
    
    else if (rator.getType() == ASTNodeType.YSTAR) {
      // CSE rule 12

      Eta etaNode = new Eta();
      etaNode.setDelta((Delta) rand);
      CSEStack.push(etaNode);
      return;
    }
    
    else if (rator.getType() == ASTNodeType.ETA) {
      // CSE rule 13
      CSEStack.push(rand);
      CSEStack.push(rator);
      CSEStack.push(((Eta) rator).getDelta());
      currentControlStack.push(node);
      currentControlStack.push(node);
      return;
    }
    
    else if (rator.getType() == ASTNodeType.TUPLE) {
      tupleSelection((Tuple) rator, rand);
      return;
    }
    
    else if (evaluateKeywords(rator, rand, currentControlStack))
      return;
  }

  // CSE rule 6
  private boolean applyBinaryOperation(ASTNode rator) {
    switch (rator.getType()) {
      case PLUS:
      case MINUS:
      case MULT:
      case DIV:
      case EXP:
      case LS:
      case LE:
      case GR:
      case GE:
        binaryArithmeticOp(rator.getType());
        return true;

      case EQ:
      case NE:
        binaryLogicalEqNeOp(rator.getType());
        return true;

      case OR:
      case AND:
        binaryLogicalOrAndOp(rator.getType());
        return true;

      case AUG:
        augTuples();
        return true;

      default:
        return false;
    }
  }

  // CSE rule 7
  private boolean applyUnaryOperation(ASTNode rator) {
    switch (rator.getType()) {
      case NOT:
        not();
        return true;

      case NEG:
        neg();
        return true;

      default:
        return false;
    }
  }

  // CSE rule 8
  private void processBeta(Beta node, Stack<ASTNode> existingControlStack) {
    ASTNode conditionResultNode = CSEStack.pop();

    if (conditionResultNode.getType() == ASTNodeType.TRUE)
      existingControlStack.addAll(node.getThenPart());

    else
      existingControlStack.addAll(node.getElsePart());
  }

  // CSE rule 9
  private void createTuple(ASTNode node) {

    int numChildren = getNumChildren(node);
    Tuple tupleNode = new Tuple();

    if (numChildren == 0) {
      CSEStack.push(tupleNode);
      return;
    }

    ASTNode childNode = null, tempNode = null;

    for (int i = 0; i < numChildren; ++i) {

      if (childNode == null)
        childNode = CSEStack.pop();

      else if (tempNode == null) {
        tempNode = CSEStack.pop();
        childNode.setSibling(tempNode);
      }
      
      else {
        tempNode.setSibling(CSEStack.pop());
        tempNode = tempNode.getSibling();
      }
    }

    tempNode.setSibling(null);
    tupleNode.setChild(childNode);
    CSEStack.push(tupleNode);

  }

  // CSE rule 10
  private void tupleSelection(Tuple rator, ASTNode rand) {

    ASTNode result = getNthTupleChild(rator, Integer.parseInt(rand.getValue()));

    CSEStack.push(result);
  }

  private int getNumChildren(ASTNode node) {

    int numChildren = 0;
    ASTNode childNode = node.getChild();

    while (childNode != null) {
      numChildren++;
      childNode = childNode.getSibling();
    }
    return numChildren;

  }

  private void printNodeValue(ASTNode rand) {

    String evaluationResult = rand.getValue();
    evaluationResult = evaluationResult.replace("\\t", "\t");
    evaluationResult = evaluationResult.replace("\\n", "\n");
    System.out.print(evaluationResult);

  }

  private boolean isReservedIdentifier(String value) {
    switch (value) {
      case "Isinteger":
      case "Isstring":
      case "Istuple":
      case "Isdummy":
      case "Istruthvalue":
      case "Isfunction":
      case "ItoS":
      case "Order":
      case "Conc":
      case "conc":
      case "Stern":
      case "Stem":
      case "Null":
      case "Print":
      case "print":
      case "neg":
        return true;
    }
    return false;
  }

  // processing the current node
  private void processExistingNode(Delta existingDelta, Environment existingEnvironment, Stack<ASTNode> existingControlStack) {
    ASTNode node = existingControlStack.pop();

    if (applyBinaryOperation(node))
      return;

    else if (applyUnaryOperation(node))
      return;

    else {
      switch (node.getType()) {

        case IDENTIFIER:
          processIdentifiers(node, existingEnvironment);
          break;
        case NIL:
        case TAU:
          createTuple(node);
          break;
        case BETA:
          processBeta((Beta) node, existingControlStack);
          break;
        case GAMMA:
          applyGamma(existingDelta, node, existingEnvironment, existingControlStack);
          break;
        case DELTA:
          ((Delta) node).setLinkedEnvironment(existingEnvironment); // CSE rule 2
          CSEStack.push(node);
          break;
        default:
          CSEStack.push(node);
          break;
      }
    }
  }

  // processing the controlStack
  private void controlStack(Delta existingDelta, Environment existingEnvironment) {

    Stack<ASTNode> controlStack = new Stack<ASTNode>();
    controlStack.addAll(existingDelta.getBody());

    while (!controlStack.isEmpty())
      processExistingNode(existingDelta, existingEnvironment, controlStack);
  }

  // evaluating the program
  public void evaluateProgram() {
    controlStack(rootDelta, rootDelta.getLinkedEnvironment());
  }
}