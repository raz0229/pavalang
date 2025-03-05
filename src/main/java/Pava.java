import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;

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
        // If more than one argument is passed, show usage.
        if (args.length > 1) {
            System.err.println("Usage: ./pava <filename> or ./pava -v|--version");
            System.exit(64);
        }

        // Check if the user requested version information.
        if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
            System.out.println("PavaLang 1.0.0");
            System.exit(0);
        }

        if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String filename) {
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

    private static void runPrompt() {
        // Build a LineReader with JLine 3 to support history and arrow keys.
        LineReader reader = LineReaderBuilder.builder().build();
        Interpreter interpreter = new Interpreter();
        System.out.println("PavaLang 1.0.0 Interactive Shell.");
        System.out.println("Type \"help()\" for more information or  \"quit()\" to exit.");
        while (true) {
            String line = null;
            try {
                line = reader.readLine("💪 >> ");
            } catch (UserInterruptException e) {
                // Handle Ctrl-C by continuing to next loop iteration.
                continue;
            } catch (EndOfFileException e) {
                // Ctrl-D (EOF) ends the REPL.
                break;
            }

            if (line == null || line.trim().isEmpty())
                continue; // Skip empty lines.
            else if (line.trim().equals("help()")) {
                System.out.println("-------------------------------------");
                System.out.println("PavaLang 1.0.0");
                System.out.println("Author: @raz0229");
                System.out.println();
                System.out.println("For help on syntax or licese:");
                System.out.println("https://github.com/raz0229/pavalang");
                System.out.println("-------------------------------------");
                continue;
            }
            else if (line.trim().equals("quit()")) {
                System.exit(0);
            }
            else if (line.trim().equals("quit")) {
                System.out.println("Use quit() or Ctrl-D (i.e. EOF) to exit");
            }
            else if (line.trim().equals("help")) {
                System.out.println("Use help() or Ctrl-D (i.e. EOF) to exit");
            }
            try {
                Lexer lexer = new Lexer(line);
                List<Token> tokens = lexer.scanTokens();
                Parser parser = new Parser(tokens);
                List<Stmt> statements = parser.parse();
                interpreter.interpret(statements);

                System.out.print("\n");
            } catch (SyntaxError err) {
                System.err.println("[Syntax Error] " + err.getMessage());
            } catch (RuntimeException re) {
                System.err.println("[ERROR] " + re.getMessage());
            }
        }
    }
}
