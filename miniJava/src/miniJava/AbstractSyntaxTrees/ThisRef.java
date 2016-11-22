/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ThisRef extends Reference {
	
	public ThisRef(SourcePosition posn) {
		super(posn);
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitThisRef(this, o);
	}

	@Override
	public Declaration getDeclaration() {
		// TODO Auto-generated method stub
		return id.decl;
	}

	@Override
	public void setDeclaration(Declaration decl) {
		// TODO Auto-generated method stub
		this.id.decl = decl;
	}
	public Identifier id;
	public Declaration decl;
}
