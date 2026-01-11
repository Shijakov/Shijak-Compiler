package com.company;

import com.company.old.code_generator.Generator;
import com.company.old.exceptions.dev_exceptions.ErrorInGenerationException;
import com.company.old.exceptions.symbol_table.ScopeNotFoundException;
import com.company.old.lexer.Lexer;
import com.company.old.lexer.Token;
import com.company.old.parser.abstract_syntax_tree.AbstractSyntaxTree;
import com.company.old.parser.parse_tree.ParseTree;
import com.company.old.parser.parse_tree.SyntaxAnalyzer;
import com.company.old.semantic_analyzer.SemanticAnalyzer;
import com.company.old.symbol_table.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ShijakCompiler {

    public static void main(String[] args) throws ErrorInGenerationException, ScopeNotFoundException {
        if (args.length == 0) {
            System.err.println("Input file path not specified");
            return;
        }
        String codePath = args[0];
        String inputPath = null;
        if (args.length > 1) {
            inputPath = args[1];
        }
        try {
            String inputCode = new String(Files.readAllBytes(Paths.get(codePath)));
            Scanner scanner = null;
            if (inputPath != null) {
                File file = new File(inputPath);
                scanner = new Scanner(file);
            }
            String compiledMIPS = compile(inputCode, scanner); // Assuming `compile` is your method
            Files.write(Paths.get("output.mips"), compiledMIPS.getBytes());
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String compile(String code, Scanner inputScanner) throws Exception {
        String result;
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.getTokens(code);

        ParseTree parseTree = SyntaxAnalyzer.analyze(tokens);

        AbstractSyntaxTree abs = AbstractSyntaxTree.from(parseTree);

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        SymbolTable symbolTable = semanticAnalyzer.analyze(abs);

        var generator = new Generator(symbolTable);
        result = generator.generate(abs);

        return result;
    }
}
