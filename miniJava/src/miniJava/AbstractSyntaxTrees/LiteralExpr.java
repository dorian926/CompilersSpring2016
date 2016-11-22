/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.TokenKind;

public class LiteralExpr extends Expression
{
    public LiteralExpr(Terminal t, SourcePosition posn){
        super(t.posn);
        lit = t;
        switch(lit.kind) {
        case NUM:
        	type = new BaseType(TypeKind.INT, posn);
        	break;
        case INT:
        	type = new BaseType(TypeKind.INT, posn);
        	break;
        case BOOLEAN:
        	type = new BaseType(TypeKind.BOOLEAN, posn);
        	break;
        case TRUE:
        	type = new BaseType(TypeKind.BOOLEAN, posn);
        	break;
        case FALSE:
        	type = new BaseType(TypeKind.BOOLEAN, posn);
        	break;
        default:
        	type = new BaseType(TypeKind.VOID, posn);
        }
    }
        
    public <A,R> R visit(Visitor<A,R> v, A o){
        return v.visitLiteralExpr(this, o);
    }
    
    public Terminal lit;
}