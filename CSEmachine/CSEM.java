package CSEmachine;

import java.util.Stack;

import Standardize.Standardize;
import parser.ASTNode;
import parser.ASTNodeType;

public class CSEM {

  private Stack<ASTNode> stackCSE;
  private Delta rootDelta;

  public CSEM(Standardize ast) {

    if (!ast.isStandardized())
      throw new RuntimeException("AST is not standardized!"); // Ensure that the AST is standardized

    rootDelta = ast.createDeltas();
    rootDelta.setLinkedEnvironment(new Environment()); // Set up the primitive environment
    stackCSE = new Stack<ASTNode>();

  }

  // Process binary arithmetic operations
  private void arithmeticOperations(ASTNodeType type) {
    ASTNode operand1 = stackCSE.pop();
    ASTNode operand2 = stackCSE.pop();
    ASTNode result = new ASTNode();

    result.setType(ASTNodeType.INTEGER);

    //// Perform arithmetic operation based on the operator type
    //Addition
    if (type == ASTNodeType.PLUS) {
      result.setValue(Integer.toString(Integer.parseInt(operand1.getValue()) + Integer.parseInt(operand2.getValue())));
    }

    //Subtraction
    else if (type == ASTNodeType.MINUS) {
      result.setValue(Integer.toString(Integer.parseInt(operand1.getValue()) - Integer.parseInt(operand2.getValue())));
    }

    //Multiplication
    else if (type == ASTNodeType.MULT) {
      result.setValue(Integer.toString(Integer.parseInt(operand1.getValue()) * Integer.parseInt(operand2.getValue())));
    }

    //Division
    else if (type == ASTNodeType.DIV) {
      result.setValue(Integer.toString(Integer.parseInt(operand1.getValue()) / Integer.parseInt(operand2.getValue())));
    }

    //Exponentiation
    else if (type == ASTNodeType.EXP) {
      result.setValue(Integer.toString((int) Math.pow(Integer.parseInt(operand1.getValue()), Integer.parseInt(operand2.getValue()))));
    }

    //Less than comparison
    else if (type == ASTNodeType.LS) {

      if (Integer.parseInt(operand1.getValue()) < Integer.parseInt(operand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
    }

    //Less than or equal comparison
    else if (type == ASTNodeType.LE) {
      if (Integer.parseInt(operand1.getValue()) <= Integer.parseInt(operand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;

    }

    //Greater than comparison
    else if (type == ASTNodeType.GR) {
      if (Integer.parseInt(operand1.getValue()) > Integer.parseInt(operand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
    }

    //Greater than or equal compariosn
    else if (type == ASTNodeType.GE) {
      if (Integer.parseInt(operand1.getValue()) >= Integer.parseInt(operand2.getValue()))
          pushTrueNode();
      else
          pushFalseNode();
      return;
    }

    stackCSE.push(result);
 }

   // Process binary logical operations for equality and inequality
   private void EqNeOperations(ASTNodeType type) {
    ASTNode operand1 = stackCSE.pop();
    ASTNode operand2 = stackCSE.pop();

    // If operand1 is a boolean value
    if (operand1.getType() == ASTNodeType.TRUE || operand1.getType() == ASTNodeType.FALSE) {
      compareTruthValues(operand1, operand2, type);
      return;
    }

    // If operand1 is a string
    if (operand1.getType() == ASTNodeType.STRING)
      compareStrings(operand1, operand2, type);

    // If operand1 is an integer
    else if (operand1.getType() == ASTNodeType.INTEGER)
      compareIntegers(operand1, operand2, type);

  }

  // Compare truth values
  private void compareTruthValues(ASTNode operand1, ASTNode operand2, ASTNodeType type) {
    if (operand1.getType() == operand2.getType())

      if (type == ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();

    else if (type == ASTNodeType.EQ)
      pushFalseNode();

    else
      pushTrueNode();
  }

  // Compare strings
  private void compareStrings(ASTNode operand1, ASTNode operand2, ASTNodeType type) {
    if (operand1.getValue().equals(operand2.getValue()))

      if (type == ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();

    else if (type == ASTNodeType.EQ)
      pushFalseNode();

    else
      pushTrueNode();
  }

  // Compare integers
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

  // Process binary logical operations for OR , AND
  private void orAndOperations(ASTNodeType type) {
    ASTNode operand1 = stackCSE.pop();
    ASTNode operand2 = stackCSE.pop();

    if ((operand1.getType() == ASTNodeType.TRUE || operand1.getType() == ASTNodeType.FALSE) &&
        (operand2.getType() == ASTNodeType.TRUE || operand2.getType() == ASTNodeType.FALSE)) {
      orAndTruthValues(operand1, operand2, type);
      return;
    }
  }

  // Evaluate truth values for OR and AND operations
  private void orAndTruthValues(ASTNode operand1, ASTNode operand2, ASTNodeType type) {

    //OR operation
    if (type == ASTNodeType.OR) {

      if (operand1.getType() == ASTNodeType.TRUE || operand2.getType() == ASTNodeType.TRUE)
        pushTrueNode();

      else
        pushFalseNode();
    }

    //AND operation
    else {

      if (operand1.getType() == ASTNodeType.TRUE && operand2.getType() == ASTNodeType.TRUE)
        pushTrueNode();

      else
        pushFalseNode();
    }

  }

  // Process for augmented assignment
  private void augmentedAssignment() {

    ASTNode operand1 = stackCSE.pop();
    ASTNode operand2 = stackCSE.pop();

    ASTNode childNode = operand1.getChild();

    if (childNode == null)   // If the target node has no child
      operand1.setChild(operand2);

    else {   
      // If the target node already has children    
      while (childNode.getSibling() != null)
        childNode = childNode.getSibling();

      childNode.setSibling(operand2);
    }

    operand2.setSibling(null);

    stackCSE.push(operand1);
  }

  // Process operation for logical NOT
  private void not() {
    ASTNode operand = stackCSE.pop();

    if (operand.getType() == ASTNodeType.TRUE)  // Negate the operand's truth value
      pushFalseNode();

    else
      pushTrueNode();
  }

  // Process operation for arithmetic negation
  private void neg() {
    ASTNode operand = stackCSE.pop();
    ASTNode result = new ASTNode();

    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(-1 * Integer.parseInt(operand.getValue())));
    stackCSE.push(result);
  }

  //Process for evaluating keywords
  private boolean evaluateKeywords(ASTNode operator, ASTNode operand, Stack<ASTNode> existingControlStack) {
    String operatorValue = operator.getValue();

     // if the operator is "Isstring"
    if (operatorValue.equals("Isstring")) {
        checkTypeAndPushTrueOrFalse(operand, ASTNodeType.STRING);
        return true;
    }
    
    // if the operator is "Isinteger"
    else if (operatorValue.equals("Isinteger")) {
        checkTypeAndPushTrueOrFalse(operand, ASTNodeType.INTEGER);
        return true;
    }
    
    // if the operator is "Isfunction"
    else if (operatorValue.equals("Isfunction")) {
        checkTypeAndPushTrueOrFalse(operand, ASTNodeType.DELTA);
        return true;
    }
    

    // if the operator is "Istruthvalue"
    else if (operatorValue.equals("Istruthvalue")) {
        if (operand.getType() == ASTNodeType.TRUE || operand.getType() == ASTNodeType.FALSE)
            pushTrueNode();

        else
            pushFalseNode();
        return true;
    }
    
    // if the operator is "Null"
    else if (operatorValue.equals("Null")) {
        isNullTuple(operand);
        return true;
    }
    
    // if the operator is "Isdummy"
    else if (operatorValue.equals("Isdummy")) {
        checkTypeAndPushTrueOrFalse(operand, ASTNodeType.DUMMY);
        return true;
    }
    
    // if the operator is "Istuple"
    else if (operatorValue.equals("Istuple")) {
        checkTypeAndPushTrueOrFalse(operand, ASTNodeType.TUPLE);
        return true;
    }
    
    // if the operator is "Stem"
    else if (operatorValue.equals("Stem")) {
        stem(operand);
        return true;
    }
    
    // if the operator is "Stern"
    else if (operatorValue.equals("Stern")) {
       stern(operand);
        return true;
    }
    
    // if the operator is "Conc" or "conc"
    else if (operatorValue.equals("Conc") || operatorValue.equals("conc")) {
        conc(operand, existingControlStack);
        return true;
    }
    
    // if the operator is "Print" or "print"
    else if (operatorValue.equals("Print") || operatorValue.equals("print")) {
        printNodeValue(operand);
        pushDummyNode();
        return true;
    }
    
    // if the operator is "ItoS"
    else if (operatorValue.equals("ItoS")) {
        itos(operand);
        return true;
    }
    
    // if the operator is "Order"
    else if (operatorValue.equals("Order")) {
        order(operand);
        return true;
    }
    
    else {
        return false;
    }
  }

  // Check the type of a node and push true or false onto the stack accordingly
  private void checkTypeAndPushTrueOrFalse(ASTNode operand, ASTNodeType type) {
    
    if (operand.getType() == type) //if the node's type matches the specified type
      pushTrueNode();

    else
      pushFalseNode();
  }

  // Push a true node onto the stack
  private void pushTrueNode() {

    ASTNode trueNode = new ASTNode();
    trueNode.setType(ASTNodeType.TRUE);
    trueNode.setValue("true");
    stackCSE.push(trueNode);

  }

  // Push a false node onto the stack
  private void pushFalseNode() {

    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.FALSE);
    falseNode.setValue("false");
    stackCSE.push(falseNode);

  }
 // Push a dummy node onto the stack
  private void pushDummyNode() {

    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.DUMMY);
    stackCSE.push(falseNode);

  }

  // Process for stem function
  private void stem(ASTNode operand) {

    String value = operand.getValue();

    if (value.isEmpty())          // If the value is empty
      operand.setValue("");

    else
      operand.setValue(value.substring(0, 1));

      stackCSE.push(operand);
  }

  // Process for stern function
  private void stern(ASTNode operand) {

    String value = operand.getValue();

    // If the value is empty or has only one character
    if (value.isEmpty() || value.length() == 1)
      operand.setValue("");

    else
      operand.setValue(value.substring(1));

      stackCSE.push(operand);
  }

  // Process for conc function
  private void conc(ASTNode operand1, Stack<ASTNode> currentControlStack) {
    currentControlStack.pop();

    ASTNode operand2 = stackCSE.pop();

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.STRING);
    result.setValue(operand1.getValue() + operand2.getValue());

    stackCSE.push(result);
  }

  // Process for itos function
  private void itos(ASTNode operand) {

    operand.setType(ASTNodeType.STRING);
    stackCSE.push(operand);
  }

  // Process for order function
  private void order(ASTNode operand) {

    int numChildren = getNumChildren(operand);

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(numChildren));

    stackCSE.push(result);
  }

  // Process for isNullTuple function
  private void isNullTuple(ASTNode operand) {

    if (getNumChildren(operand) == 0)
      pushTrueNode();

    else
      pushFalseNode();
  }


  // Retrieve the nth child of a tuple
  private ASTNode getNthTupleChild(Tuple tupleNode, int n) {
    ASTNode childNode = tupleNode.getChild();

    for (int i = 1; i < n; ++i) { // index starting from 1

      if (childNode == null)    // If the child node is null
        break;

      childNode = childNode.getSibling();
    }
    return childNode;
  }

  // Process identifiers based on existing environment
  private void processIdentifiers(ASTNode node, Environment existingEnvironment) {

    if (existingEnvironment.lookup(node.getValue()) != null) // CSE rule 1
      stackCSE.push(existingEnvironment.lookup(node.getValue()));

    else if (isReservedIdentifier(node.getValue()))
      stackCSE.push(node);
  }

  // CSE rule 3
  private void applyGamma(Delta currentDelta, ASTNode node, Environment currentEnv, Stack<ASTNode> currentControlStack) {

    ASTNode operator = stackCSE.pop();
    ASTNode operand = stackCSE.pop();

    // If the operator is a delta
    if (operator.getType() == ASTNodeType.DELTA) {

      Delta nextDelta = (Delta) operator;
      Environment newEnvironment = new Environment();
      newEnvironment.setParent(nextDelta.getLinkedEnvironment());

      // CSE rule 4
      if (nextDelta.getBoundVars().size() == 1) {
        newEnvironment.addMapping(nextDelta.getBoundVars().get(0), operand);
      }
      // CSE rule 11
      else {

        for (int i = 0; i < nextDelta.getBoundVars().size(); i++) {
          newEnvironment.addMapping(nextDelta.getBoundVars().get(i), getNthTupleChild((Tuple) operand, i + 1));
        }
      }

      controlStack(nextDelta, newEnvironment);
      return;
    }
    
    // CSE rule 12
    // If the operator is a YSTAR
    else if (operator.getType() == ASTNodeType.YSTAR) {  
      Eta etaNode = new Eta();
      etaNode.setDelta((Delta) operand);
      stackCSE.push(etaNode);
      return;
    }
    
    // CSE rule 13
    // If the operator is an ETA
    else if (operator.getType() == ASTNodeType.ETA) {
      stackCSE.push(operand);
      stackCSE.push(operator);
      stackCSE.push(((Eta) operator).getDelta());
      currentControlStack.push(node);
      currentControlStack.push(node);
      return;
    }
    
    // If the operator is a TUPLE
    else if (operator.getType() == ASTNodeType.TUPLE) {
      tupleSelection((Tuple) operator, operand);
      return;
    }
    
    // If the operator is any other keyword
    else if (evaluateKeywords(operator, operand, currentControlStack))
      return;
  }

  // CSE rule 6
  // Apply binary operations
  private boolean applyBinaryOperation(ASTNode operator) {
    switch (operator.getType()) {
      case PLUS:
      case MINUS:
      case MULT:
      case DIV:
      case EXP:
      case LS:
      case LE:
      case GR:
      case GE:
      arithmeticOperations(operator.getType());
        return true;

      case EQ:
      case NE:
      EqNeOperations(operator.getType());
        return true;

      case OR:
      case AND:
      orAndOperations(operator.getType());
        return true;

      case AUG:
        augmentedAssignment();
        return true;

      default:
        return false;
    }
  }

  // CSE rule 7
  // Apply unary operations 
  private boolean applyUnaryOperation(ASTNode operator) {
    switch (operator.getType()) {
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
  // Process the BETA node
  private void processBeta(Beta node, Stack<ASTNode> existingControlStack) {
    ASTNode conditionResultNode = stackCSE.pop();

    if (conditionResultNode.getType() == ASTNodeType.TRUE)
      existingControlStack.addAll(node.getThenPart());

    else
      existingControlStack.addAll(node.getElsePart());
  }

  // CSE rule 9
  // Create a tuple
  private void createTuple(ASTNode node) {

    int numChildren = getNumChildren(node);
    Tuple tupleNode = new Tuple();

    if (numChildren == 0) {      // If there are no children
      stackCSE.push(tupleNode);
      return;
    }

    ASTNode childNode = null, tempNode = null;

    for (int i = 0; i < numChildren; ++i) {

      if (childNode == null)
        childNode = stackCSE.pop();

      else if (tempNode == null) {
        tempNode = stackCSE.pop();
        childNode.setSibling(tempNode);
      }
      
      else {
        tempNode.setSibling(stackCSE.pop());
        tempNode = tempNode.getSibling();
      }
    }

    tempNode.setSibling(null);
    tupleNode.setChild(childNode);
    stackCSE.push(tupleNode);

  }

  // CSE rule 10
  // Perform tuple selection
  private void tupleSelection(Tuple operator, ASTNode operand) {

    ASTNode result = getNthTupleChild(operator, Integer.parseInt(operand.getValue()));

    stackCSE.push(result);
  }

  // Get the number of children of a node
  private int getNumChildren(ASTNode node) {

    int numChildren = 0;
    ASTNode childNode = node.getChild();

    while (childNode != null) {
      numChildren++;
      childNode = childNode.getSibling();
    }
    return numChildren;

  }

  // Print the value of the node
  private void printNodeValue(ASTNode operand) {

    String evaluationResult = operand.getValue();
    evaluationResult = evaluationResult.replace("\\t", "\t");
    evaluationResult = evaluationResult.replace("\\n", "\n");
    System.out.print(evaluationResult);

  }

  private boolean isReservedIdentifier(String value) {
    switch (value) {
      // List of reserved identifiers
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

  // Process the current node in the control stack
  private void processExistingNode(Delta delta, Environment environment, Stack<ASTNode> controlStack) {
    ASTNode node = controlStack.pop();

    if (applyBinaryOperation(node))
      return;

    else if (applyUnaryOperation(node))
      return;

    else {
      switch (node.getType()) {

        case IDENTIFIER:
          processIdentifiers(node, environment);
          break;
        case NIL:
        case TAU:
          createTuple(node);
          break;
        case BETA:
          processBeta((Beta) node, controlStack);
          break;
        case GAMMA:
          applyGamma(delta, node, environment, controlStack);
          break;
        case DELTA:
          ((Delta) node).setLinkedEnvironment(environment); // CSE rule 2
          stackCSE.push(node);
          break;
        default:
          stackCSE.push(node);
          break;
      }
    }
  }

  // Process the controlStack
  private void controlStack(Delta existingDelta, Environment existingEnvironment) {

    Stack<ASTNode> controlStack = new Stack<ASTNode>();
    controlStack.addAll(existingDelta.getBody());

    while (!controlStack.isEmpty())
      processExistingNode(existingDelta, existingEnvironment, controlStack);
  }

  // Evaluate the program by starting from the root delta
  public void evaluateProgram() {
    controlStack(rootDelta, rootDelta.getLinkedEnvironment());
  }
}