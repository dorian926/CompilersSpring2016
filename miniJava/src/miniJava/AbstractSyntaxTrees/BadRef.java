package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class BadRef extends Reference{

	public BadRef(Identifier id, SourcePosition posn) {
		super(posn);
		this.id = id;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitBadRef(this, o);
	}

	public Identifier id;

	@Override
	public Declaration getDeclaration() {
		return id.decl;
	}

	@Override
	public void setDeclaration(Declaration decl) {
		// TODO Auto-generated method stub
		id.decl = decl;
	}
}
