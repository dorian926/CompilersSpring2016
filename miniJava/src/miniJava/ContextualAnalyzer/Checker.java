package miniJava.ContextualAnalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.Compiler;
import miniJava.ErrorReporter;

public class Checker implements Visitor<Type, Type>{

	public Checker(ErrorReporter reporter, AST ast) {
		this.reporter = reporter;
		MemberIdentify identify = new MemberIdentify(reporter);
		idTable = identify.createIdentificationTable(ast, reporter);
		//QualRefs
		ReplaceReference replace = new ReplaceReference(reporter);
		replace.visitPackage((miniJava.AbstractSyntaxTrees.Package) ast, idTable);
	}
	
	private IdentificationTable idTable;
	private static SourcePosition dummyPos = new SourcePosition();
	private ErrorReporter reporter;
	
	public MethodDecl typeCheck(Package prog) {
		MethodDecl mainMethod = null;
		
		visitPackage(prog, null);
		
		for(ClassDecl cd: prog.classDeclList) {
			for(MethodDecl md : cd.methodDeclList) {
				if(md.isStatic) {
					if(mainMethod == null && md.type.typeKind.equals(BaseType.VOID_TYPE.typeKind) &&!md.isPrivate
										  && md.name.equals("main") && md.parameterDeclList.size() == 1) {
						ParameterDecl pDecl = md.parameterDeclList.get(0);
						
						if(getEquiv(pDecl.type, ArrayType.STRING_ARRAY_TYPE) && ((ClassType)((ArrayType)pDecl.type).eltType).className.spelling.equals("String")) {
							mainMethod = md;
						}
					} else if(mainMethod != null && md.type.typeKind.equals(BaseType.VOID_TYPE.typeKind) &&!md.isPrivate
							  && md.name.equals("main") && md.parameterDeclList.size() == 1) {
						reportError("Multiple main methods!", prog.posn);
					}
				}
			}
		}
		if (mainMethod == null){
			reportError("Did not find a method with signature public static void main(String[] args)", prog.posn);
		}
		return mainMethod;
	}
	@Override
	public Type visitPackage(Package prog, Type arg) {
		for(ClassDecl cd : prog.classDeclList) {
			cd.visit(this, null);
		}
		return null;
	}
	@Override
	public Type visitClassDecl(ClassDecl cd, Type arg) {
		
		for(FieldDecl fd : cd.fieldDeclList) {
			fd.visit(this, null);
		}
		for(MethodDecl md : cd.methodDeclList) {
			md.visit(this, null);
		}
		return null;
	}
	@Override
	public Type visitFieldDecl(FieldDecl fd, Type arg) {
		
		//stays empty
		return null;
	}
	@Override
	public Type visitMethodDecl(MethodDecl md, Type arg) {
		for(ParameterDecl pd : md.parameterDeclList) {
			pd.visit(this, null);
		}
		for(Statement s : md.statementList) {
			s.visit(this, null);
		}
		ReturnStmt rt = new ReturnStmt(null, null);
		Type returnType = null;
		if(md.statementList.size() > 0) {
			if(md.statementList.get(md.statementList.size()-1).toString().equals(rt.toString())) {
				returnType = md.statementList.get(md.statementList.size()-1).visit(this, null);
			} else {
				returnType = BaseType.VOID_TYPE;
			}
			if(!getEquiv(returnType, md.type)) {
				reportError("Method " + md.name + " does not have a correct return type. Must return: " + md.type.typeKind, md.posn);
			}
		} else {
			if(md.type.typeKind == TypeKind.VOID) {
				returnType = BaseType.VOID_TYPE;
				if(!getEquiv(returnType, md.type)) {
					reportError("Method " + md.name + " does not have a correct return type. Must return: " + md.type.typeKind, md.posn);
				}
			} else {
				reportError("Method " + md.name + " does not have a correct return type. Must return: " + md.type.typeKind, md.posn);
			}
		}
		
		return null;
	}
	@Override
	public Type visitParameterDecl(ParameterDecl pd, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitVarDecl(VarDecl decl, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitBaseType(BaseType type, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitClassType(ClassType type, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitArrayType(ArrayType type, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitBlockStmt(BlockStmt stmt, Type arg) {
		Iterator<Statement> it = stmt.sl.iterator();
		while(it.hasNext()) {
			it.next().visit(this, null);
		}
		return null;
	}
	@Override
	public Type visitVardeclStmt(VarDeclStmt stmt, Type arg) {
		if(stmt.initExp != null) {
			Type expresType = stmt.initExp.visit(this, null);
			validateEquiv(stmt.varDecl.type, expresType, stmt.posn);
		}
		return null;
	}
	@Override
	public Type visitAssignStmt(AssignStmt stmt, Type arg) {
		if(stmt.ref == null) {
			return null;
		}
		if(stmt.ref instanceof DeclRef) {
			DeclRef ref = (DeclRef) stmt.ref;
			
			if(ref.memberReference.id.spelling.equals("length")) {
				reportError("Array length cannot be assigned",ref.posn);
			}
		}
		Type refType = stmt.ref.visit(this, null);
		Declaration decl= stmt.ref.getDeclaration();
		
		if(!(decl instanceof FieldDecl || decl instanceof LocalDecl)) {
			reportError(stmt.ref + "is not a variable", stmt.posn);
		}
		
		Type valType = stmt.val.visit(this, null);
		validateEquiv(refType, valType, stmt.posn);
		
		return null;
	}
	@Override
	public Type visitIxAssignStmt(IxAssignStmt stmt, Type arg) {
		Type refType = stmt.ixRef.visit(this, null);
		Declaration decl= stmt.ixRef.getDeclaration();
		
		if(!(decl instanceof FieldDecl || decl instanceof LocalDecl)) {
			reportError(stmt.ixRef + "is not a variable", stmt.posn);
		}
		
		Type valType = stmt.val.visit(this, null);
		validateEquiv(refType, valType, stmt.posn);
		
		return null;
	}
	@Override
	public Type visitCallStmt(CallStmt stmt, Type arg) {
		return validateMethodReference(stmt.methodRef,stmt.argList);
	}
	@Override
	public Type visitReturnStmt(ReturnStmt stmt, Type arg) {
		if(stmt.returnExpr != null) {
			return stmt.returnExpr.type;
		} else {
			return new BaseType(TypeKind.VOID, stmt.posn);
		}
	}
	@Override
	public Type visitIfStmt(IfStmt stmt, Type arg) {
		Type type = stmt.cond.visit(this, null);
		if(!getEquiv(type, BaseType.BOOLEAN_TYPE)) {
			reportError("Type error: cannot convert " + type + " to boolean", stmt.cond.posn);
			return new ErrorType(TypeKind.ERROR, stmt.cond.posn);
		}
		
		stmt.thenStmt.visit(this, null);
		if(stmt.elseStmt != null) {
			stmt.elseStmt.visit(this, null);
		}
		return new StatementType(TypeKind.TEMP, stmt.posn);
	}
	@Override
	public Type visitWhileStmt(WhileStmt stmt, Type arg) {
		if(stmt.cond.visit(this, null).typeKind != TypeKind.BOOLEAN) {
			reportError("Type error: cannot convert " + stmt.cond + " to boolean", stmt.cond.posn);
			return new ErrorType(TypeKind.ERROR, stmt.cond.posn);
		}
		stmt.body.visit(this, null);
		
		return new StatementType(TypeKind.TEMP, stmt.posn);
	}
	@Override
	public Type visitUnaryExpr(UnaryExpr expr, Type arg) {
		return expr.expr.visit(this, null);
	}
	@Override
	public Type visitBinaryExpr(BinaryExpr expr, Type arg) {
		Type left = expr.left.visit(this, null);
		Type right = expr.right.visit(this, null);
		Type result = null;
		
		switch(expr.operator.kind) {
		
		case EQUALS: case NEQUALS:
			if(!validateEquiv(left, right, expr.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.posn);
			}
			result = new BaseType(TypeKind.BOOLEAN, expr.posn);
			break;
		case OR: case AND:
			if(!validateEquiv(BaseType.BOOLEAN_TYPE, left, expr.left.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.left.posn);
			}
			if(!validateEquiv(BaseType.BOOLEAN_TYPE, right, expr.right.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.right.posn);
			}
			result = new BaseType(TypeKind.BOOLEAN, expr.posn);
			break;
		case PLUS: case MINUS: case TIMES: case DIVIDE:
			if(!validateEquiv(BaseType.INT_TYPE, left, expr.left.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.left.posn);
			}
			if(!validateEquiv(BaseType.INT_TYPE, right, expr.right.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.right.posn);
			}
			result = new BaseType(TypeKind.INT, expr.posn);
			break;
		case LEQUALS: case GEQUALS: case LESS: case GREATER:
			if(!validateEquiv(BaseType.INT_TYPE, left, expr.left.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.left.posn);
			}
			if(!validateEquiv(BaseType.INT_TYPE, right, expr.right.posn)) {
				return new ErrorType(TypeKind.ERROR, expr.right.posn);
			}
			result = new BaseType(TypeKind.BOOLEAN, expr.posn);
			break;
		default:
			return null;
		}
		return result;
	}
	@Override
	public Type visitRefExpr(RefExpr expr, Type arg) {
		return handleUnsupported(expr.ref.visit(this, null), idTable);
	}
	@Override
	public Type visitCallExpr(CallExpr expr, Type arg) {
		return validateMethodReference(expr.functionRef, expr.argList);
	}
	@Override
	public Type visitLiteralExpr(LiteralExpr expr, Type arg) {
		return expr.lit.visit(this, null);
	}
	@Override
	public Type visitNewObjectExpr(NewObjectExpr expr, Type arg) {
		return handleUnsupported(expr.classtype, idTable);
	}
	@Override
	public Type visitNewArrayExpr(NewArrayExpr expr, Type arg) {
		Type type = expr.sizeExpr.visit(this, null);
		if(type.typeKind != TypeKind.INT) {
			reportError("Type error: cannot convert from " + type + " to int.", expr.sizeExpr.posn);
			return new ErrorType(TypeKind.ERROR, expr.sizeExpr.posn);
		}
		
		return new ArrayType(expr.eltType, expr.eltType.posn);	
	}
	@Override
	public Type visitQualifiedRef(QualifiedRef ref, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitIndexedRef(IndexedRef ref, Type arg) {
		Type itype = ref.indexExpr.visit(this, null);
		if(itype.typeKind != TypeKind.INT) {
			reportError("Type error: cannot convert from " + itype + " to int.", ref.indexExpr.posn);
			return new ErrorType(TypeKind.ERROR, ref.indexExpr.posn);
		}
		
		Type rType = ref.idRef.visit(this, null);
		if (!(rType instanceof ArrayType)) {
			reportError(ref.idRef + " must be an array type but it resolved to " + rType,
					ref.idRef.posn);
			return new ErrorType(TypeKind.ERROR, ref.posn);
		}

		return handleUnsupported(((ArrayType) rType).eltType, idTable);
	}
	@Override
	public Type visitIdRef(IdRef ref, Type arg) {
		return handleUnsupported(ref.id.decl.type, idTable);
	}
	@Override
	public Type visitThisRef(ThisRef ref, Type arg) {
		return handleUnsupported(ref.getDeclaration().type, idTable);
	}
	@Override
	public Type visitIdentifier(Identifier id, Type arg) {
		return handleUnsupported(id.decl.type, idTable);
	}
	@Override
	public Type visitOperator(Operator op, Type arg) {
		//stays empty
		return null;
	}
	@Override
	public Type visitIntLiteral(IntLiteral num, Type arg) {
		return new BaseType(TypeKind.INT, num.posn);
	}
	@Override
	public Type visitBooleanLiteral(BooleanLiteral bool, Type arg) {
		return new BaseType(TypeKind.BOOLEAN, bool.posn);
	}
	@Override
	public Type visitNullLiteral(NullLiteral nul, Type arg) {
		return new BaseType(TypeKind.NULL, nul.posn);
	}
	
	private Type validateMethodReference(Reference methodRef, ExprList argList) {
		Declaration decl = methodRef.getDeclaration();
		
		if(!(decl instanceof MethodDecl)) {
			reportError("Method " + methodRef + " is undefined", methodRef.posn);
			return new ErrorType(methodRef.getDeclaration().type.typeKind,methodRef.posn);
		}
		List<Type> argTypes = new ArrayList<Type>();
		for ( Expression e : argList) {
			argTypes.add(e.visit(this, null));
		}
		
		MethodDecl methodDecl = null;
		return handleUnsupported(methodRef.getDeclaration().type, idTable);
	}
	@Override
	public Type visitUnsupportedType(UnsupportedType type, Type arg) {
		//stays here
		return null;
	}
	@Override
	public Type visitErrorType(ErrorType type, Type arg) {
		//stays here
		return null;
	}
	@Override
	public Type visitStatementType(StatementType type, Type arg) {
		//stays here
		return null;
	}
	
	public void reportError(String s, SourcePosition pos) {
		reporter.reportError("*** " + s + " at " + pos);
	}
	@Override
	public Type visitClassRef(ClassRef ref, Type arg) {
		return handleUnsupported(ref.id.decl.type, idTable);
	}
	@Override
	public Type visitMemberRef(MemberRef ref, Type arg) {
		return handleUnsupported(ref.id.decl.type, idTable);
	}
	@Override
	public Type visitLocalRef(LocalRef ref, Type arg) {
		return handleUnsupported(ref.id.decl.type, idTable);
	}
	@Override
	public Type visitBadRef(BadRef ref, Type arg) {
		return new ErrorType(TypeKind.ERROR,ref.posn);
	}
	@Override
	public Type visitDeclRef(DeclRef ref, Type arg) {
		return ref.memberReference.visit(this, null);
	}
	
	
	
	public boolean validateEquiv(Type type1, Type type2, SourcePosition pos) {
		Boolean equiv = getEquiv(type1, type2);
		if(!equiv) {
			if(type1 instanceof UnsupportedType || type2 instanceof UnsupportedType) {
				if(type1 instanceof UnsupportedType) {
					reportError(type1.typeKind + "is an unsupported type",pos);
				}
				if(type2 instanceof UnsupportedType) {
					reportError(type2.typeKind + "is an unsupported type",pos);
				}
			} else {
				reportError("Type mismatch: cannot convert from " + type2.typeKind + " to " + type1.typeKind,pos);
			}
		}
		return equiv;
	}
	
	public boolean getEquiv(Type type1, Type type2) {
		if(type1 instanceof UnsupportedType || type2 instanceof UnsupportedType) {
			return false;
		}
		if(type1 instanceof ErrorType || type2 instanceof ErrorType) {
			return true;
		}
		if((type1.typeKind == TypeKind.NULL || type2.typeKind == TypeKind.NULL) && !(type1.typeKind == TypeKind.NULL && type2.typeKind == TypeKind.NULL)) {
			if(type1.typeKind == TypeKind.INT || type2.typeKind == TypeKind.INT ||
				type1.typeKind == TypeKind.BOOLEAN || type2.typeKind == TypeKind.BOOLEAN) {
				return false;
			}
			return true;
		}
		if(type1.typeKind == TypeKind.ARRAY || type2.typeKind == TypeKind.ARRAY) {
			if(type1.typeKind == TypeKind.INT || type2.typeKind == TypeKind.INT) {
				return true;
			}
		}
		return type1.typeKind.equals(type2.typeKind);
	}
	
	public  Type handleUnsupported(Type type, IdentificationTable idTable) {
		if (type instanceof ClassType && ((ClassType) type).toString().equals("String")
		&& idTable.getScope("String") == IdentificationTable.predefined_scope) {
			return new UnsupportedType(IdentificationTable.string_decl.id, type.posn);
		}
		return type;
	}
}
