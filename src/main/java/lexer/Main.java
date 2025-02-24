package lexer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("tokenize")) {
            System.err.println("Usage: java Main tokenize <filename>");
            System.exit(1);
        }

        String filename = args[1];
        int errorCode = 0;  // Track lexical errors

        try {
            // Read file content
            String source = Files.readString(Path.of(filename));
            
            // Initialize lexer and scan tokens
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();

            // Print tokens
            tokens.forEach(System.out::println);
            
        } catch (LexicalError e) {
            System.err.println(e.getMessage());
            errorCode = 65;  // Lexical error
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1); // File-related error
        }

        // Exit with error code if a lexical error occurred
        if (errorCode == 65) {
            System.exit(65);
        }
    }
}