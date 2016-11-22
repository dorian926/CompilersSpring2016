package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class DeclRef extends Reference{

	public DeclRef(Reference classReference, MemberRef memberReference, SourcePosition posn) {
		super(posn);
		this.classReference = classReference;
		this.memberReference = memberReference;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitDeclRef(this, o);
	}

	public MemberRef memberReference;
	public Reference classReference;

	@Override
	public Declaration getDeclaration() {
		return memberReference.getDeclaration();
	}

	@Override
	public void setDeclaration(Declaration decl) {
		// TODO Auto-generated method stub
		memberReference.setDeclaration(decl);
	}

}
