package scanner;

//Represents a token produced by the scanner
public class Token {

  private TokenType type; //Type of the token
  private String value; //Value of the token

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public void setValue(String value) {
    this.value = value;
  }

}


