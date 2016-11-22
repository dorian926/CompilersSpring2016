/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class BaseType extends Type
{
	public static final BaseType BOOLEAN_TYPE = new BaseType(TypeKind.BOOLEAN, null);
	public static final BaseType INT_TYPE = new BaseType(TypeKind.INT, null);
	public static final BaseType VOID_TYPE = new BaseType(TypeKind.VOID, null);
	public static final BaseType UNSUPPORTED_TYPE = new BaseType(TypeKind.UNSUPPORTED, null);
	public static final BaseType NULL_TYPE = new BaseType(TypeKind.NULL, null);

	public BaseType(TypeKind t, SourcePosition posn){
        super(t, posn);
    }
    
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitBaseType(this, o);
    }
}
