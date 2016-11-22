/**
 *   COMP 520 
 *   Simple recursive descent parser for the grammar
 *   
 *   S ::= E$
 *   E ::= T (("+" | "*") T)*     
 *   T ::= num | "(" E ")"
 *
 *   num ::= '0' | ... | '9'
 *
 *   Terminals of this grammar are characters '0' ..'9', '+', '*', '(', and ')'
 *
 */
import java.io.*;
public class Parser {

	private InputStream inputStream;
	private char currentTerminal;
	private boolean trace = false;

	public Parser(InputStream inputStream) {
		this.inputStream = inputStream;
		initStream();
	}

	public static void main(String[] args) {
		System.out.println("Enter Expression");
		Parser parser = new Parser(System.in);

		// parse input, exits on failure
		parser.parseS();

		// indicate successful parse
		System.out.println("Successful parse!");
		System.exit(0);
	}


	/*
	 * A parse procedure for each nonterminal in the grammar
	 */

	//    S ::= E$
	private void parseS() {
		parseE();
		accept('$');
	}

	//    E ::= T (("+" | "*") T)*     
	private void parseE() {
		parseT();
		while (currentTerminal == '+' || currentTerminal == '*') {
			acceptIt();
			parseT();
		} 
	}

	//   T ::= num | "(" E ")"
	private void parseT() {
		if (isDigit(currentTerminal)) {
			acceptIt();
		}
		else if (currentTerminal == '(') {
			acceptIt();
			parseE();
			accept( ')' );
		}
		else 
			parseError("ParseT expecting '(' or digit but found '"
					+ currentTerminal +"'");
	}


	/*
	 * Parser support
	 */
	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	private void acceptIt() {
		accept(currentTerminal);
	}

	private void accept(char expectedTerminal) {
		if (currentTerminal == expectedTerminal) {
			pTrace();
			nextTerminal();
		}
		else
			parseError("expecting '" + expectedTerminal +
					"' but found '" + currentTerminal + "'");
	}

	// display parse error and terminate
	private void parseError(String e) {
		System.out.println("Parse error: " + e);
		System.exit(4);
	}
	
	// show parse stack whenever terminal is  accepted
	private void pTrace() {
		if (trace) {
			StackTraceElement [] stl = Thread.currentThread().getStackTrace();
			for (int i = stl.length - 1; i > 0 ; i--) {
				if(stl[i].toString().contains("parse"))
					System.out.println(stl[i]);
			}
			System.out.println("accepting: " + currentTerminal);
			System.out.println();
		}
	}

	
	

	/*
	 * extract terminals from input and maintain in currentTerminal
	 */
	

	final String terminals = "+*()0123456789";
	
	private void initStream() {
		currentTerminal = readChar();
	}

	// extract next terminal from inputstream if not at end of input
	private void nextTerminal() {
		if (currentTerminal != '$') {
			currentTerminal = readChar();
		}
	}

	// read a character from inputstream
	// detect end of input and substitute '$' as distinguished eot terminal
	// check for legitimate terminals and I/O errors
	private char readChar() { 
		char cc = '?';
		try {
			int c = inputStream.read();
			cc = (char) c;
			if (c == -1  || cc == '\n' || cc == '\r')
				return '$';
			if (! terminals.contains(Character.toString(cc)))
				parseError("Invalid character " + cc + " encountered in input");
		}
		catch (IOException e) {
			parseError("I/O Exception!");
		}
		return cc;
	}


}
