package scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

//Lexical Analyzer and Screener
public class Scanner {

  private BufferedReader bufferedReader;
  private String extraCharacterRead;
  private final List<String> reservedKeywords;

  //To initialize the Scanner with the input file
  public Scanner(String inputFile) throws IOException {
    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));

    //Keywords
    reservedKeywords = Arrays.asList("let", "in", "within", "fn", "where", "aug", "or",
                "not", "gr", "ge", "ls", "le", "eq", "ne", "true",
                "false", "nil", "dummy", "rec", "and");
  }

  //Read the next token
  public Token readNextToken() {

    Token nextToken = null;
    String nextCharacter;

    //Check if there is an extra character from the previous token
    if (extraCharacterRead != null) {
      nextCharacter = extraCharacterRead;
      extraCharacterRead = null;
    }

    else
      nextCharacter = readNextCharacter();

      //Build token from the next character

    if (nextCharacter != null)
      nextToken = buildToken(nextCharacter);
      return nextToken;
  }

  //Read the next character from the input stream
  private String readNextCharacter() {

    String nextCharacter = null;

    try {
      //Read the next character from the stream
      int c = bufferedReader.read();

      //Check if end of file is reached
      if (c != -1) {
        nextCharacter = Character.toString((char) c);
      }

      else  //Close the BufferedReader if end of file is reached
        bufferedReader.close();
    }

    catch (IOException e) {
      //IOException
    }
    return nextCharacter;

  }

  //Build a token based on the current character
  private Token buildToken(String currentCharacter) {
    Token nextToken = null;
    //Check if the character is a letter
    if (Regex.LetterPattern.matcher(currentCharacter).matches()) {
      nextToken = buildIdentifierToken(currentCharacter);
    }
    //Check if the character is a digit
    else if (Regex.DigitPattern.matcher(currentCharacter).matches()) {
      nextToken = buildIntegerToken(currentCharacter);
    }
    //Operating symbol
    else if (Regex.OpSymbolPattern.matcher(currentCharacter).matches()) {
      nextToken = buildOperatorToken(currentCharacter);
    }
    //String delimiter
    else if (currentCharacter.equals("\'")) {
      nextToken = buildStringToken(currentCharacter);
    }
    //Space
    else if (Regex.SpacePattern.matcher(currentCharacter).matches()) {
      nextToken = buildSpaceToken(currentCharacter);
    }
    //Punctuation
    else if (Regex.PunctuationPattern.matcher(currentCharacter).matches()) {
      nextToken = buildPunctuationPattern(currentCharacter);
    }
    return nextToken;

  }

  //Identifier token
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
      //Store the extra character for the next token
      extraCharacterRead = nextCharacter;
      break;
      }

    }

    String value = stringBuilder.toString();

    //Check if the identifier is a reserved keyword
    if (reservedKeywords.contains(value))
      identifierToken.setType(TokenType.RESERVED);

    identifierToken.setValue(value);
    return identifierToken;

  }

  //Integer token
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
        extraCharacterRead = nextCharacter;
        break;
      }
    }

    integerToken.setValue(stringBuilder.toString());
    return integerToken;
  }

  //Operator token
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
        extraCharacterRead = nextCharacter;
        break;
      }
    }

    operatorToken.setValue(stringBuilder.toString());
    return operatorToken;
  }

  //String token
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

      else if (Regex.StringPattern.matcher(nextCharacter).matches()) { //If the current character is a valid string character
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }
    }

    return null;
  }

  //Space token
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
        //If a non-space character is encountered, store it as the extra character for the next token
        extraCharacterRead = nextCharacter;
        break;
      }
    }

    // Set the value of the space token to the constructed space
    deleteToken.setValue(stringBuilder.toString());
    return deleteToken;
  }

  //Comment token
  private Token buildCommentToken(String currentCharacter) {
    Token commentToken = new Token();
    commentToken.setType(TokenType.DELETE);
    StringBuilder stringBuilder = new StringBuilder(currentCharacter);
    String nextCharacter = readNextCharacter();

    while (nextCharacter != null) {
      //If the current character is part of the comment
      if (Regex.CommentPattern.matcher(nextCharacter).matches()) {
        stringBuilder.append(nextCharacter);
        nextCharacter = readNextCharacter();
      }

      //If a newline character is encountered
      else if (nextCharacter.equals("\n"))
        break;
    }

    commentToken.setValue(stringBuilder.toString());
    return commentToken;
  }

  //Punctuation token
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