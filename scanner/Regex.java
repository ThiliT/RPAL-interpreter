package scanner;
import java.util.regex.Pattern;

// Used by the scanner for tokenizing
public class Regex {

  // Regex strings
  private static final String letterRegex = "a-zA-Z";

  private static final String digitRegex = "\\d";

  private static final String opSymbolRegexString = "+-/~:=|!#%_{}\"*<>.&$^\\[\\]?@";

  private static final String opSymbolToEscapeString = "([*<>.&$^?])";

  private static final String spaceRegex = "[\\s\\t\\n]";

  private static final String puncRegex = "();,";

  public static final String opSymbolRegex = "[" + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]";

  // patterns
  public static final Pattern LetterPattern = Pattern.compile("[" + letterRegex + "]");

  public static final Pattern DigitPattern = Pattern.compile(digitRegex);

  public static final Pattern OpSymbolPattern = Pattern.compile(opSymbolRegex);

  public static final Pattern IdentifierPattern = Pattern.compile("[" + letterRegex + digitRegex + "_]");

  public static final Pattern PunctuationPattern = Pattern.compile("[" + puncRegex + "]");

  public static final Pattern SpacePattern = Pattern.compile(spaceRegex);

  public static final Pattern CommentPattern = Pattern.compile("[ \\t\\'\\\\ \\r" + puncRegex
      + letterRegex + digitRegex + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");

  public static final Pattern StringPattern = Pattern.compile("[ \\t\\n\\\\" + puncRegex
      + letterRegex + digitRegex + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");



  private static String escapeMetaChars(String inputString, String charsToEscape) {
    return inputString.replaceAll(charsToEscape, "\\\\\\\\$1");
  }

}