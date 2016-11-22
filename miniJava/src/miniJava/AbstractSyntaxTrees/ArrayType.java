/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */

package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ArrayType extends Type {
	public static final ArrayType STRING_ARRAY_TYPE = new ArrayType(UnsupportedType.STRING_TYPE, null);
	
	    public ArrayType(Type eltType, SourcePosition posn){
	        super(TypeKind.ARRAY, posn);
	        this.eltType = eltType;
	    }
	        
	    public <A,R> R visit(Visitor<A,R> v, A o) {
	        return v.visitArrayType(this, o);
	    }

	    public Type eltType;
	    public ClassDecl decl = ARRAY_DECL;

		public static final ClassDecl ARRAY_DECL = new ClassDecl("Array", new FieldDeclList(),
				new MethodDeclList(), null);
		public static final FieldDecl LENGTH_DECL = new FieldDecl(false, false, BaseType.INT_TYPE, "length", null);
		static {
			LENGTH_DECL.id = new Identifier(new Token(TokenKind.ID, "length", null));
			LENGTH_DECL.id.decl = LENGTH_DECL;
			ARRAY_DECL.fieldDeclList.add(LENGTH_DECL);
		}
	}

