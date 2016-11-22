package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class MemberRef extends Reference{

	public MemberRef(Identifier id, SourcePosition posn) {
		super(posn);
		this.id = id;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitMemberRef(this, o);
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
