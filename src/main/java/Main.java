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
    MINUS("MINUS", '-');


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

  static String tokenScanner(Character ch) {
    for (Tokens tk : Tokens.values()) {
      if (tk.getValue() == ch) {
        return tk.getToken() + " " + tk.getValue() + " null";
      }
    }

    return "EOF null";
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

    if (!command.equals("tokenize")) {
      System.err.println("Unknown command: " + command);
      System.exit(1);
    }

    String fileContents = "";
    try {
      fileContents = Files.readString(Path.of(filename));
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      System.exit(1);
    }

    // Uncomment this block to pass the first stage
    
    if (fileContents.length() > 0) {
      // throw new RuntimeException("Scanner not implemented");
      for (Character ch : fileContents.toCharArray()) {
        System.out.println(tokenScanner(ch));
      }

      System.out.println("EOF  null"); // Placeholder, remove this line when implementing the scanner
    } else {
      System.out.println("EOF  null");
    }
  }
}
