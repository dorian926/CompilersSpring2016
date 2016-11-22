/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class IdRef extends Reference {
	
	public IdRef(Identifier id, SourcePosition posn){
		super(posn);
		this.id = id;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitIdRef(this, o);
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
