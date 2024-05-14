package scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


// Combination of lexer and screener
public class Scanner {

  private BufferedReader bufferedReader;
  private String extraCharacterRead;
  private final List<String> reservedKeywords;

  public Scanner(String inputFile) throws IOException {
    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));

        // Reserved keywords
    reservedKeywords = Arrays.asList("let", "in", "within", "fn", "where", "aug", "or",
                "not", "gr", "ge", "ls", "le", "eq", "ne", "true",
                "false", "nil", "dummy", "rec", "and");
  }

  //Read the next token
  public Token readNextToken() {

    Token nextToken = null;
    String nextCharacter;

    // Check if there is an extra character from the previous token
    if (extraCharacterRead != null) {
      nextCharacter = extraCharacterRead;
      extraCharacterRead = null;
    }

    else
      nextCharacter = readNextCharacter();

      // Build token from the next character

    if (nextCharacter != null)
      nextToken = buildToken(nextCharacter);
      return nextToken;
  }

  //Read the next character from the input stream
  private String readNextCharacter() {

    String nextCharacter = null;

    try {
      int c = bufferedReader.read();

      if (c != -1) {
        nextCharacter = Character.toString((char) c);
      }

      else
        bufferedReader.close();
    }

    catch (IOException e) {
      // Handle IOException
    }
    return nextCharacter;

  }

  //Build a token based on the current character
  private Token buildToken(String currentCharacter) {
    Token nextToken = null;

    if (Regex.LetterPattern.matcher(currentCharacter).matches()) {
      nextToken = buildIdentifierToken(currentCharacter);
    }

    else if (Regex.DigitPattern.matcher(currentCharacter).matches()) {
      nextToken = buildIntegerToken(currentCharacter);
    }

    else if (Regex.OpSymbolPattern.matcher(currentCharacter).matches()) {
      nextToken = buildOperatorToken(currentCharacter);
    }

    else if (currentCharacter.equals("\'")) {
      nextToken = buildStringToken(currentCharacter);
    }

    else if (Regex.SpacePattern.matcher(currentCharacter).matches()) {
      nextToken = buildSpaceToken(currentCharacter);
    }

    else if (Regex.PunctuationPattern.matcher(currentCharacter).matches()) {
      nextToken = buildPunctuationPattern(currentCharacter);
    }
    return nextToken;

  }

  // Building identifier token
  private Token buildIdentifierToken(String currentCharacter) {

    Token identifierToken = new Token();
    identifierToken.setType(TokenType.IDENTIFIER);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {
      if (Regex.IdentifierPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      else {
      extraCharacterRead = nextCharacter; // Store the extra character for the next token
      break;
      }

    }

    String value = stringBuilder.toString();

    // Check if the identifier is a reserved keyword
    if (reservedKeywords.contains(value))
      identifierToken.setType(TokenType.RESERVED);

    identifierToken.setValue(value);
    return identifierToken;

  }

  // Building integer token
  private Token buildIntegerToken(String currentCharacter) {

    Token integerToken = new Token();
    integerToken.setType(TokenType.INTEGER);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {

      if (Regex.DigitPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      else {
        extraCharacterRead = nextCharacter; // Store the extra character for the next token
        break;
      }
    }

    integerToken.setValue(stringBuilder.toString());
    return integerToken;
  }

  //Building operator token
  private Token buildOperatorToken(String currentCharacter) {
    Token operatorToken = new Token();
    operatorToken.setType(TokenType.OPERATOR);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    // Special case for comment token
    if (currentCharacter.equals("/") && nextCharacter.equals("/"))
      return buildCommentToken(currentCharacter + nextCharacter);

    while (nextCharacter != null) {
      if (Regex.OpSymbolPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      else {
        extraCharacterRead = nextCharacter; // Store the extra character for the next token
        break;
      }
    }

    operatorToken.setValue(stringBuilder.toString());
    return operatorToken;
  }

  //Building string token
  private Token buildStringToken(String currentCharacter) {
    Token stringToken = new Token();
    stringToken.setType(TokenType.STRING);
    StringBuilder stringBuilder = new StringBuilder("");
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {

      if (nextCharacter.equals("\'")) {
        stringToken.setValue(stringBuilder.toString());
        return stringToken;
      }

      else if (Regex.StringPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);  // If the current character is a valid string character
        nextCharacter = readNextCharacter();
      }
    }

    return null;
  }

  // Building space token
  private Token buildSpaceToken(String currentCharacter) {

    Token deleteToken = new Token();
    deleteToken.setType(TokenType.DELETE);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {

      if (Regex.SpacePattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      else {
        // If a non-space character is encountered, store it as the extra character for the next token
        extraCharacterRead = nextCharacter;
        break;
      }
    }

    deleteToken.setValue(stringBuilder.toString());  // Set the value of the space token to the constructed space
    return deleteToken;
  }

  // Building comment token
  private Token buildCommentToken(String currentCharacter) {
    Token commentToken = new Token();
    commentToken.setType(TokenType.DELETE);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {
      // If the current character is part of the comment
      if (Regex.CommentPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      else if (nextCharacter.equals("\n"))  // If a newline character is encountered
        break;
    }

    commentToken.setValue(stringBuilder.toString());
    return commentToken;
  }

  // Building punctuation token
  private Token buildPunctuationPattern(String currentCharacter) {

    Token punctuationToken = new Token();
    punctuationToken.setValue(currentCharacter);

    switch (currentCharacter) {
      case "(":
          punctuationToken.setType(TokenType.L_PAREN);
          break;
      case ")":
          punctuationToken.setType(TokenType.R_PAREN);
          break;
      case ";":
          punctuationToken.setType(TokenType.SEMICOLON);
          break;
      case ",":
          punctuationToken.setType(TokenType.COMMA);
          break;
    }

    return punctuationToken;
  }
}