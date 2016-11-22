package miniArith.AbstractSyntaxTrees;

import miniArith.SyntacticAnalyzer.Token;

public class UnaryExpr extends Expr{
	    public Token oper;
	    public Expr expr;

	    public UnaryExpr(Token oper, Expr expr) {
	        this.oper = oper;
	        this.expr = expr;
	    }
	    
	    public <Inh,Syn> Syn visit(Visitor<Inh,Syn> v, Inh arg) {
	        return v.visitUnaryExpr(this, arg);
	    }
}
