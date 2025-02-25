

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

import interpreter.Interpreter;
import lexer.Lexer;
import lexer.Token;

import parser.Parser;
import parser.SyntaxError;
import parser.AstPrinter;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 ) {
            System.err.println("Usage: ./your_program.sh tokenize <filename>");
            System.exit(1);
        }

        String command = args[0];
        String filename = args[1];
        int errorCode = 0;  // Track lexical errors

        switch (command) {
            case "tokenize":
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
      
                break;

            case "parse":
            try {
                String source = Files.readString(Path.of(filename));
                Lexer lexer = new Lexer(source);
                List<Token> tokens = null;
                
                tokens = lexer.scanTokens();
                errorCode = lexer.errorCode;
                
                Parser parser = new Parser(tokens);
                List<String> expressions = parser.parse();

                // Step 3: Print each parsed expression
                for (String expr : expressions) {
                    System.out.println(expr);
                }


            } catch(SyntaxError err) {
                errorCode = 65;
                System.err.println(err.getMessage());
            } 
            
            catch (IOException err) {
                System.err.println("Error reading file: " + err.getMessage());
                System.exit(1); // File-related error
            }
                break;
            

            case "evaluate":
            try {
                String source = Files.readString(Path.of(filename));
                Lexer lexer = new Lexer(source);
                List<Token> tokens = null;
                tokens = lexer.scanTokens();
                errorCode = lexer.errorCode;
                Parser parser = new Parser(tokens);
                List<String> expressions = parser.parse();

                // Step 4: Evaluate parsed expressions
                Interpreter interpreter = new Interpreter(expressions);
                List<String> results = interpreter.evaluate();

                // Step 5: Print evaluated results
                for (String result : results) {
                    System.out.println(result);
        }


            } catch(SyntaxError err) {
                errorCode = 65;
                System.err.println(err.getMessage());
            } 
            
            catch (IOException err) {
                System.err.println("Error reading file: " + err.getMessage());
                System.exit(1); // File-related error
            }
                break;
            default:
                System.err.println("Unknown command: " + command);
                //System.exit(1);
                break;
        }

        
      // Ensure the program exits with the correct error code
      if (errorCode == 65) {
          System.exit(65);
      }
    }
}