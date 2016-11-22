package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.*;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;
import miniJava.ErrorReporter;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;


public class IdentificationTable {

	public static final int predefined_scope = 0;
	public static final int class_scope = 1;	
	public static final int member_scope = 2;
	public static final int parameter_scope = 3;
	public static final int local_scope = 4;
	public static final int error_scope = -1;
	
	public static final String sstring = "String";
	
	public static final String printstream = "_Printstream";
	public static final String ps_print = "print";
	public static final String ps_println = "println";
	
	public static final String system = "System";
	public static final String s_out = "out";
	
	//test main method
	public static final ClassDecl string_decl = new ClassDecl("String", 
															  new FieldDeclList(), 
															  new MethodDeclList(), 
															  null);
	
	public static MethodDecl println_decl;
	
	
	List<HashMap<String,Declaration>> scope = new ArrayList<HashMap<String, Declaration>>();
	private ErrorReporter reporter;
	
	public IdentificationTable(ErrorReporter reporter) {
		this.reporter = reporter;
		openScope();
		
		//String
		ClassDecl stringDecl = new ClassDecl(sstring,
											 new FieldDeclList(),
											 new MethodDeclList(),
											 null);
		stringDecl.type = new ClassType(new Identifier(new Token(TokenKind.ID,sstring,null)), null);
		try {
			set(stringDecl);
		} catch(SyntaxError e) {}
//		
		//Print Stream
		MethodDeclList printstreamMethods = new MethodDeclList();
		//int
		ParameterDeclList intParameter = new ParameterDeclList();
		intParameter.add(new ParameterDecl(new BaseType(TypeKind.INT, null),"n", null));
		//println(int n);
		FieldDecl fieldDecl = new FieldDecl(false, false,new BaseType(TypeKind.VOID,null), ps_println, null);
		println_decl = new MethodDecl(fieldDecl, intParameter, null, null);

		printstreamMethods.add(println_decl);
		
		ClassDecl printstreamDecl = new ClassDecl(printstream,
												  new FieldDeclList(),
												  printstreamMethods, null);
		printstreamDecl.type = new ClassType(new Identifier(new Token(TokenKind.ID,printstream,null)), null);
		try {
			set(printstreamDecl);
		} catch (SyntaxError e) {}
		
		//system
		//_printstream out
		FieldDeclList systemFields = new FieldDeclList();
//		FieldDecl _ps = new FieldDecl(false, true, )
		ClassType outIdent = new ClassType(new Identifier(new Token(TokenKind.ID,printstream,null)),null);
		outIdent.decl = printstreamDecl;
		FieldDecl out = new FieldDecl(false, true, outIdent, s_out, null );
//		systemFields.add(printstreamDecl);
		systemFields.add(out);
		//class system
		ClassDecl systemDecl = new ClassDecl(system,
											 systemFields,
											 new MethodDeclList(),
											 null);
		systemDecl.type = new ClassType(new Identifier(new Token(TokenKind.ID,system,null)), null);
		systemDecl.id = new Identifier(new Token(TokenKind.CLASS, system, null));
		try {
			set(systemDecl);
			//set(string_decl);
		} catch (SyntaxError e) {}
		
		openScope();
	}
	
	//scope methods
	//open
	public void openScope() {
		scope.add(new HashMap<String, Declaration>());
	}
	//close
	public void closeScope() throws RuntimeException{
		if(scope.size() <= 1) {
			throw new RuntimeException("Close Scope called too many times");
		}
		scope.remove(scope.size()-1);
	}
	//identifier scope
	public int getScope(String n) {
		for(int i = scope.size() - 1; i >= 0; i--) {
			if(scope.get(i).containsKey(n)) {
				return i;
			}
		}
		return error_scope;
	}
	
	//Declaration methods
	//indentifier declaration
	public Declaration get(String n) {
		Declaration decl = null;
		for(int i = scope.size() - 1; i >= 0; i--) {
			decl = scope.get(i).get(n);
			if(decl != null) {
				break;
			}
		}
		return decl;
	}
	public Declaration getClassDecl(String n) {
		Declaration decl = null;
		decl = scope.get(class_scope).get(n);
		if(decl != null) {
			return decl;
		}
		decl =  scope.get(predefined_scope).get(n);
		return decl;
	}
	//set identifier declaration
	public void set(Declaration d) throws SyntaxError {
		String name = d.name;
		//check for redeclaration
		//higher scope
		for(int i = parameter_scope; i < scope.size(); i++) {
			Declaration decl = scope.get(i).get(name);
			if(decl != null) {
				reporter.reportError("***Syntax error: " + name + " already declared. Position delcared: " + decl.posn);
				throw new SyntaxError();
			}
		}
		//current scope
		HashMap<String, Declaration> scope1 = scope.get(scope.size() - 1);
		Declaration decl = scope1.get(name);
		if(decl != null) {
			reporter.reportError("***Syntax error: " + name + " already declared. Position delcared: " + decl.posn);
			throw new SyntaxError();
		}
		
		//add
		scope1.put(name, d);
	}
	//link declaration of indentifier
	//////////make sure valid
	public int declarationLink(Identifier id) {
		Declaration decl;
		for(int i = scope.size() - 1; i >= 0; i--) {
			decl = scope.get(i).get(id.spelling);
			if(decl != null) {
				id.decl = decl;
				if (i >= local_scope) {
					return local_scope;
				}
				return i;
			}
		}
		return error_scope;
	}
	//class declarations
	public HashMap<String, Declaration> getClasses() {
		if(scope.size() - 1 >= class_scope) {
			return scope.get(class_scope);
		}
		return null;
	}
	public HashMap<String,Declaration> getClassMembers() {
		if(scope.size() - 1 >= member_scope) {
			return scope.get(member_scope);
		}
		return null;
	}
	
	//syntax error
	class SyntaxError extends Exception {
		private static final long serialVersionUID = 1L;	
	}
	
	public void display() {
		Iterator<HashMap<String, Declaration>> it = scope.iterator();
		String padding = "";

		while (it.hasNext()) {
			HashMap<String, Declaration> scope1 = it.next();

			Iterator<String> its = scope1.keySet().iterator();
			while (its.hasNext()) {
				String id = its.next();
				System.out.println(padding + "\"" + id + "\"" + ": " + scope1.get(id) + " " + getScope(id));
			}

			padding = padding + "  ";
		}
	}

}
