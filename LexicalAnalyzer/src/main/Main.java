package main;

import java.io.*;

import Implement.Lexer;

/*
 * Ö÷³ÌÐò
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        lexer.printToken();
        lexer.printSymbolsTable();
    }
}
