# RPAL-interpreter

This project is an interpreter for the RPAL (Right-associative, Pattern-matching, Applicative Language) programming language. The interpreter parses RPAL programs, builds an abstract syntax tree (AST), standardizes the AST, and then evaluates it.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Make (optional, for using the Makefile to build and run the project)

## Project Structure
src

    CSEmachine
        Beta.java
        Copier.java
        JCSEM.java
        Delta.java
        Environment.java
        Eta.java
        Tuple.java
    parser
        ASTNode.java
        ASTNodeType.java
        ParseException.java
        Parser.java
    scanner
        Regex.java
        Scanner.java
        Token.java
        TokenType.java

    Standardize
        Standardize.java
        StandardizeException.java

    Test_Programs

    myrpal.java

Makefile


## Building the Project

To compile the project, you can use the provided Makefile. Ensure you are in the project's root directory and run:
        make

## Running the Project

After compiling, you can run the interpreter with the following command:
        java myrpal file_name
Ensure you are in the project's src directory

## Command Line Options

-ast: This switch prints the abstract syntax tree (AST) of the given RPAL program.
    command: java myrpal file_name

Ensure you are in the project's src directory

## Cleaning Up

To remove all compiled .class files, run: make clean

## Notes

The main class is myrpal.
Ensure that the input RPAL program file exists and is readable.

## Acknowledgments

This project was developed as part of an educational assignment.
