package lexer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 ) {
            System.err.println("Usage: ./your_program.sh tokenize <filename>");
            System.exit(1);
        }

        String command = args[0];
        String filename = args[1];
        int errorCode = 0;  // Track lexical errors

        if (!command.equals("tokenize")) {
          System.err.println("Unknown command: " + command);
          //System.exit(1);
        }    

        try {
          // Read file content
          String source = Files.readString(Path.of(filename));

          // Initialize lexer
          Lexer lexer = new Lexer(source);

          // Scan tokens while handling multiple errors
          List<Token> tokens = null;
          
          tokens = lexer.scanTokens();
          errorCode = lexer.errorCode;
          

          // Print valid tokens (even if an error occurred)
          if (tokens != null) {
              tokens.forEach(System.out::println);
          }

      } catch (IOException e) {
          System.err.println("Error reading file: " + e.getMessage());
          System.exit(1); // File-related error
      }

      // Ensure the program exits with the correct error code
      if (errorCode == 65) {
          System.exit(65);
      }
    }
}