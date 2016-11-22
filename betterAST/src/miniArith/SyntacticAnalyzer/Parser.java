package miniArith.SyntacticAnalyzer;

/**  Simple precedence parser for arithmetic expressions with +, *, and
 *   parenthesization.
 *   
 *   The stratified grammar is
 *   
 *       S ::= E eof          augmented grammar
 *       E ::= E + T | T      left associative addition, lowest precedence
 *       T ::= F * T | F      left associative multiplication, higher precedence
 *       F ::= - F | N        right associative unary negation, even higher precedence
 *       N ::= num | lparen E rparen   numbers or  parenthesized expressions, 
 *                                     highest precedence
 *   
 *   After transforming the stratified grammar to LL(1) EBNF we have
 *
 *       S ::= E eof
 *       E ::= T (+ T)* 
 *       T ::= F (* F)*   
 *       F ::= - F | N
 *       N ::= num | lparen E rparen
 *
 */

import miniArith.SyntacticAnalyzer.Scanner;
import miniArith.SyntacticAnalyzer.Token;
import miniArith.ErrorReporter;
import miniArith.AbstractSyntaxTrees.*;

public class Parser {

	private Scanner scanner;
	private ErrorReporter reporter;
	private Token currentToken;

	public Parser(Scanner scanner, ErrorReporter reporter) {
		this.scanner = scanner;
		this.reporter = reporter;
	}


	/**
	 * used to unwind parse stack when parse fails
	 *
	 */
	class SyntaxError extends Error {
		private static final long serialVersionUID = 1L;	
	}

	/**
	 * start parse
	 */
	public Expr parse() {
		currentToken = scanner.scan();
		try {
			return parseS();
		}
		catch (SyntaxError e) { 
			return null;
		}
	}

	//    S ::= E$
	private Expr parseS() throws SyntaxError {
		Expr e = parseE();
		accept(TokenKind.EOF);
		return e;
	}

	//    E ::= T (+ T)* 
	private Expr parseE() throws SyntaxError {
		Expr expr = parseT();
		while (currentToken.kind == TokenKind.PLUS || currentToken.kind == TokenKind.MINUS) {
			Token oper = currentToken;
			acceptIt();
			Expr expr2 = parseT();
			expr = new BinExpr(expr,oper,expr2);
		} 
		return expr;
	}

	//    T ::= F (* F)*   
	private Expr parseT() throws SyntaxError {
		Expr expr = parseF();
		while (currentToken.kind == TokenKind.TIMES) {
			Token oper = currentToken;
			acceptIt();
			Expr expr2 = parseF();
			expr = new BinExpr(expr,oper,expr2);
		} 
		return expr;
	}
	
	//  F ::= - F | N
	private Expr parseF() throws SyntaxError {
		if (currentToken.kind == TokenKind.MINUS) {
			Token oper = currentToken;
			acceptIt();
			Expr right = parseF();
			return new UnaryExpr(oper, right);
		}
		else
			return parseN();
	}

	//  F ::= num | ( E  )
	private Expr parseN() throws SyntaxError {
		Expr expr;
		switch (currentToken.kind) {
		case NUM:
			expr = new NumExpr(currentToken);
			acceptIt();
			return expr;

		case LPAREN:
			acceptIt();
			expr = parseE();
			accept(TokenKind.RPAREN);
			return expr;

		default:
			parseError("Expecting number or left parenthesis but found " + currentToken); 
			// never reached
			return null;
		}
	}

	/**
	 * accept current token and advance to next token
	 */
	private void acceptIt() throws SyntaxError {
		accept(currentToken.kind);
	}

	/**
	 * verify that current token in input matches expected token and advance to next token
	 * @param expectedToken
	 * @throws SyntaxError  if match fails
	 */
	private void accept(TokenKind expectedTokenKind) throws SyntaxError {
		if (currentToken.kind == expectedTokenKind) {
			currentToken = scanner.scan();
		}
		else
			parseError("expecting '" + expectedTokenKind +
					"' but found '" + currentToken.kind + "'");
	}

	/**
	 * report parse error and unwind call stack to start of parse
	 * @param e  string with error detail
	 * @throws SyntaxError
	 */
	private void parseError(String e) throws SyntaxError {
		reporter.reportError("Parse error: " + e);
		throw new SyntaxError();
	}
}
