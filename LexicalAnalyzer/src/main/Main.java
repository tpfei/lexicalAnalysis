package main;

import java.io.*;

import Implement.Lexer;

/*
 * ������
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        lexer.printToken();
        lexer.printSymbolsTable();
    }
}
