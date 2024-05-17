
import java.io.IOException;

import CSEmachine.CSEM;
import Standardize.Standardize;
import parser.ParseException;
import parser.Parser;
import scanner.Scanner;

// Main class
public class myrpal {

  public static String fileName;

  public static void main(String[] args) {
    boolean astFlag = false;
    fileName = "";
    Standardize ast = null;

    for (String cmdOption : args) {

      if (cmdOption.equals("-ast"))
        astFlag = true;
      else
        fileName = cmdOption;
    }

    // only prints the result
    if (!astFlag) {
      ast = buildAST(fileName, true);
      ast.standardize();
      evaluateST(ast);
      return;
    }

    // prints ast and result
    if (astFlag) {
      if (fileName.isEmpty())
        throw new ParseException("Input a relavant file.");
      ast = buildAST(fileName, true);
      printAST(ast);
    }

  }

  private static void evaluateST(Standardize ast) {
    CSEM csem = new CSEM(ast);
    csem.evaluateProgram();
    System.out.println();
  }

  private static Standardize buildAST(String fileName, boolean printOutput) {
    Standardize ast = null;
    try {
      Scanner scanner = new Scanner(fileName);
      Parser parser = new Parser(scanner);
      ast = parser.buildAST();
    } catch (IOException e) {
      throw new ParseException("ERROR: Could not read from file: " + fileName);
    }
    return ast;
  }

  private static void printAST(Standardize ast) {
    ast.print();
  }

}
