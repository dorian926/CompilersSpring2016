package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class LocalRef extends IdRef {

	public LocalRef(Identifier id, SourcePosition posn) {
		super(id, posn);
		this.id = id;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitLocalRef(this, o);
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
