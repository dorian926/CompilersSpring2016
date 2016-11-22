package miniJava.ContextualAnalyzer;

import java.util.ArrayList;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class MemberIdentify implements Visitor<IdentificationTable, Void>{	
	public ClassDecl currentClass;
	public MethodDecl currentMethod;
	private ErrorReporter reporter;
	public MemberIdentify(ErrorReporter reporter) {
		this.reporter = reporter;
	}
	public IdentificationTable createIdentificationTable(AST ast, ErrorReporter reporter) {
		IdentificationTable idTable = new IdentificationTable(reporter);
		ast.visit(this, idTable);
		return idTable;
	}
	@Override
	public Void visitPackage(Package prog, IdentificationTable table) {
		for(ClassDecl cd : prog.classDeclList) {
			Helper.addDeclaration(table, cd);
		}
		return null;
	}

	@Override
	public Void visitClassDecl(ClassDecl cd, IdentificationTable table) {
		cd.id.decl = cd;
		currentClass = cd;

		
		table.openScope();
		
		for(FieldDecl fd : cd.fieldDeclList) {
			fd.visit(this, table);
		}
		for(MethodDecl md : cd.methodDeclList) {
			Helper.addDeclaration(table, md);
		}
		for(FieldDecl fd : cd.fieldDeclList) {
			fd.type.visit(this, table);
		}
		for(MethodDecl md : cd.methodDeclList) {
			md.visit(this, table);
		}
		return null;
	}

	@Override
	public Void visitFieldDecl(FieldDecl fd, IdentificationTable table) {
		fd.id.decl = fd;
		
		if(fd.type.typeKind == TypeKind.VOID) {
			reportError("void is an invalid type for the field " + fd.name, fd.posn);
		} else {
			Helper.addDeclaration(table, fd);
		}
		return null;
	}

	@Override
	public Void visitMethodDecl(MethodDecl md, IdentificationTable table) {
		md.id.decl = md;
		currentMethod = md;
		md.type.visit(this, table);
		
		table.openScope();
		
		for(ParameterDecl pd : md.parameterDeclList) {
			pd.visit(this, table);
		}
		for(ParameterDecl pd : md.parameterDeclList) {
			pd.type.visit(this, table);
		}
		
		table.openScope();
		
		for(Statement st : md.statementList) {
			st.visit(this, table);
		}
		
		table.closeScope();
		table.closeScope();
		
		return null;
	}

	@Override
	public Void visitParameterDecl(ParameterDecl pd, IdentificationTable table) {
		pd.id.decl = pd;
		
		if(pd.type.typeKind == TypeKind.VOID) {
			reportError("void is not a valid  type for the variable: " + pd.name, pd.posn);
		}
		Helper.addDeclaration(table, pd);
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl decl, IdentificationTable table) {
		decl.id.decl = decl;
		return null;
	}

	@Override
	public Void visitBaseType(BaseType type, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitClassType(ClassType type, IdentificationTable table) {
		Declaration decl = table.getClassDecl(type.className.spelling);
		
		if(!(decl instanceof ClassDecl)) {
			reportError(type.className.spelling + " canot be resolved to a type.", type.posn);
			return null;
		}
		
		type.decl = (ClassDecl) decl;
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType type, IdentificationTable table) {
		type.eltType.visit(this, table);
		return null;
	}

	@Override
	public Void visitUnsupportedType(UnsupportedType type, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitErrorType(ErrorType type, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitStatementType(StatementType type, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitBlockStmt(BlockStmt stmt, IdentificationTable table) {
		table.openScope();
		
		for(Statement s : stmt.sl) {
			s.visit(this, table);
		}
		
		table.closeScope();
		
		return null;
	}

	@Override
	public Void visitVardeclStmt(VarDeclStmt stmt, IdentificationTable table) {
		if (stmt.varDecl.type.typeKind == TypeKind.VOID){
			reportError("void is an invalid type for the variable: " + stmt.varDecl.name, stmt.varDecl.posn);
		}
		stmt.varDecl.type.visit(this, table);
		stmt.varDecl.visit(this, table);

		Helper.addDeclaration(table, stmt.varDecl);
		stmt.initExp.visit(this, table);
		stmt.varDecl.initialized = true;
		return null;
	}

	@Override
	public Void visitAssignStmt(AssignStmt stmt, IdentificationTable table) {
		stmt.ref.visit(this, table);
		stmt.val.visit(this, table);
		return null;
	}

	@Override
	public Void visitIxAssignStmt(IxAssignStmt stmt, IdentificationTable table) {
		stmt.ixRef.visit(this, table);
		stmt.val.visit(this, table);
		return null;
	}

	@Override
	public Void visitCallStmt(CallStmt stmt, IdentificationTable table) {
		stmt.methodRef.visit(this, table);
		for (Expression e : stmt.argList) {
			e.visit(this, table);
		}
		return null;
	}

	@Override
	public Void visitReturnStmt(ReturnStmt stmt, IdentificationTable table) {
		// come back here
		RefExpr rExpr = new RefExpr(null, null);
		BinaryExpr bExpr = new BinaryExpr(null,null,null,null);
		CallExpr cExpr = new CallExpr(null,null,null);
		NewArrayExpr aExpr = new NewArrayExpr(null, null, null);
		UnaryExpr uExpr = new UnaryExpr(null, null, null);
		if(stmt.returnExpr != null) {
			stmt.returnExpr.visit(this, table);
			
			if(stmt.returnExpr.toString().equals(rExpr.toString())) {
				rExpr = (RefExpr) stmt.returnExpr;
				rExpr.type = rExpr.ref.getDeclaration().id.decl.type;
			} else if(stmt.returnExpr.toString().equals(bExpr.toString())) {
				bExpr = (BinaryExpr) stmt.returnExpr;
				switch(bExpr.operator.kind) {
				
				case EQUALS: case NEQUALS:
					bExpr.type = new BaseType(TypeKind.BOOLEAN, bExpr.posn);
					break;
				case OR: case AND:
					bExpr.type = new BaseType(TypeKind.BOOLEAN, bExpr.posn);
					break;
				case PLUS: case MINUS: case TIMES: case DIVIDE:
					bExpr.type = new BaseType(TypeKind.INT, bExpr.posn);
					break;
				case LEQUALS: case GEQUALS: case LESS: case GREATER:
					bExpr.type = new BaseType(TypeKind.BOOLEAN, bExpr.posn);
					break;
				default:
				}
			} else if(stmt.returnExpr.toString().equals(cExpr.toString())) {
				cExpr = (CallExpr) stmt.returnExpr;
				rExpr.type = cExpr.functionRef.getDeclaration().id.decl.type;
			} else if(stmt.returnExpr.toString().equals(aExpr.toString())) {
				aExpr = (NewArrayExpr) stmt.returnExpr;
				aExpr.type = aExpr.eltType;
			} else if(stmt.returnExpr.toString().equals(uExpr.toString())) {
				uExpr = (UnaryExpr) stmt.returnExpr;
				uExpr.type = uExpr.expr.type;
			}
		}
		return null;
	}

	@Override
	public Void visitIfStmt(IfStmt stmt, IdentificationTable table) {
		stmt.cond.visit(this, table);
		if (stmt.thenStmt instanceof VarDeclStmt) {
			reportError("Variable declaration cannot be the only statement in a conditional statement", stmt.thenStmt.posn);
		} else {
			stmt.thenStmt.visit(this, table);
		}

		if (stmt.elseStmt != null) {
			if (stmt.elseStmt instanceof VarDeclStmt) {
				reportError("Variable declaration cannot be the only statement in a conditional statement", stmt.elseStmt.posn);
			} else {
				stmt.elseStmt.visit(this, table);
			}
		}
		return null;
	}

	@Override
	public Void visitWhileStmt(WhileStmt stmt, IdentificationTable table) {
		stmt.cond.visit(this, table);

		if (stmt.body instanceof VarDeclStmt) {
			reportError("Variable declaration cannot be the only statement in a while statement", stmt.body.posn);
		} else {
			stmt.body.visit(this, table);
		}
		return null;
	}

	@Override
	public Void visitUnaryExpr(UnaryExpr expr, IdentificationTable table) {
		expr.operator.visit(this, table);
		expr.expr.visit(this, table);
		return null;
	}

	@Override
	public Void visitBinaryExpr(BinaryExpr expr, IdentificationTable table) {
		expr.operator.visit(this, table);
		expr.left.visit(this, table);
		expr.right.visit(this, table);
		return null;
	}

	@Override
	public Void visitRefExpr(RefExpr expr, IdentificationTable table) {
		expr.ref.visit(this, table);
		return null;
	}

	@Override
	public Void visitCallExpr(CallExpr expr, IdentificationTable table) {
		expr.functionRef.visit(this, table);
		for (Expression e : expr.argList) {
			e.visit(this, table);
		}
		return null;
	}

	@Override
	public Void visitLiteralExpr(LiteralExpr expr, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitNewObjectExpr(NewObjectExpr expr, IdentificationTable table) {
		expr.classtype.visit(this, table);
		return null;
	}

	@Override
	public Void visitNewArrayExpr(NewArrayExpr expr, IdentificationTable table) {
		expr.eltType.visit(this, table);
		expr.sizeExpr.visit(this, table);
		return null;
	}
	public QualifiedRef getTopRef(QualifiedRef ref) {
		QualifiedRef qr;
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(ref.ref.toString().equals(thisRef.toString())) {
			thisRef = (ThisRef) ref.ref;
			thisRef.visit(this, null);
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
	public QualifiedRef makeThisQual(ThisRef ref) {
		return new QualifiedRef(ref,ref.id,ref.posn);
	}
	public QualifiedRef makeidQual(IdRef ref) {
		return new QualifiedRef(ref,ref.id,ref.posn);
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
	public Identifier getQualRefID(QualifiedRef ref, int i) {
		ThisRef thisRef = new ThisRef(null);
		IdRef idRef = new IdRef(new Identifier(new Token(TokenKind.ID,"dummy", null)), null);
		if(i >= 1) {
			if(i == 1 && !(ref.toString().equals(thisRef.toString())) && !(ref.toString().equals(idRef.toString()))) {
				return ref.id;
			}
			try {
				return getQualRefID((QualifiedRef) ref.ref, i-1);
			} catch(Exception e) {
				try {
					ThisRef tRef = (ThisRef) ref.ref;
					return parseThisRef(tRef);
				} catch(Exception f) {
					try {
						IdRef iRef = (IdRef) ref.ref;
						return parseIdRef(iRef);
					} catch(Exception g){
						return ref.id;
					}
				}
			}
		} else {
			return ref.id;
		}
	}
	public Identifier parseThisRef(ThisRef ref) {
		return ref.id;
	}
	public Identifier parseIdRef(IdRef ref) {
		return ref.id;
	}
	@Override
	public Void visitQualifiedRef(QualifiedRef ref, IdentificationTable table) {
		Identifier id;
		//come back here
		QualifiedRef r = getTopRef(ref);
		QualifiedRef currentRef;
		boolean thisRef = getTopRefBool(ref);
		boolean isStaticRef = false;
		boolean foundAStatic = false;
		int countRef = getTopRefInt(ref);
		if(thisRef) {
			if(currentMethod.isStatic) {
				reportError("Cannot use 'this' in static context", ref.posn);
				return null;
			}
			if(ref.ref == null) {//top of stack, this has no other references
				return null;
			}
			id = new Identifier(new Token(TokenKind.THIS,"this",ref.posn));
			id.decl = currentClass;
		} else {
			id = r.id;
			
			int scope = table.declarationLink(id);
			
			if(scope == IdentificationTable.error_scope) {
				reporter.reportError("*** Undeclared identifier: " + id.spelling + " at " + id.posn);

				return null;
			}
			
			if(scope == IdentificationTable.member_scope && currentMethod.isStatic && !((MemberDecl)r.id.decl).isStatic) {
				reportError("Non-static member " + id.spelling + " cannot be directly accessed from static method " + currentMethod.name, id.posn);
				return null;
			}
			
		}
		//back here problem with vardecl to classdecl
		for(int i = (thisRef) ? 0 : 1; i < countRef; i++ ) {
			boolean currentIdentifierIsThis = (i == 0);
			
			Identifier parentID = id;
			
			ClassDecl parentClassDecl;
			if(parentID.decl.type instanceof ClassType) {
				ClassType parentClassType = (ClassType) parentID.decl.type;
//				parentClassType.decl =  (ClassDecl) parentID.decl;
				parentClassType.visit(this, table);
				if (!(parentClassType.decl instanceof ClassDecl)) {
					reportError(parentClassType.toString() + " is not a valid type", parentID.posn);
					return null;
				}
				parentClassDecl = (ClassDecl) parentClassType.decl;
//				isStaticRef = checkIfStatic(ref);
			} else if (parentID.decl.type instanceof ArrayType) {
				ArrayType parentArrayType = (ArrayType) parentID.decl.type;
				parentArrayType.visit(this, table);
				parentClassDecl = parentArrayType.decl;
			} else {
				reportError(parentID.spelling + " is not an instance or a class" + " at ", parentID.posn);
				return null;
			}
			
			boolean privateAccess = (parentClassDecl == currentClass);
	
			id = getQualRef(ref, countRef - i).id;
			
			if(currentIdentifierIsThis) {
				id.decl = currentClass;
			} else {
				id.decl = parentClassDecl.getFieldDeclaration(id, privateAccess, reporter);
				
				if(id.decl == null) {
					if(i == countRef - 1) {
						id.decl = parentClassDecl.getMethodDeclaration(id, privateAccess, reporter);
						if(id.decl == null) {
							reportError(id.spelling + " is not a member of " + parentClassDecl.name, id.posn);
							return null;
						}
					} else {
						reportError(id.spelling + " is not a field of " + parentClassDecl.id, r.id.posn);
						return null;
					}
				}
			}
		}
		if(currentMethod.isStatic) {
			boolean doCheck = true;
			for(int i = (thisRef) ? 0 : 1; i < countRef; i++ ) {
				currentRef = getQualRef(ref, countRef - i);
				if(ref.equals(currentRef)) {
					try{
						MemberDecl temp = (MemberDecl) currentRef.id.decl;
						if(isAMember(temp)) {
							if(r.id.spelling.equals(currentClass.id.spelling)){
								if(!temp.isStatic && currentMethod.isStatic){
									reportError("Trying to access non-static member: " + ref.id.spelling + " in a static instance from method: " + currentMethod.name,ref.posn);
								}
							}
						}
						doCheck = false;
					}catch(Exception e) {
						doCheck = false;
					}
				}
				if(checkIfStatic(currentRef)) {
					foundAStatic = true;
				}
			}
			if(doCheck) {
				if(!foundAStatic) {
					reportError("Trying to access non-static member: " + ref.id.spelling + " in a static instance from method: " + currentMethod.name,ref.posn);
				}
			}
		}
		isStaticRef = checkIfStatic(ref);
		
		if((r.id.spelling.equals(currentClass.id.spelling)) && (r.id.decl == currentClass.id.decl) && !(isStaticRef) && !(thisRef)) {
			reportError("Cannot access field: " + id.spelling + " of class: " + r.id.spelling + " in this manner. Need to use an instance variable", id.posn);
		}
		if(id.decl instanceof ClassDecl) {
			reportError(id.spelling + " cannot be resolved to a variable", id.posn);
		}
		return null;

	}

	@Override
	public Void visitIndexedRef(IndexedRef ref, IdentificationTable table) {
		ref.idRef.visit(this, table);
		ref.indexExpr.visit(this, table);
		return null;
	}

	@Override
	public Void visitIdRef(IdRef ref, IdentificationTable table) {
		
		int scope = table.declarationLink(ref.id);
		
		if(scope == IdentificationTable.error_scope) {
			reporter.reportError("*** Undeclared identifier: " + ref.id.spelling + " at " + ref.id.posn);
			return null;
		}
		
		if(scope == IdentificationTable.member_scope && currentMethod.isStatic &&!((MemberDecl)ref.id.decl).isStatic) {
			reportError("Non-static member " + ref.id.spelling + " cannot be accessed from static method " + currentMethod.name, ref.id.posn);
			return null;
		}
		//come back here, dealing with case of allowing same level declarations but not same line declarations.
//		if(ref.id.decl instanceof VarDecl) {
//			if( ref.posn.toString().equals(ref.id.decl.posn.toString())) {
//				reportError("*** variable declaration cannot reference variable being declared: " + ref.id.spelling + " at ", ref.id.posn);
//			}
//		}
		if (ref.id.decl instanceof VarDecl && !((VarDecl) ref.id.decl).initialized) {
			reportError("Local variable " + ref.id.spelling + " may not have been initialized", ref.id.posn);
		}
		if(ref.id.decl instanceof ClassDecl) {
			reportError(ref.id.spelling + " cannot be resolved to a variable", ref.id.posn);
		}
		return null;

	}

	@Override
	public Void visitThisRef(ThisRef ref, IdentificationTable table) {
		ref.id = currentClass.id;
		ref.decl = currentClass.id.decl;
		return null;
	}

	@Override
	public Void visitIdentifier(Identifier id, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitOperator(Operator op, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral num, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitBooleanLiteral(BooleanLiteral bool, IdentificationTable table) {
		//stays empty
		return null;
	}

	@Override
	public Void visitNullLiteral(NullLiteral nul, IdentificationTable table) {
		//stays empty
		return null;
	}

	public void reportError(String s, SourcePosition pos) {
		reporter.reportError("*** " + s + " at " + pos);
	}
	@Override
	public Void visitClassRef(ClassRef ref, IdentificationTable table) {
		//stays empty
		return null;
	}
	@Override
	public Void visitMemberRef(MemberRef ref, IdentificationTable table) {
		//stays empty
		return null;
	}
	@Override
	public Void visitLocalRef(LocalRef ref, IdentificationTable table) {
		//stays empty
		return null;
	}
	@Override
	public Void visitBadRef(BadRef ref, IdentificationTable table) {
		//stays empty
		return null;
	}
	@Override
	public Void visitDeclRef(DeclRef ref, IdentificationTable table) {
		//stays empty
		return null;
	}
	
	public boolean checkIfStatic(QualifiedRef ref) {
		try {
			MemberDecl md = (MemberDecl) ref.id.decl;
			if(md.isStatic) {
				return true;
			}
		} catch(Exception e) {}
		
		return false;
	}
	public boolean isAMember(MemberDecl md) {
		for(FieldDecl fd : currentClass.fieldDeclList) {
			if(md.equals(fd)) {
				return true;
			}
		}
		for(MethodDecl med : currentClass.methodDeclList) {
			if(md.equals(med)) {
				return true;
			}
		}
		return false;
	}
}
