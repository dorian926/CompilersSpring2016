package miniJava.SyntacticAnalyzer;

import java.io.*;
import miniJava.ErrorReporter;

public class Scanner {
	private InputStream inputStream;
	private SourceFile sourceFile;
	private ErrorReporter reporter;
	
	private char currentChar;
	private StringBuilder currentSpelling;
	private boolean currentlyScanningToken;
	
//	private boolean eot = false;
	
	public Scanner(SourceFile source, ErrorReporter reporter) {
		sourceFile = source;
		currentChar = sourceFile.getSource();
		this.reporter = reporter;
//		
//		//go to first char
//		readChar();
	}
	public Token scan() {
		Token tok;
		SourcePosition pos;
		TokenKind kind;
		// skip whitespace
//		while (!eot && currentChar == ' ')
//			takeIt();
//		
		currentlyScanningToken = false;
		while (currentChar == ' '
		           || currentChar == '\n'
		           || currentChar == '\r'
		           || currentChar == '\t')
		      scanSeparator();
		
		currentlyScanningToken = true;
		// collect spelling and identify token kind
		currentSpelling = new StringBuilder();
		pos = new SourcePosition();
		pos.start = sourceFile.getCurrentLine();
		
		kind = scanToken();
		
		pos.finish = sourceFile.getCurrentLine();
		tok = new Token(kind, currentSpelling.toString(), pos);
		// return new token
		return tok;
	}

	private void scanSeparator() {
		switch (currentChar) {
	    case '/':
	    	while (currentChar != '\n') {
	    		if(currentChar == SourceFile.eot) {
	    			break;
	    		}
	    		takeIt();
	    	}
	    	takeIt();
	        break;
	    case '*':
	    	takeIt();
	    	while(true){
	    		if(currentChar != SourceFile.eot) {
		    		if(currentChar == '*'){
		       			takeIt();
		       			if(currentChar == '/'){
		       				takeIt();
		       				break;
		       			}
		       		} else {
		       			takeIt();
		       		}
	    		} else {
	    			scanError("Unterminated comment");
	    			break;
	    		}
	       	}
	    	break;

	    case ' ': case '\n': case '\r': case '\t':
	      takeIt();
	      break;
	    }
	}
	public TokenKind scanToken() {
//		if (eot)
//			return(TokenKind.EOT); 

		// scan Token
		switch (currentChar) {
		case 'a': case 'b': case 'c': case 'd': case 'e':
		case 'f': case 'g': case 'h': case 'i': case 'j':
		case 'k': case 'l': case 'm': case 'n': case 'o':
		case 'p': case 'r': case 's': case 't': case 'u':
		case 'v': case 'w': case 'x': case 'y': case 'z':
		case 'A': case 'B': case 'C': case 'D': case 'E':
		case 'F': case 'G': case 'H': case 'I': case 'J':
		case 'K': case 'L': case 'M': case 'N': case 'O':
		case 'P': case 'R': case 'S': case 'T': case 'U':
		case 'V': case 'W': case 'X': case 'Y': case 'Z':
			while (isAlphaNumer(currentChar))
				takeIt();
			if(currentSpelling.toString().equals("class"))
				return(TokenKind.CLASS);
			else
			if(currentSpelling.toString().equals("int"))
				return(TokenKind.INT);
			else
			if(currentSpelling.toString().equals("boolean"))
				return(TokenKind.BOOLEAN);
			else
			if(currentSpelling.toString().equals("private"))
				return(TokenKind.PRIVATE);
			else
			if(currentSpelling.toString().equals("public"))
				return(TokenKind.PUBLIC);
			else
			if(currentSpelling.toString().equals("static"))
				return(TokenKind.STATIC);
			else
			if(currentSpelling.toString().equals("void"))
				return(TokenKind.VOID);
			else
			if(currentSpelling.toString().equals("true"))
				return(TokenKind.TRUE);
			else
			if(currentSpelling.toString().equals("false"))
				return(TokenKind.FALSE);
			else
			if(currentSpelling.toString().equals("null"))
				return(TokenKind.NULL);
			else
			if(currentSpelling.toString().equals("while"))
				return(TokenKind.WHILE);
			else
			if(currentSpelling.toString().equals("if"))
				return(TokenKind.IF);
			else
			if(currentSpelling.toString().equals("else"))
				return(TokenKind.ELSE);
			else
			if(currentSpelling.toString().equals("new"))
				return(TokenKind.NEW);
			else
			if(currentSpelling.toString().equals("this"))
				return(TokenKind.THIS);
			else
			if(currentSpelling.toString().equals("return"))
				return(TokenKind.RETURN);
			else 
				return(TokenKind.ID);
		
		case '.': 
			takeIt();
			return(TokenKind.PERIOD);
		case '+':
			takeIt();
			return(TokenKind.PLUS);

		case '*':
			takeIt();
			return(TokenKind.TIMES);

		case '(': 
			takeIt();
			return(TokenKind.LPAREN);

		case ')':
			takeIt();
			return(TokenKind.RPAREN);
			
		case '-':
			takeIt();
			if(currentChar == '-') {
				takeIt();
				scanError("Invalid  operator");
				return(TokenKind.ERROR);
			} else {
				return(TokenKind.MINUS);	
			}

		case '/':
			takeIt();
			if(currentChar == '/' || currentChar == '*'){
				currentSpelling = new StringBuilder();
				currentlyScanningToken = false;
				
				scanSeparator();
				while (currentChar == ' '
				           || currentChar == '\n'
				           || currentChar == '\r'
				           || currentChar == '\t')
				      scanSeparator();
				
				currentlyScanningToken = true;
				return scanToken();
			}
			return(TokenKind.DIVIDE);

		case '[': 
			takeIt();
			return(TokenKind.LBRACK);

		case ']':
			takeIt();
			return(TokenKind.RBRACK);
			
		case '{':
			takeIt();
			return(TokenKind.LCBRACK);
			
		case '}':
			takeIt();
			return(TokenKind.RCBRACK);
			
		case '>':
			takeIt();
			if(currentChar == '='){
				takeIt();
				return(TokenKind.GEQUALS);
			}
			else
				return(TokenKind.GREATER);

		case '<':
			takeIt();
			if(currentChar == '=') {
				takeIt();
				return(TokenKind.LEQUALS);
			}
			else
				return(TokenKind.LESS);

		case '!': 
			takeIt();
			if(currentChar == '='){
				takeIt();
				return(TokenKind.NEQUALS);
			}
			else
				return(TokenKind.NOT);
		
		case '=':
			takeIt();
			if(currentChar == '='){
				takeIt();
				return(TokenKind.EQUALS);
			}
			else
				return(TokenKind.ASSIGN);
			
		case '&':
			takeIt();
			if(currentChar == '&'){
				takeIt();
				return(TokenKind.AND);
			}
			else
				scanError("Unrecognized input expected two '" + currentChar + "' only read one");

		case '|':
			takeIt();
			if(currentChar == '|'){
				takeIt();
				return(TokenKind.OR);
			}
			else
				scanError("Unrecognized input expected two '" + currentChar + "' only read one");
		case ',':
			takeIt();
			return(TokenKind.COMMA);
		
		case ';':
			takeIt();
			return(TokenKind.SEMIC);

		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			while (isDigit(currentChar))
				takeIt();
			return(TokenKind.NUM);
			
		case SourceFile.eot:
			return(TokenKind.EOT);

		default:
			scanError("Unrecognized character '" + currentChar + "' in input");
			takeIt();
			return(TokenKind.ERROR);
		}
	}
	
	private void takeIt() {
		if (currentlyScanningToken) {
			currentSpelling.append(currentChar);
		}
		currentChar = sourceFile.getSource();
	}

//	private void skipIt() {
//		nextChar();
//	}
	
	private boolean isAlphaNumer(char c) {
		return (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || ((c >= '0') && (c <= '9')) || (c == '_') );
	}
	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}
	
	private void scanError(String m) {
		reporter.reportError("Scan Error:  " + m);
	}
	
	
	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';
//	
//	private void nextChar() {
//		if (!eot)
//			readChar();
//	}
//	
//	private void readChar() {
//		try {
//			int c = inputStream.read();
//			currentChar = (char) c;
//			if (currentChar == eolUnix ||currentChar == eolWindows)
//				c = inputStream.read();
//			currentChar = (char) c;
//			if (c == -1 ) 
//				eot = true;
//		} catch (IOException e) {
//			scanError("I/O Exception!");
//			eot = true;
//		}
//	}
}
