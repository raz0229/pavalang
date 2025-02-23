import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


// Custom Unchecked Exception
class LexicalError extends RuntimeException {
  public LexicalError(String message) {
      super(message);
  }
}

public class Main {

  public enum Tokens {
    
    LEFT_PAREN("LEFT_PAREN", '('),
    RIGHT_PAREN("RIGHT_PAREN", ')'),
    LEFT_BRACE("LEFT_BRACE", '{'),
    RIGHT_BRACE("RIGHT_BRACE", '}'),
    STAR("STAR", '*'),
    DOT("DOT", '.'),
    COMMA("COMMA", ','),
    PLUS("PLUS", '+'),
    MINUS("MINUS", '-'),
    SEMICOLON("SEMICOLON", ';'),
    EQUAL("EQUAL", '='),
    BANG("BANG", '!'),
    LESS("LESS", '<'),
    GREATER("GREATER", '>'),
    SLASH("SLASH", '/');


    private final String token;
    private final Character value;
  
    Tokens(String token, Character value) {
      this.token = token;
      this.value = value;
    }

    public String getToken() {
      return token;
    }
    public Character getValue() {
      return value;
    }
  }

  public enum DualCharTokens {

    EQUAL_EQUAL("EQUAL_EQUAL", "=="),
    BANG_EQUAL("BANG_EQUAL", "!="),
    LESS_EQUAL("LESS_EQUAL", "<="),
    GREATER_EQUAL("GREATER_EQUAL", ">="),
    COMMENT("COMMENT", "//");

    private final String token;
    private final String value;
  
    DualCharTokens(String token, String value) {
      this.token = token;
      this.value = value;
    }

    public String getToken() {
      return token;
    }
    public String getValue() {
      return value;
    }
  }

  static String tokenScanner(Character ch, int lineNumber) throws LexicalError {
    for (Tokens tk : Tokens.values()) {
      if (tk.getValue() == ch) {
        return tk.getToken() + " " + tk.getValue() + " null";
      }
    }

    throw new LexicalError("[line " + lineNumber +  "] Error: Unexpected character: " + ch);
  }

  static String dualTokenScanner(String str, int lineNumber) throws LexicalError {
    for (DualCharTokens tk : DualCharTokens.values()) {
      if (tk.getValue().equals(str)) {
        return tk.getToken() + " " + tk.getValue() + " null";
      }
    }

    throw new LexicalError("[line " + lineNumber +  "] Error: Unexpected character: " + str);
  }

  static Boolean checkForDualCharacterOperator(String line, int index) {
    String tempToken = String.format("%s%s", line.charAt(index), line.charAt(index+1));
    
    for (DualCharTokens tk : DualCharTokens.values()) {
      if (tempToken.equals(tk.getValue())) {
        return true;
      }
    }
    return false;
  }

  static Boolean isCharacterANumber(char ch) {
    return ch >= '0' && ch <= '9';
  }

  static Boolean isCharacterAnAlphabet(char ch) {
    return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_');
  }

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.err.println("Logs from your program will appear here!");

    if (args.length < 2) {
      System.err.println("Usage: ./your_program.sh tokenize <filename>");
      System.exit(1);
    }

    String command = args[0];
    String filename = args[1];
    ArrayList<String> validTokens = new ArrayList<String>();
    int errorCode = 0;

    if (!command.equals("tokenize")) {
      System.err.println("Unknown command: " + command);
      System.exit(1);
    }

    try {
      //fileContents = Files.readString(Path.of(filename));
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      int lineNumber = 1;

      // check for lexical errors first
      while ((line = reader.readLine()) != null) {
        for (int i=0; i<line.length(); i++) {
          try {

            String scanned;

            // check for DUAL_CHARACTER_OPERATORS (==, !=, <=)
            if (i < line.length()-1 && checkForDualCharacterOperator(line, i)) {
              String str = String.format("%s%s", line.charAt(i), line.charAt(i+1));
              if (str.equals("//")) {
                i = line.length(); // skip for comment
              } else {
                scanned = dualTokenScanner(str, lineNumber);
                i++;  // skip another character
                validTokens.add(scanned);
              }
            } else {

              // skip whitespace
              if (line.charAt(i) != '\t' && line.charAt(i) != ' ') {

                // Special check if string literal
                if (line.charAt(i) == '"') {
                  // search for closing "
                  boolean closingStringFound = false;
                  for (int j=i+1; j<line.length(); j++) {
                    if (line.charAt(j) == '"') {
                      // closing " found
                      String lexeme = line.substring(i, j+1);
                      String literal = line.substring(i+1, j);

                      validTokens.add("STRING "+ lexeme + " " + literal);
                      closingStringFound = true;
                      i=j;
                      break;
                    }
                  }

                  // lexical error if closing " is not found
                  if (!closingStringFound) {
                    i=line.length();  // skip unclosed string characters on same line
                    throw new LexicalError("[line " + lineNumber +  "] Error: Unterminated string.");
                  }

                } 
                // Special check if number literal
                else if (isCharacterANumber(line.charAt(i))) {
                  int j=i;
                  while (j<line.length()) {

                    if (line.charAt(j) != '.' && !isCharacterANumber(line.charAt(j)))
                      break;
                    
                    // detect double dots in between numbers
                    // if (j+1<line.length() && (line.charAt(j) == '.') && (line.charAt(j+1) == '.')) {
                    //   i=line.length();
                    //   throw new LexicalError("[line " + lineNumber +  "] Error: Illegal \'.\'");
                    // }

                    j++;
                  }
                  String lexeme = line.substring(i,j);
                  String literal = String.valueOf(Double.parseDouble(line.substring(i,j)));
                  validTokens.add("NUMBER " + lexeme + " " + literal);
                  i=j-1;
                } 

                // Special check if identifier
                else if (isCharacterAnAlphabet(line.charAt(i))) {
                  int j=i;
                  while (j<line.length()) {

                    if (!isCharacterAnAlphabet(line.charAt(j)))
                      break;

                    j++;
                  }
                  String lexeme = line.substring(i,j);
                  String literal = "null";
                  validTokens.add("IDENTIFER " + lexeme + " " + literal);
                  i=j-1;
                }
                else {
                  scanned = tokenScanner(line.charAt(i), lineNumber);
                  validTokens.add(scanned);
                }

                
              }

            }

            
          } catch (LexicalError err) {
            System.err.println(err.getMessage());
            errorCode = 65;
          }
        }
  
        lineNumber++;
      }

      validTokens.forEach(System.out::println);
      
      if (line == null)
        System.out.println("EOF  null");

      if (errorCode == 65) {
        // Lexical error
        System.exit(65);
      }

      
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      System.exit(1);
    }
  }
}
