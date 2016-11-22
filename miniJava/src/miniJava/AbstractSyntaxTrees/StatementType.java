package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class StatementType extends Type {

	public StatementType(TypeKind typ, SourcePosition posn) {
		super(typ, posn);
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitStatementType(this, o);
	}

}
