/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class IndexedRef extends Reference {
	
	public IndexedRef(IdRef idr, Expression expr, SourcePosition posn){
		super(posn);
		this.idRef = idr;
		this.indexExpr = expr;
	}

	public <A,R> R visit(Visitor<A,R> v, A o){
		return v.visitIndexedRef(this, o);
	}
	
	public IdRef idRef;
	public Expression indexExpr;
	
	@Override
	public Declaration getDeclaration() {
		return idRef.getDeclaration();
	}

	@Override
	public void setDeclaration(Declaration decl) {
		throw new RuntimeException("Should not happen");
		
	}
}
