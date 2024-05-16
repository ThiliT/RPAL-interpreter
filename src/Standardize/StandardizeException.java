package Standardize;

// Custom exception class for standardization errors
public class StandardizeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public StandardizeException(String message) {
    super(message);
  }

}
