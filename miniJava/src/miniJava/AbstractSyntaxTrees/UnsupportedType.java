package miniJava.AbstractSyntaxTrees;

import miniJava.ContextualAnalyzer.IdentificationTable;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class UnsupportedType extends ClassType {
	public static final UnsupportedType STRING_TYPE = new UnsupportedType(IdentificationTable.string_decl.id, null);
	
	public UnsupportedType(Identifier cn, SourcePosition posn) {
		super(cn, posn);
		super.className = cn;
	}
	
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitUnsupportedType(this,o);
	}
	
	public String toString() {
		return "(Unsupported) " + className.spelling;
	}

}
