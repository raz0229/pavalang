import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

import evaluator.Evaluator;
import lexer.Lexer;
import lexer.Token;

import parser.Parser;
import parser.SyntaxError;
import parser.AstPrinter;
import parser.Stmt;

import interpreter.Interpreter;

class Pava {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./pava <filename> or ./pava -v|--version");
            System.exit(1);
        }
        
        // Check if the user requested version information.
        if (args[0].equals("-v") || args[0].equals("--version")) {
            System.out.println("PavaLang 1.0.0");
            System.exit(0);
        }
        
        String filename = args[0];

        int errorCode = 0; // Track lexical and runtime errors

        try {
            String source = Files.readString(Path.of(filename));
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            errorCode = lexer.errorCode;
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();

            Interpreter interpreter = new Interpreter();
            interpreter.interpret(statements);

        } catch (SyntaxError err) {
            errorCode = 65;
            System.err.println("[Syntax Error] " + err.getMessage());
        } catch (RuntimeException re) {
            errorCode = 70;
            System.err.println("[ERROR] " + re.getMessage());
        } catch (IOException err) {
            System.err.println("Error reading file: " + err.getMessage());
            System.exit(1); // File-related error
        }

        // Ensure the program exits with the correct error code
        if (errorCode != 0) {
            System.exit(errorCode);
        }
    }
}
