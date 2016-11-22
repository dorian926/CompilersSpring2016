package miniJava.ContextualAnalyzer;

import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;

public class Helper {
	private static ErrorReporter reporter;
	public Helper(ErrorReporter reporter) {
		Helper.reporter = reporter;
	}
	public static void addDeclaration(IdentificationTable idTable, Declaration decl)  {
		try {
			idTable.set(decl);
		} catch (miniJava.ContextualAnalyzer.IdentificationTable.SyntaxError e) {
			
		}
	}
	
	public static void validateType(Type type, IdentificationTable idTable) {
		if(type instanceof BaseType || type instanceof UnsupportedType) {
			return;
		}
		
		int scope1 = idTable.getScope(type.toString());
		if(scope1 > IdentificationTable.class_scope) {
			reporter.reportError("*** " + "The type " + type.typeKind + " is not a valid class." + " at " + type.posn);
		} else if(scope1 == IdentificationTable.error_scope) {
			reporter.reportError("*** " + "The type " + type.typeKind + " is not defined" + " at " + type.posn);
		}
	}
	
	public static boolean validateEquiv(Type type1, Type type2, SourcePosition pos) {
		Boolean equiv = getEquiv(type1, type2);
		if(!equiv) {
			if(type1 instanceof UnsupportedType || type2 instanceof UnsupportedType) {
				if(type1 instanceof UnsupportedType) {
					reporter.reportError("*** " + type1.typeKind + "is an unsupported type" + " at " + pos);
				}
				if(type2 instanceof UnsupportedType) {
					reporter.reportError("*** " + type2.typeKind + "is an unsupported type" + " at " + pos);
				}
			} else {
				reporter.reportError("*** " + "Type mismatch: cannot convert from " + type2.typeKind + " to " + type1.typeKind + " at " + pos);
			}
		}
		return equiv;
	}
	
	public static boolean getEquiv(Type type1, Type type2) {
		if(type1 instanceof UnsupportedType || type2 instanceof UnsupportedType) {
			return false;
		}
		if(type1 instanceof ErrorType || type2 instanceof ErrorType) {
			return true;
		}
		return type1.typeKind.equals(type2.typeKind);
	}
	
	public static Type handleUnsupported(Type type, IdentificationTable idTable) {
		if (type instanceof ClassType && ((ClassType) type).toString().equals("String")
		&& idTable.getScope("String") == IdentificationTable.predefined_scope) {
			return new UnsupportedType(IdentificationTable.string_decl.id, type.posn);
		}
		return type;
	}
}
