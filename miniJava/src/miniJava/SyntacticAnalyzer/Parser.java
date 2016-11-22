package miniJava.SyntacticAnalyzer;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.TokenKind;
import miniJava.SyntacticAnalyzer.SourcePosition;

import java.util.Stack;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;


public class Parser {
	private Scanner scanner;
	private ErrorReporter reporter;
	private Token currentToken;
	private boolean trace = false;
	private SourcePosition previousTokenPosition;
	
	public Parser(Scanner scanner, ErrorReporter reporter) {
		this.scanner = scanner;
		this.reporter = reporter;
		previousTokenPosition = new SourcePosition();
	}
	// start records the position of the start of a phrase.
	// This is defined to be the position of the first
	// character of the first token of the phrase.

	  void start(SourcePosition position) {
	    position.start = currentToken.posn.start;
	  }

	// finish records the position of the end of a phrase.
	// This is defined to be the position of the last
	// character of the last token of the phrase.

	  void finish(SourcePosition position) {
	    position.finish = previousTokenPosition.finish;
	  }
	
	/**
	 * SyntaxError is used to unwind parse stack when parse fails
	 *
	 */
	class SyntaxError extends Error {
		private static final long serialVersionUID = 1L;	
	}
	
	private void acceptIt() throws SyntaxError {
		accept(currentToken.kind);
//		previousTokenPosition = currentToken.posn;
//	    currentToken = scanner.scan();
	}

	/**
	 * verify that current token in input matches expected token and advance to next token
	 * @param expectedToken
	 * @throws SyntaxError  if match fails
	 */
	private void accept(TokenKind expectedTokenKind) throws SyntaxError {
		if (currentToken.kind == expectedTokenKind) {
			previousTokenPosition = currentToken.posn;
			if (trace)
				pTrace();
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

	// show parse stack whenever terminal is  accepted
	private void pTrace() {
		StackTraceElement [] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i > 0 ; i--) {
			if(stl[i].toString().contains("parse"))
				System.out.println(stl[i]);
		}
		System.out.println("accepting: " + currentToken.kind + " (\"" + currentToken.spelling + "\")");
		System.out.println();
	}

	/**
	 * start parse
	 * @return 
	 */
	public Package parse() {
		Package packageAST = null;
		
		previousTokenPosition.start = 0;
	    previousTokenPosition.finish = 0;
	    
	    currentToken = scanner.scan();
	    
		ClassDeclList clAST = new ClassDeclList();
		
		SourcePosition classListPos = new SourcePosition();
		start(classListPos);
		
		
		try {
			while(currentToken.kind == TokenKind.CLASS) {
				ClassDecl c = parseClass();
				clAST.add(c);
			}
			finish(classListPos);
			packageAST = new Package(clAST, previousTokenPosition);
			accept(TokenKind.EOT);
			
			
		} 
		catch (SyntaxError e) {  }
		return packageAST;
	}
	private ClassDecl parseClass() throws SyntaxError {
		ClassDecl c = null;
		SourcePosition cPos = currentToken.posn;
		accept(TokenKind.CLASS);
		String className = currentToken.spelling;
		Identifier classID = new Identifier(currentToken);
		accept(TokenKind.ID);
		FieldDeclList fieldList = new FieldDeclList();
		MethodDeclList methodList = new MethodDeclList();
		
		accept(TokenKind.LCBRACK);
		
		while(currentToken.kind != TokenKind.RCBRACK) {
			MemberDecl memDecl = parseDeclarators();
			Identifier memID = new Identifier(currentToken);
			boolean isVoid = false;
			Type type = null;
			SourcePosition typePos = currentToken.posn;
			memID = new Identifier(currentToken);
			switch(currentToken.kind) {
			
			case INT: case ID: 
				if(currentToken.kind == TokenKind.INT) {
					
				type = new BaseType(TypeKind.INT, typePos);
				} else {
					Identifier typeID = new Identifier(currentToken);
					type = new ClassType(typeID, typePos);
				}
				acceptIt();
				if(currentToken.kind == TokenKind.LBRACK) {
					acceptIt();
					accept(TokenKind.RBRACK);
					type = new ArrayType(type, typePos);
				}
				break;
			
			case BOOLEAN: case VOID:
				if(currentToken.kind == TokenKind.BOOLEAN) {
					type = new BaseType(TypeKind.BOOLEAN, typePos);
				} else {
					type = new BaseType(TypeKind.VOID, typePos);
					isVoid = true;
				}
				acceptIt();
				break;
			
			default:
				parseError("Invalid Type");
			}
			memDecl = new FieldDecl(memDecl.isPrivate,memDecl.isStatic, type, currentToken.spelling, memDecl.posn );
			memDecl.id = memID;
			accept(TokenKind.ID);
			switch(currentToken.kind) {
			
			case LPAREN: //method
				acceptIt();
				ParameterDeclList paramList = new ParameterDeclList();
				if(currentToken.kind != TokenKind.RPAREN) {
					paramList = parseParameter();
				}
				accept(TokenKind.RPAREN);
				
				accept(TokenKind.LCBRACK);
				StatementList stateList = new StatementList();
				while(currentToken.kind != TokenKind.RCBRACK) {

						Statement s = parseStatement();
						stateList.add(s);

				}
				accept(TokenKind.RCBRACK);
				MethodDecl methodDecl = new MethodDecl(memDecl, paramList, stateList, memDecl.posn);
				//methodDecl.type = memType;
				methodDecl.id = memDecl.id;
				methodList.add(methodDecl);
				break;
				
			default:
				if(isVoid) {
					parseError("not allowed");
					break;
				}
				FieldDecl fieldDecl = new FieldDecl(memDecl,memDecl.posn);
				fieldDecl.id = memDecl.id;
				fieldList.add(fieldDecl);
				accept(TokenKind.SEMIC);
			}
		}
		accept(TokenKind.RCBRACK);
		
		c = new ClassDecl(className, fieldList, methodList, cPos);
		c.type = new ClassType(classID,cPos);
		return c;
	}
//	private Identifier parseIdentifier() {
//		Identifier I = null;
//
//	    if (currentToken.kind == TokenKind.CLASS) {
//	      previousTokenPosition = currentToken.posn;
//	      String spelling = currentToken.spelling;
//	      I = new Identifier(currentToken);
//	    }
//	    return I;
//	}

	private FieldDecl parseDeclarators() throws SyntaxError {
		boolean isPrivate = false;
		boolean isStatic = false;
		SourcePosition currPos = currentToken.posn;
		start(currPos);
		if (currentToken.kind == TokenKind.PUBLIC || currentToken.kind == TokenKind.PRIVATE) {
			isPrivate = (currentToken.kind == TokenKind.PRIVATE);
			acceptIt();
		}
		if (currentToken.kind == TokenKind.STATIC) {
			isStatic = true;
			acceptIt();
		}
//		Type memberType = parseType();
		Identifier memberId = new Identifier(currentToken);
		finish(currPos);
		FieldDecl fd = new FieldDecl(isPrivate, isStatic, null, null, currPos);
		return new FieldDecl(isPrivate, isStatic, null, null, currPos);
	}
	
	public Statement parseStatement() {
		Statement s = null;
		SourcePosition sPos = currentToken.posn;
		start(sPos);
		switch(currentToken.kind) {
		
		case LCBRACK:
			acceptIt();
			StatementList sList = new StatementList();
			while(currentToken.kind != TokenKind.RCBRACK) {
				sList.add(parseStatement());
			}
			accept(TokenKind.RCBRACK);
			finish(sPos);
			s = new BlockStmt(sList, sPos);
			break;
			
		case BOOLEAN: case VOID:
			Type varDeclType = parseType();
			Identifier varDeclID = new Identifier(currentToken);
			accept(TokenKind.ID);
			accept(TokenKind.ASSIGN);
			Expression varDeclExpr = parseExpression();
			accept(TokenKind.SEMIC);
			VarDecl varDecl = new VarDecl(varDeclType,varDeclID.spelling, sPos);
			varDecl.id = varDeclID;
			s = new VarDeclStmt(varDecl, varDeclExpr, sPos);
			break;
			
		case INT:  
			Type intVarDecl = new BaseType(TypeKind.INT, currentToken.posn);
			acceptIt();
			if(currentToken.kind == TokenKind.LBRACK) {
				acceptIt();
				intVarDecl = new ArrayType(intVarDecl, intVarDecl.posn);
				accept(TokenKind.RBRACK);
			}
			Identifier intVarDeclID = new Identifier(currentToken);
			accept(TokenKind.ID);
			accept(TokenKind.ASSIGN);
			Expression intVarDeclExpr = parseExpression();
			accept(TokenKind.SEMIC);
			VarDecl varDecl2 = new VarDecl(intVarDecl,intVarDeclID.spelling, sPos);
			varDecl2.id = intVarDeclID;
			finish(sPos);
			s = new VarDeclStmt(varDecl2, intVarDeclExpr, sPos);
			break;
		
		case THIS:
			Reference ref = parseReference();
//			Reference r = new ThisRef(sPos);
//			QualifiedRef ref = (QualifiedRef) parseReference();
//			ref = new QualifiedRef(r, ref.id, sPos);
//			if(ref.getClass() != 5) {
//				ref = (QualifiedRef) ref;
//			}
//			QualifiedRef ref = (QualifiedRef) parseReference();
			
			switch(currentToken.kind) {
			
			case ASSIGN:
				acceptIt();
				Expression thisRefExpr = parseExpression();
				accept(TokenKind.SEMIC);
				finish(sPos);
				s = new AssignStmt(ref, thisRefExpr, sPos);
				break;
				
			case LBRACK:
				QualifiedRef qRef = (QualifiedRef) ref;
				Identifier i = qRef.id;
				IdRef iDRef = new IdRef(i, qRef.posn);
				
				acceptIt();
				Expression thisRefExpr2 = parseExpression();
				IndexedRef iRef = new IndexedRef(iDRef, thisRefExpr2, sPos);
				accept(TokenKind.RBRACK);
				accept(TokenKind.ASSIGN);
				Expression thisRefExpr3 = parseExpression();
				accept(TokenKind.SEMIC);
				finish(sPos);
				s = new AssignStmt(iRef, thisRefExpr3, sPos);
				break;
			
			case LPAREN:
				acceptIt();
				ExprList thisRefExprList = new ExprList();
				if(currentToken.kind != TokenKind.RPAREN) {
					thisRefExprList = parseArgumentList();
				}
				accept(TokenKind.RPAREN);
				accept(TokenKind.SEMIC);
				finish(sPos);
				s = new CallStmt(ref, thisRefExprList, sPos);
				break;
				
			default:
				parseError("Invalid statement, wrong use of reference");
			}
			
			break;
		case ID:
			Identifier id = new Identifier(currentToken);
			Reference idRef = new IdRef(id, id.posn);
			acceptIt();
			
			switch(currentToken.kind) {
			
			case ASSIGN:
				acceptIt();
				Expression idExpr = parseExpression();
				accept(TokenKind.SEMIC);
				finish(sPos);
				s = new AssignStmt(idRef, idExpr, sPos);
				break;
				
			case LBRACK:
				acceptIt();
				if(currentToken.kind == TokenKind.RBRACK) {
					Type idType = new ClassType(id,id.posn);
					Type idArrayType = new ArrayType(idType, idType.posn);
					acceptIt();
					Identifier id2 = new Identifier(currentToken);
					accept(TokenKind.ID);
					VarDecl idVarDecl = new VarDecl(idArrayType, id2.spelling, sPos);
					idVarDecl.id = id2;
					accept(TokenKind.ASSIGN);
					Expression idExpr2 = parseExpression();
					accept(TokenKind.SEMIC);
					finish(sPos);
					s = new VarDeclStmt(idVarDecl, idExpr2, sPos);
				} else {
					Expression idExpr3 = parseExpression();
					IdRef t = new IdRef(id, idRef.posn);
					IndexedRef iRef = new IndexedRef(t, idExpr3, sPos);
					accept(TokenKind.RBRACK);
					accept(TokenKind.ASSIGN);
					Expression idExpr4 = parseExpression();
					accept(TokenKind.SEMIC);
					finish(sPos);
					s = new IxAssignStmt(iRef, idExpr4, sPos);
					}
				break;
			
			case PERIOD://here does not pick up all of ref
				Expression idRefExpr;
//				QualifiedRef ref1 = parseReferenceMembers(idRef);
				ref = parseReferenceMembers(idRef);
				QualifiedRef ref1 = (QualifiedRef) ref;
				Identifier i1 = ref1.id;
				IdRef iDRef1 = new IdRef(i1, ref1.posn);
//				ref = new QualifiedRef(idRef, i1, sPos);
				switch(currentToken.kind) {
				
				case ASSIGN:
					acceptIt();
					idRefExpr = parseExpression();
					accept(TokenKind.SEMIC);
					finish(sPos);
					s = new AssignStmt(ref, idRefExpr, sPos);
					break;
					
				case LBRACK:
					acceptIt();
					idRefExpr = parseExpression();
					IndexedRef indiRef = new IndexedRef(iDRef1, idRefExpr, sPos);
					accept(TokenKind.RBRACK);
					accept(TokenKind.ASSIGN);
					Expression idRefExpr2 = parseExpression();
					accept(TokenKind.SEMIC);
					finish(sPos);
					s = new AssignStmt(indiRef, idRefExpr2, sPos);
					
				case LPAREN:
					acceptIt();
					ExprList idRefExprList = new ExprList();
					if(currentToken.kind != TokenKind.RPAREN) {
						idRefExprList = parseArgumentList();
					}
					accept(TokenKind.RPAREN);
					accept(TokenKind.SEMIC);
					finish(sPos);
					s = new CallStmt(ref, idRefExprList, sPos);
					break;
				
				default:
					parseError("Invalid statement, misuse of reference");
				}
				break;
			
			case ID:
				Identifier id2 = new Identifier(currentToken);
				Type idClass = new ClassType(id, id.posn);
				acceptIt();
				accept(TokenKind.ASSIGN);
				Expression idExpr1 = parseExpression();
				accept(TokenKind.SEMIC);
				VarDecl idVarDecl = new VarDecl(idClass, id2.spelling, sPos);
				idVarDecl.id = id2;
				finish(sPos);
				s = new VarDeclStmt(idVarDecl, idExpr1, sPos);
				break;
				
			case LPAREN:
				acceptIt();
				ExprList idExprList = new ExprList();
				if(currentToken.kind != TokenKind.RPAREN) {
					idExprList = parseArgumentList();
				}
				accept(TokenKind.RPAREN);
				accept(TokenKind.SEMIC);
				finish(sPos);
				s = new CallStmt(idRef, idExprList, sPos);
				break;
				
			default:
				parseError("Invalid Statement");
			}
			break;
		case RETURN:
			acceptIt();
			if(isBinOp() || currentToken.kind == TokenKind.THIS || currentToken.kind == TokenKind.ID ||
					currentToken.kind == TokenKind.NUM || currentToken.kind == TokenKind.TRUE ||
					currentToken.kind == TokenKind.FALSE || currentToken.kind == TokenKind.LPAREN ||
					currentToken.kind == TokenKind.NEW) {
				Expression returnExpr = parseExpression();
				finish(sPos);
				s = new ReturnStmt(returnExpr, sPos);
				
			} else {
				finish(sPos);
				s = new ReturnStmt(null, sPos);
				
			}
			accept(TokenKind.SEMIC);
			break;
		case IF:
			acceptIt();
			accept(TokenKind.LPAREN);
			Expression ifExpr = parseExpression();
			accept(TokenKind.RPAREN);
			Statement ifStatement = parseStatement();
			if(currentToken.kind == TokenKind.ELSE) {
				acceptIt();
				finish(sPos);
				s = new IfStmt(ifExpr, ifStatement, parseStatement(), sPos);
			} else {
				finish(sPos);
				s = new IfStmt(ifExpr, ifStatement, sPos);
			}
			break;
		case WHILE:
			acceptIt();
			accept(TokenKind.LPAREN);
			Expression whileExpr = parseExpression();
			accept(TokenKind.RPAREN);
			finish(sPos);
			s = new WhileStmt(whileExpr, parseStatement(), sPos);
			break;
		default:
			parseError("Invalid statement");
		}
		return s;
	}
	
	public Expression parseExpression() throws SyntaxError{
		Expression expr = parseConjunction();
		while(currentToken.kind == TokenKind.OR) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseConjunction(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseConjunction() throws SyntaxError {
		Expression expr = parseEquality();
		while(currentToken.kind == TokenKind.AND) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseEquality(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseEquality() throws SyntaxError {
		Expression expr = parseRelational();
		while(currentToken.kind == TokenKind.EQUALS || currentToken.kind == TokenKind.NEQUALS) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseRelational(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseRelational() throws SyntaxError {
		Expression expr = parseAdditive();
		while(currentToken.kind == TokenKind.LESS || currentToken.kind == TokenKind.GREATER ||
				currentToken.kind == TokenKind.LEQUALS || currentToken.kind == TokenKind.GEQUALS) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseAdditive(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseAdditive() throws SyntaxError {
		Expression expr = parseMultiplicative();
		while(currentToken.kind == TokenKind.PLUS || currentToken.kind == TokenKind.MINUS) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseMultiplicative(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseMultiplicative() throws SyntaxError {
		Expression expr = parseUnary();
		while(currentToken.kind == TokenKind.TIMES || currentToken.kind == TokenKind.DIVIDE) {
			Operator op = new Operator(currentToken);
			acceptIt();
			expr = new BinaryExpr(op, expr, parseUnary(), currentToken.posn);
		}
		return expr;
	}
	public Expression parseUnary() throws SyntaxError {
		Stack<Operator> operators = new Stack<Operator>();
		while (currentToken.kind == TokenKind.MINUS || currentToken.kind == TokenKind.NOT) {
			operators.push(new Operator(currentToken));
			acceptIt();
		}
		Expression expr = parseTerm();
		while (!operators.empty()) {
			Operator op = operators.pop();
			expr = new UnaryExpr(op, expr, op.posn);
		}
		return expr;
	}
	public Expression parseTerm() {
		Expression expr = null;
		SourcePosition ePos = currentToken.posn;
		start(ePos);
		switch(currentToken.kind) {
		
		case NEW:
			acceptIt();
			Type newType;
			Expression arrayExpr;
			
			switch(currentToken.kind) {
			
			case INT:
				newType = new BaseType(TypeKind.INT,currentToken.posn);
				acceptIt();
				accept(TokenKind.LBRACK);
				arrayExpr = parseExpression();
				accept(TokenKind.RBRACK);
				finish(ePos);
				expr = new NewArrayExpr(newType, arrayExpr, ePos);
				break;
				
			case ID:
				Identifier id = new Identifier(currentToken);
				newType = new ClassType(id, id.posn);
				acceptIt();
				switch(currentToken.kind) {
				
				case LPAREN:
					acceptIt();
					accept(TokenKind.RPAREN);
					finish(ePos);
					expr = new NewObjectExpr((ClassType) newType, ePos);
					break;
					
				case LBRACK:
					acceptIt();
					arrayExpr = parseExpression();
					accept(TokenKind.RBRACK);
					finish(ePos);
					expr = new NewArrayExpr(newType, arrayExpr, ePos);
					break;
					
				default:
					parseError("Invalid expression, expecting LPAREN|LBRACK");
				}
				break;
			default:
				parseError("Invalid expression, expected INT|ID");
				
			}
			break;
			
		case ID: case THIS:
			Reference ref = parseReference();
			Expression idExpr;
			
			switch (currentToken.kind) {
			case LBRACK:
				IdRef idRef = null;
				try {
					idRef = (IdRef) ref;
					
				} catch(Exception e) {
					parseError("Invalid use of Reference");
				}
				acceptIt();
				idExpr = parseExpression();
				accept(TokenKind.RBRACK);
				IndexedRef indexedRef1 = new IndexedRef(idRef, idExpr, idRef.posn);
				finish(ePos);
				expr = new RefExpr(indexedRef1, ePos);
				break;

			case LPAREN:
				
				acceptIt();
				ExprList argList = new ExprList();
				if (currentToken.kind != TokenKind.RPAREN)
					argList = parseArgumentList();
				accept(TokenKind.RPAREN);
				finish(ePos);
				expr = new CallExpr(ref, argList, ePos);
				break;

			default:
				finish(ePos);
				expr = new RefExpr(ref, ePos);
				break;
			}
			break;
			
		case NUM:
			IntLiteral iLit = new IntLiteral(currentToken);
			acceptIt();
			finish(ePos);
			expr = new LiteralExpr(iLit, ePos);
			break;
		
		case TRUE: case FALSE:
			BooleanLiteral boolLit = new BooleanLiteral(currentToken);
			acceptIt();
			finish(ePos);
			expr = new LiteralExpr(boolLit, ePos);
			break;
			
		case NULL:
			NullLiteral nullLit = new NullLiteral(currentToken);
			acceptIt();
			finish(ePos);
			expr = new LiteralExpr( nullLit, currentToken.posn);
			break;
			
		case LPAREN:
			acceptIt();
			expr = parseExpression();
			accept(TokenKind.RPAREN);
			break;
			
		default:
			parseError("Invalid Expression");
		}
		return expr;
	}
	
	public Type parseType() {
		Type type = null;
		SourcePosition typePos = currentToken.posn;
		start(typePos);
		switch(currentToken.kind) {
		
		case INT: case ID: 
			if(currentToken.kind == TokenKind.INT) {
				finish(typePos);
			type = new BaseType(TypeKind.INT, typePos);
			} else {
				Identifier typeID = new Identifier(currentToken);
				finish(typePos);
				type = new ClassType(typeID, typePos);
			}
			acceptIt();
			if(currentToken.kind == TokenKind.LBRACK) {
				acceptIt();
				accept(TokenKind.RBRACK);
				finish(typePos);
				type = new ArrayType(type, typePos);
			}
			break;
			
		case BOOLEAN:
			finish(typePos);
			type = new BaseType(TypeKind.BOOLEAN, typePos);
			acceptIt();
			break;
		
		default:
			parseError("Invalid Type");
		}
		
		return type;
	}
	
	public Reference parseReference() {
		Reference ref = null;
		SourcePosition refPos = currentToken.posn;
		start(refPos);
		if(currentToken.kind == TokenKind.THIS || currentToken.kind == TokenKind.ID) {
			boolean checkThis = false;
			if(currentToken.kind == TokenKind.THIS) {
				checkThis = true;
			}
			Identifier id;
			if(checkThis) {
				ref = new ThisRef(currentToken.posn);
			} else {
				id = new Identifier(currentToken);
				ref = new IdRef(id, id.posn);
			}
			acceptIt();
			while(currentToken.kind == TokenKind.PERIOD) {
				acceptIt();
				id = new Identifier(currentToken);
				accept(TokenKind.ID);
				
				ref = new QualifiedRef(ref, id, id.posn);
			}
		}
		finish(refPos);
		return ref;
	}
	public QualifiedRef parseReferenceMembers(Reference ref) {
		
		QualifiedRef qualRef = null;
		SourcePosition refPos = currentToken.posn;
		start(refPos);
		Identifier id;
		acceptIt();
		id = new Identifier(currentToken);
		accept(TokenKind.ID);
		qualRef = new QualifiedRef(ref, id, id.posn);
		while(currentToken.kind == TokenKind.PERIOD) {
			acceptIt();
			id = new Identifier(currentToken);
			accept(TokenKind.ID);
			
			qualRef = new QualifiedRef(qualRef, id, id.posn);
		}
		return qualRef;
	}
	public ParameterDeclList parseParameter() throws SyntaxError{
		ParameterDeclList paramList = new ParameterDeclList();
		Type type = parseType();
		String paramID = currentToken.spelling;
		ParameterDecl p = new ParameterDecl(type,paramID,type.posn);
		p.id = new Identifier(currentToken);
		paramList.add(p);
		accept(TokenKind.ID);
		while(currentToken.kind == TokenKind.COMMA) {
			acceptIt();
			type = parseType();
			paramID = currentToken.spelling;
			p = new ParameterDecl(type, paramID, type.posn);
			p.id = new Identifier(currentToken);
			paramList.add(p);
			accept(TokenKind.ID);
		
		}
		return paramList;
	}
	public ExprList parseArgumentList() {
		ExprList exprList = new ExprList();
		Expression e = parseExpression();
		exprList.add(e);
		while(currentToken.kind == TokenKind.COMMA) {
			acceptIt();
			e = parseExpression();
			exprList.add(e);
		}
		return exprList;
	}
	
	public boolean isBinOp() {
		return (currentToken.kind == TokenKind.GREATER || currentToken.kind == TokenKind.LESS || 
				currentToken.kind == TokenKind.EQUALS || currentToken.kind == TokenKind.LEQUALS ||
				currentToken.kind == TokenKind.GEQUALS || currentToken.kind == TokenKind.NEQUALS ||
				currentToken.kind == TokenKind.AND || currentToken.kind == TokenKind.OR ||
				currentToken.kind == TokenKind.DIVIDE || currentToken.kind == TokenKind.PLUS ||
				currentToken.kind == TokenKind.MINUS || currentToken.kind == TokenKind.TIMES
				);
	}
	public boolean isUnOp() {
		return (currentToken.kind == TokenKind.NOT || currentToken.kind == TokenKind.MINUS);
	}
	
	public Type myType(Type type) {
		if(type.typeKind == TypeKind.INT) {
			return new BaseType(TypeKind.INT, null);
		} else if(type.typeKind == TypeKind.BOOLEAN) {
			return new BaseType(TypeKind.BOOLEAN, null);
		} else if(type.typeKind == TypeKind.TEMP) {
			return new BaseType(TypeKind.TEMP, null);
		} else {
			return type;
		}
	}
}