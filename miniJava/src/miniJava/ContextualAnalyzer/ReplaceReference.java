package miniJava.ContextualAnalyzer;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ReplaceReference implements Visitor<IdentificationTable, AST> {
	private ErrorReporter reporter;
	public ReplaceReference(ErrorReporter reporter) {
		this.reporter = reporter;
	}
	@Override
	public AST visitPackage(Package prog, IdentificationTable arg) {
		for(ClassDecl cd : prog.classDeclList) {
			cd.visit(this, arg);
		}
		return null;
	}

	@Override
	public AST visitClassDecl(ClassDecl cd, IdentificationTable arg) {
		cd.id.decl = cd;
		currentClass = cd;

		MemberIdentify identify = new MemberIdentify(reporter);
		cd.visit(identify, arg);
		for (FieldDecl fd : cd.fieldDeclList){
			fd.visit(this, arg);
		}
		for (MethodDecl md : cd.methodDeclList){
			md.visit(this, arg);
		}
		arg.closeScope();
		return null;
	}

	@Override
	public AST visitFieldDecl(FieldDecl fd, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitMethodDecl(MethodDecl md, IdentificationTable arg) {
		arg.openScope();
		
		for(ParameterDecl pd : md.parameterDeclList) {
			pd.visit(this, arg);
		}
		
		arg.openScope();
		
		for(Statement st : md.statementList) {
			st.visit(this, arg);
		}
		arg.closeScope();
		arg.closeScope();
		return null;
	}

	@Override
	public AST visitParameterDecl(ParameterDecl pd, IdentificationTable arg) {
		Helper.addDeclaration(arg, pd);
		return null;
	}

	@Override
	public AST visitVarDecl(VarDecl decl, IdentificationTable arg) {
		Helper.addDeclaration(arg, decl);
		return null;
	}

	@Override
	public AST visitBaseType(BaseType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitClassType(ClassType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitArrayType(ArrayType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitUnsupportedType(UnsupportedType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitErrorType(ErrorType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitStatementType(StatementType type, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitBlockStmt(BlockStmt stmt, IdentificationTable arg) {
		arg.openScope();
		
		for(Statement s : stmt.sl) {
			s.visit(this, arg);
		}
		
		arg.closeScope();
		
		return null;
	}

	@Override
	public AST visitVardeclStmt(VarDeclStmt stmt, IdentificationTable arg) {
		stmt.varDecl.visit(this, arg);
		stmt.initExp.visit(this, arg);
		return null;
	}

	@Override
	public AST visitAssignStmt(AssignStmt stmt, IdentificationTable arg) {
		stmt.ref = (Reference) stmt.ref.visit(this, arg);
		stmt.val.visit(this, arg);
		return null;
	}

	@Override
	public AST visitIxAssignStmt(IxAssignStmt stmt, IdentificationTable arg) {
		stmt.ixRef = (IndexedRef)stmt.ixRef.visit(this, arg);
		stmt.val.visit(this, arg);
		return null;
	}

	@Override
	public AST visitCallStmt(CallStmt stmt, IdentificationTable arg) {
		stmt.methodRef = (Reference) stmt.methodRef.visit(this, arg);
		for(Expression e : stmt.argList) {
			e.visit(this, arg);
		}
		return null;
	}

	@Override
	public AST visitReturnStmt(ReturnStmt stmt, IdentificationTable arg) {
		if(stmt.returnExpr != null) {
			stmt.returnExpr.visit(this, arg);
		}
		return null;
	}

	@Override
	public AST visitIfStmt(IfStmt stmt, IdentificationTable arg) {
		stmt.cond.visit(this, arg);
		stmt.thenStmt.visit(this, arg);
		if (stmt.elseStmt != null) {
			stmt.elseStmt.visit(this, arg);
		}
		return null;
	}

	@Override
	public AST visitWhileStmt(WhileStmt stmt, IdentificationTable arg) {
		stmt.cond.visit(this, arg);
		stmt.body.visit(this, arg);
		return null;
	}

	@Override
	public AST visitUnaryExpr(UnaryExpr expr, IdentificationTable arg) {
		expr.operator.visit(this, arg);
		expr.expr.visit(this, arg);
		return null;
	}

	@Override
	public AST visitBinaryExpr(BinaryExpr expr, IdentificationTable arg) {
		expr.operator.visit(this, arg);
		expr.left.visit(this, arg);
		expr.right.visit(this, arg);
		return null;
	}

	@Override
	public AST visitRefExpr(RefExpr expr, IdentificationTable arg) {
		expr.ref = (Reference) expr.ref.visit(this, arg);
		return null;
	}

	@Override
	public AST visitCallExpr(CallExpr expr, IdentificationTable arg) {
		expr.functionRef = (Reference) expr.functionRef.visit(this, arg);
		for(Expression e : expr.argList) {
			e.visit(this, arg);
		}
		return null;
	}

	@Override
	public AST visitLiteralExpr(LiteralExpr expr, IdentificationTable arg) {
		expr.lit.visit(this, arg);
		return null;
	}

	@Override
	public AST visitNewObjectExpr(NewObjectExpr expr, IdentificationTable arg) {
		expr.classtype.visit(this, arg);
		return null;
	}

	@Override
	public AST visitNewArrayExpr(NewArrayExpr expr, IdentificationTable arg) {
		expr.eltType.visit(this, arg);
		expr.sizeExpr.visit(this, arg);
		return null;
	}
	public QualifiedRef getTopRef(QualifiedRef ref) {
		QualifiedRef qr;
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(ref.ref.toString().equals(thisRef.toString())) {
			thisRef = (ThisRef) ref.ref;
			qr = new QualifiedRef(null, thisRef.id, thisRef.posn);
			return qr;
		} else if(ref.ref.toString().equals(idRef.toString())) {
			idRef = (IdRef) ref.ref;
			qr = new QualifiedRef(null,idRef.id, idRef.posn);
			return qr;
		} else {
			return getTopRef((QualifiedRef) ref.ref);
		}
	}
	public Boolean getTopRefBool(QualifiedRef ref) {
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(ref.ref.toString().equals(thisRef.toString())) {
			return true;
		} else if(ref.ref.toString().equals(idRef.toString())) {
			return false;
		} else {
			return getTopRefBool((QualifiedRef) ref.ref);
		}
	}
	public int getTopRefInt(QualifiedRef ref) {
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(ref.ref.toString().equals(thisRef.toString())) {
			return 2;
		} else if(ref.ref.toString().equals(idRef.toString())) {
			return 2;
		} else {
			return 1 + getTopRefInt((QualifiedRef) ref.ref);
		}
	}
	public QualifiedRef getQualRef(QualifiedRef ref, int i) {
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(i >= 1) {
			if(i == 1 && !(ref.toString().equals(thisRef.toString())) && !(ref.toString().equals(idRef.toString()))) {
				return ref;
			}
			try {
				return getQualRef((QualifiedRef) ref.ref, i-1);
			} catch(Exception e) {
				try {
					ThisRef tRef = (ThisRef) ref.ref;
					return makeThisQual(tRef);
				} catch(Exception f) {
					try {
						IdRef iRef = (IdRef) ref.ref;
						return makeidQual(iRef);
					} catch(Exception g){
						return ref;
					}
				}
			}
		} else {
			return ref;
		}
	}
	public QualifiedRef makeThisQual(ThisRef ref) {
		return new QualifiedRef(ref,ref.id,ref.posn);
	}
	public QualifiedRef makeidQual(IdRef ref) {
		return new QualifiedRef(ref,ref.id,ref.posn);
	}
	@Override
	public AST visitQualifiedRef(QualifiedRef ref, IdentificationTable arg) {
		QualifiedRef r = getTopRef(ref);
		Reference newRef = null;
		int countRef = getTopRefInt(ref);
		Identifier id;
		boolean thisRef = getTopRefBool(ref);
		
		if(thisRef) {
			newRef = new ThisRef(currentClass.id.posn);
		} else {
			id = r.id;
			//a and bb not picking up the declaration from the parameterdecl
			if(id.decl instanceof ClassDecl) {
				newRef = new ClassRef(id, id.posn);
			} else if(id.decl instanceof MemberDecl) {
				newRef = new MemberRef(id, id.posn);
			} else if(id.decl instanceof LocalDecl || id.decl instanceof ParameterDecl) {
				newRef = new LocalRef(id, id.posn);
			} else {
				reportError("Undeclared identifier '" + id.spelling + "'", id.posn);
				return new BadRef(id, id.posn);
			}
		}
		
		for(int i = (thisRef) ? 0 : 1; i < countRef; i++) {
			id = getQualRef(ref, countRef - i).id;
			
			if(id.decl == null) {
				return new BadRef(id, id.posn);
			}
			
			newRef = new DeclRef(newRef, new MemberRef(id, id.posn), ref.posn);
		}
		
		return newRef;
	}

	@Override
	public AST visitIndexedRef(IndexedRef ref, IdentificationTable arg) {
		
		ref.idRef = (IdRef) ref.idRef.visit(this, arg);
//		Reference newRef = null;
//		Identifier id = ref.idRef.id;
//		
//		if(id.decl instanceof ClassDecl) {
//			newRef = new ClassRef(id, id.posn);
//		} else if(id.decl instanceof MemberDecl) {
//			newRef = new MemberRef(id, id.posn);
//		} else if(id.decl instanceof LocalDecl || id.decl instanceof ParameterDecl) {
//			newRef = new LocalRef(id, id.posn);
//		} else {
//			reportError("Undeclared identifier '" + id.spelling + "'", id.posn);
//			return new BadRef(id, id.posn);
//		}
//		
		ref.indexExpr.visit(this, arg);
		return ref;
	}

	@Override
	public AST visitIdRef(IdRef ref, IdentificationTable arg) {
		Reference newRef = null;
		Identifier id = ref.id;
		
		if(id.decl instanceof ClassDecl) {
			newRef = new ClassRef(id, id.posn);
		} else if(id.decl instanceof MemberDecl) {
			newRef = new MemberRef(id, id.posn);
		} else if(id.decl instanceof LocalDecl || id.decl instanceof ParameterDecl) {
			newRef = new LocalRef(id, id.posn);
		} else {
			reportError("Undeclared identifier '" + id.spelling + "'", id.posn);
			return new BadRef(id, id.posn);
		}
		return newRef;
	}

	@Override
	public AST visitThisRef(ThisRef ref, IdentificationTable arg) {
		if(ref.id.decl instanceof ClassDecl) {
			return ref;
		} else {
			reportError("Undeclared identifier '" + ref.id.spelling + "'", ref.id.posn);
			return new BadRef(ref.id, ref.id.posn);
		}
	}

	@Override
	public AST visitIdentifier(Identifier id, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitOperator(Operator op, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitIntLiteral(IntLiteral num, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitBooleanLiteral(BooleanLiteral bool, IdentificationTable arg) {
		//stays empty
		return null;
	}

	@Override
	public AST visitNullLiteral(NullLiteral nul, IdentificationTable arg) {
		//stays empty
		return null;
	}
	public void reportError(String s, SourcePosition pos) {
		reporter.reportError("*** " + s + " at " + pos);
	}

	public ClassDecl currentClass;
	@Override
	public AST visitClassRef(ClassRef ref, IdentificationTable arg) {
		//stays empty
		return null;
	}
	@Override
	public AST visitMemberRef(MemberRef ref, IdentificationTable arg) {
		//stays empty
		return null;
	}
	@Override
	public AST visitLocalRef(LocalRef ref, IdentificationTable arg) {
		//stays empty
		return null;
	}
	@Override
	public AST visitBadRef(BadRef ref, IdentificationTable arg) {
		//stays empty
		return null;
	}
	@Override
	public AST visitDeclRef(DeclRef ref, IdentificationTable arg) {
		//stays empty
		return null;
	}
}
