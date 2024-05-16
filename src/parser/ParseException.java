package parser;
//Custom exception class for parsing errors
public class ParseException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ParseException(String message) {
    super(message);
  }

}