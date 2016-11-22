package miniArith.AbstractSyntaxTrees;

import miniArith.SyntacticAnalyzer.Token;

public class UnaryExpr extends Expr {
    public Token oper;
    public Expr right;

    public UnaryExpr(Token oper, Expr right) {
        this.oper = oper;
        this.right = right;
    }
	
	public <Inh,Syn> Syn visit(Visitor<Inh,Syn> v, Inh arg) {
        return v.visitUnaryExpr(this, arg);
    }
}
