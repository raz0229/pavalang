import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


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
    SEMICOLON("SEMICOLON", ';');


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

  static String tokenScanner(Character ch, int lineNumber) throws Exception {
    for (Tokens tk : Tokens.values()) {
      if (tk.getValue() == ch) {
        return tk.getToken() + " " + tk.getValue() + " null";
      }
    }

    throw new Exception("[line " + lineNumber +  "] Error: Unexpected character: " + ch);
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

      while ((line = reader.readLine()) != null) {
        // System.out.println(lineNumber + ": " + line);
        for (Character ch : line.toCharArray()) {
          try {
            String scanned = tokenScanner(ch, lineNumber);
            System.out.println(scanned);
          } catch (Exception str) {
            System.err.println(str);
            errorCode = 65;
          }
        }
  
        lineNumber++;
      }
      
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
