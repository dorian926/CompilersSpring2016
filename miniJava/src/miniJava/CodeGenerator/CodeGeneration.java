package miniJava.CodeGenerator;

import java.util.HashMap;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Prim;
import mJAM.Machine.Reg;
import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.ContextualAnalyzer.IdentificationTable;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class CodeGeneration implements Visitor<Object, Void> {

	public CodeGeneration(ErrorReporter reporter) {
		this.reporter = reporter;
	}
	@Override
	public Void visitPackage(Package prog, Object arg) {
		Machine.initCodeGen();
		
		int cd_index = 0;
		int fd_static_index = 0;
		for (ClassDecl cd : prog.classDeclList) {
			int fd_index = 0;
			for (FieldDecl fd : cd.fieldDeclList) {
				if(fd.isStatic) {
					Machine.emit(Op.PUSH, 1);
					fd.runtimeEntity.displacement = fd_static_index++;
				} else {
					fd.runtimeEntity.displacement = fd_index;
					fd_index++;
				}
			}
			
			int md_index = 0;
			for (MethodDecl md : cd.methodDeclList) {
				md.runtimeEntity.displacement = md_index;
				md_index++;
			}
			cd.runtimeEntity.index = cd_index;
			cd_index++;
		}
		
		Machine.emit(Op.LOADL, 0);
		Machine.emit(Op.LOADL, -1);
		int mainCallAddress = Machine.nextInstrAddr();
		Machine.emit(Op.CALL, Reg.CB, 0);
		Machine.emit(Op.HALT, 0, 0, 0);
		
		
		for (ClassDecl cd : prog.classDeclList)
			cd.visit(this, null);

		for (Integer addr : methodDisplacements.keySet())
			Machine.patch(addr, methodDisplacements.get(addr).displacement);

		MethodDecl mainMethod = (MethodDecl) arg;
		Machine.patch(mainCallAddress, mainMethod.runtimeEntity.displacement);
		return null;
	}

	@Override
	public Void visitClassDecl(ClassDecl cd, Object arg) {
//		int numAllocated = allocateSpaceForMembers(cd.fieldDeclList);
		for (FieldDecl fd : cd.fieldDeclList) {
			fd.visit(this, null);
		}
		for (MethodDecl md : cd.methodDeclList) {
			md.visit(this, null);
		}
//		localDisplacement -= numAllocated;
//		Machine.emit(Op.POP, numAllocated);
		return null;
	}

	@Override
	public Void visitFieldDecl(FieldDecl fd, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitMethodDecl(MethodDecl md, Object arg) {
		localDisplacement = 3;
		md.runtimeEntity.displacement = Machine.nextInstrAddr();

		int d = -md.parameterDeclList.size();
		for (ParameterDecl pd : md.parameterDeclList) {
			pd.visit(this, null);
			pd.runtimeEntity.displacement = d++;
		}

		int numAllocated = allocateSpaceForLocals(md.statementList);
		for (Statement s : md.statementList) {
			if(s.equals(md.statementList.get(md.statementList.size()-1)) && s instanceof ReturnStmt) {
				break;
			}
			s.visit(this, null);
		}
		ReturnStmt rt = new ReturnStmt(null, null);
		if(md.statementList.size() > 0) {
			if(md.statementList.get(md.statementList.size()-1).toString().equals(rt.toString())) {
				md.statementList.get(md.statementList.size()-1).visit(this, null);
			}
		} 
		localDisplacement -= numAllocated;
		if(md.statementList.size() > 0) {
			if(md.statementList.get(md.statementList.size()-1).toString().equals(rt.toString())) {
				Machine.emit(Op.RETURN, 1, 0, md.parameterDeclList.size());
			} else {
				Machine.emit(Op.RETURN, 0, 0, md.parameterDeclList.size());
			}
		} else {
			Machine.emit(Op.RETURN, 0, 0, md.parameterDeclList.size());
		}
		return null;
	}

	@Override
	public Void visitParameterDecl(ParameterDecl pd, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl decl, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitBaseType(BaseType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitClassType(ClassType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitUnsupportedType(UnsupportedType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitErrorType(ErrorType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitStatementType(StatementType type, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitBlockStmt(BlockStmt stmt, Object arg) {
		int numAllocated = allocateSpaceForLocals(stmt.sl);
		for (Statement s : stmt.sl)
			s.visit(this, null);
		localDisplacement -= numAllocated;
		Machine.emit(Op.POP, numAllocated);
		return null;
	}

	@Override
	public Void visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		stmt.initExp.visit(this, FetchType.VALUE);
		Machine.emit(Op.STORE, Reg.LB, stmt.varDecl.runtimeEntity.displacement);
		return null;
	}

	@Override
	public Void visitAssignStmt(AssignStmt stmt, Object arg) {
		
		Reference ref = stmt.ref;
		Expression val = stmt.val;
		
		if(ref instanceof LocalRef) {
			val.visit(this, null);
			Machine.emit(Op.STORE, Reg.LB, getLocalRefDisplacement((LocalRef) ref));
		} else if(ref instanceof MemberRef) {
			Declaration decl = ((MemberRef)ref).id.decl;

			if (decl instanceof FieldDecl) {
				FieldDecl fd = (FieldDecl) decl;
				int displacement = fd.runtimeEntity.displacement;
				if(fd.isStatic) {
					val.visit(this, null);
					Machine.emit(Op.STORE,Reg.SB,displacement);
				} else {
					Machine.emit(Op.LOADA, Reg.OB, 0);
					Machine.emit(Op.LOADL, displacement);
					val.visit(this, null);
					Machine.emit(Prim.fieldupd);
				}

				
			} else if (decl instanceof MethodDecl) {
				if ((FetchType) arg != FetchType.METHOD){
					throw new RuntimeException("MethodRef called for MethodDecl with unknown arg: " + arg);
				}
				int callInstrAddr = Machine.nextInstrAddr();
				Machine.emit(Op.CALL, Reg.CB, 0);
				methodDisplacements.put(callInstrAddr, ((MethodDecl) decl).runtimeEntity);
			} 
		}
//		if (stmt.ref instanceof LocalRef) {
//			stmt.val.visit(this, null);
//			Machine.emit(Op.STORE, Reg.LB, getLocalRefDisplacement((LocalRef) stmt.ref));
		 else {
			 if(stmt.ref instanceof DeclRef) {
				DeclRef deRef = (DeclRef) stmt.ref;
				if(deRef.memberReference.getDeclaration() instanceof FieldDecl) {
					FieldDecl fd = (FieldDecl) deRef.memberReference.getDeclaration();
					if(fd.isStatic) {
						val.visit(this, null);
						Machine.emit(Op.STORE,Reg.SB,fd.runtimeEntity.displacement);
						return null;
					}
				}
			 }
			stmt.ref.visit(this, FetchType.ADDRESS);
			stmt.val.visit(this, null);
			
			Machine.emit(Prim.fieldupd);
		}
		

		return null;
	}

	@Override
	public Void visitIxAssignStmt(IxAssignStmt stmt, Object arg) {
			stmt.ixRef.visit(this, FetchType.ADDRESS);
			stmt.val.visit(this, null);

			Machine.emit(Prim.arrayupd);
		return null;
	}

	@Override
	public Void visitCallStmt(CallStmt stmt, Object arg) {
		for (Expression exp : stmt.argList)
			exp.visit(this, null);

		MethodDecl methodDecl = (MethodDecl) stmt.methodRef.getDeclaration();
		if (methodDecl == IdentificationTable.println_decl) {
			Machine.emit(Prim.putintnl);
			return null;
		}
		if (stmt.methodRef instanceof MemberRef) {
			Machine.emit(Op.LOADA, Reg.OB, 0);
		}
		stmt.methodRef.visit(this, FetchType.METHOD);
		if (((MethodDecl) stmt.methodRef.getDeclaration()).statementList.size() > 0) {
			if (((MethodDecl)stmt.methodRef.getDeclaration()).statementList.get(((MethodDecl)stmt.methodRef.getDeclaration()).statementList.size()-1) instanceof ReturnStmt) {
				if(((ReturnStmt)((MethodDecl)stmt.methodRef.getDeclaration()).statementList.get(((MethodDecl)stmt.methodRef.getDeclaration()).statementList.size()-1)).returnExpr != null) {
					Machine.emit(Op.POP, 1);
				}
			}
		}
		return null;
	}

	@Override
	public Void visitReturnStmt(ReturnStmt stmt, Object arg) {
		//nope
		if(stmt.returnExpr != null) {
			stmt.returnExpr.visit(this, null);
		}
		
		return null;
	}

	@Override
	public Void visitIfStmt(IfStmt stmt, Object arg) {
		stmt.cond.visit(this, null);

		int elseJumpAddr = Machine.nextInstrAddr();
		Machine.emit(Op.JUMPIF, 0, Reg.CB, 0);
		stmt.thenStmt.visit(this, null);

		if (stmt.elseStmt != null) {
			int endJumpAddr = Machine.nextInstrAddr();
			Machine.emit(Op.JUMP, Reg.CB, 0);
			Machine.patch(elseJumpAddr, Machine.nextInstrAddr());
			stmt.elseStmt.visit(this, null);
			Machine.patch(endJumpAddr, Machine.nextInstrAddr());
		} else {
			Machine.patch(elseJumpAddr, Machine.nextInstrAddr());
		}

		return null;
	}

	@Override
	public Void visitWhileStmt(WhileStmt stmt, Object arg) {
		int testJumpAddr = Machine.nextInstrAddr();
		Machine.emit(Op.JUMP, 0, Reg.CB, 0);
		
		int loopStartAddr = Machine.nextInstrAddr();
		stmt.body.visit(this, null);
		
		int testStart = Machine.nextInstrAddr();
		stmt.cond.visit(this, null);
		Machine.emit(Op.JUMPIF, 1, Reg.CB, loopStartAddr);
		Machine.patch(testJumpAddr, testStart);
		return null;
	}

	@Override
	public Void visitUnaryExpr(UnaryExpr expr, Object arg) {
		expr.expr.visit(this, null);

		switch (expr.operator.kind) {
		case NEQUALS:
			Machine.emit(Prim.not);
			break;
		case MINUS:
			Machine.emit(Prim.neg);
			break;
		default:
		}
			
		return null;
	}

	@Override
	public Void visitBinaryExpr(BinaryExpr expr, Object arg) {
		

		switch (expr.operator.kind) {
		case LESS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.lt);
			break;
		case GREATER:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.gt);
			break;
		case EQUALS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.eq);
			break;
		case LEQUALS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.le);
			break;
		case GEQUALS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.ge);
			break;
		case NEQUALS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.ne);
			break;
		case PLUS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.add);
			break;
		case MINUS:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.sub);
			break;
		case TIMES:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.mult);
			break;
		case DIVIDE:
			expr.left.visit(this, null);
			expr.right.visit(this, null);
			Machine.emit(Prim.div);
			break;
		case OR:
			expr.left.visit(this, null);
			
			int patchAddr_OrSC = Machine.nextInstrAddr();
			Machine.emit(Op.JUMPIF,1, Reg.CB,-1);
			Machine.emit(Op.LOADL, 0);
			
			expr.right.visit(this, null);
			Machine.emit(Prim.or);
			
			int patchAddr_OrEnd = Machine.nextInstrAddr();
			Machine.emit(Op.JUMP, Reg.CB,-1);
			Machine.patch(patchAddr_OrSC, Machine.nextInstrAddr());
			Machine.emit(Op.LOADL, 1);
			Machine.patch(patchAddr_OrEnd, Machine.nextInstrAddr());
			break;
		case AND:
			expr.left.visit(this, null);
			
			int patchAddr_AndSC = Machine.nextInstrAddr();
			Machine.emit(Op.JUMPIF,0, Reg.CB,-1);
			Machine.emit(Op.LOADL, 1);
			
			expr.right.visit(this, null);
			Machine.emit(Prim.and);
			
			int patchAddr_AndEnd = Machine.nextInstrAddr();
			Machine.emit(Op.JUMP, Reg.CB,-1);
			Machine.patch(patchAddr_AndSC, Machine.nextInstrAddr());
			Machine.emit(Op.LOADL, 0);
			Machine.patch(patchAddr_AndEnd, Machine.nextInstrAddr());
			break;
		default:
			
		}

		return null;
	}

	@Override
	public Void visitRefExpr(RefExpr expr, Object arg) {
		expr.ref.visit(this, FetchType.VALUE);
		return null;
	}

	@Override
	public Void visitCallExpr(CallExpr expr, Object arg) {
		for (Expression exp : expr.argList)
			exp.visit(this, null);

		if (expr.functionRef instanceof MemberRef) {
			Machine.emit(Op.LOADA, Reg.OB, 0);
		} 
		expr.functionRef.visit(this, FetchType.METHOD);
		
		return null;
	}

	@Override
	public Void visitLiteralExpr(LiteralExpr expr, Object arg) {
		expr.lit.visit(this, null);
		return null;
	}

	@Override
	public Void visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		Machine.emit(Op.LOADL, expr.classtype.decl.runtimeEntity.index);
//		Machine.emit(Op.LOADL, -1);
		Machine.emit(Op.LOADL, expr.classtype.decl.runtimeEntity.size);
		Machine.emit(Prim.newobj);
		return null;
	}

	@Override
	public Void visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		expr.sizeExpr.visit(this, null);
		Machine.emit(Prim.newarr);
		return null;
	}

	@Override
	public Void visitQualifiedRef(QualifiedRef ref, Object arg) {
		//nope
		throw new RuntimeException("nope");
	}

	@Override
	public Void visitIndexedRef(IndexedRef ref, Object arg) {
		switch ((FetchType) arg) {
		case ADDRESS:
			ref.idRef.visit(this, FetchType.VALUE);
			ref.indexExpr.visit(this, null);
			break;

		case VALUE:
			ref.idRef.visit(this, FetchType.VALUE);
			ref.indexExpr.visit(this, null);
			Machine.emit(Prim.arrayref);
			break;
		default:
		}
		return null;
	}

	@Override
	public Void visitIdRef(IdRef ref, Object arg) {
		//nope
		throw new RuntimeException("nope");
	}

	@Override
	public Void visitThisRef(ThisRef ref, Object arg) {
		Machine.emit(Op.LOADA, Reg.OB, 0);
		return null;
	}

	@Override
	public Void visitClassRef(ClassRef ref, Object arg) {
		Machine.emit(Op.LOADL, 0);
		return null;
	}

	@Override
	public Void visitMemberRef(MemberRef ref, Object arg) {
		Declaration decl = ref.id.decl;
		
		if (decl instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) decl;
			int displacement = fd.runtimeEntity.displacement;
			if(ref.getDeclaration().type instanceof ClassType){
				Machine.emit(Op.LOADA, Reg.OB, 0);
				Machine.emit(Op.LOADL, displacement);
				
				if (((FetchType) arg) == FetchType.VALUE) {
					Machine.emit(Prim.fieldref);
				}
			} else {
				if(fd.isStatic) {
//					Machine.emit(Op.LOADL, 0);
					Machine.emit(Op.LOAD, Reg.SB, fd.runtimeEntity.displacement);
				} else {
					Machine.emit(Op.LOADA, Reg.OB, 0);
					Machine.emit(Op.LOADL, displacement);
					
					if (((FetchType) arg) == FetchType.VALUE) {
						Machine.emit(Prim.fieldref);
					}
				}


			}
			

		} else if (decl instanceof MethodDecl) {
			if ((FetchType) arg != FetchType.METHOD){
				throw new RuntimeException("MethodRef called for MethodDecl with unknown arg: " + arg);
			}
			int callInstrAddr = Machine.nextInstrAddr();
			if(ref.getDeclaration().type instanceof ClassType) {
				Machine.emit(Op.CALLI, Reg.CB, 0);
			} else {
				Machine.emit(Op.CALLI, Reg.CB, 0);
			}
			
			
			methodDisplacements.put(callInstrAddr, ((MethodDecl) decl).runtimeEntity);
		} 
		return null;
	}

	@Override
	public Void visitLocalRef(LocalRef ref, Object arg) {
		if ((FetchType) arg != FetchType.VALUE)
			throw new RuntimeException("visitLocalRef should only be called for VALUE");
//		if(ref.getDeclaration().type instanceof ClassType) {
//			Machine.emit(Op.LOADA, Reg.SB, getLocalRefDisplacement(ref));
//		} else {
			Machine.emit(Op.LOAD, Reg.LB, getLocalRefDisplacement(ref));
//		}
		return null;
	}

	@Override
	public Void visitBadRef(BadRef ref, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitDeclRef(DeclRef ref, Object arg) {
		ref.classReference.visit(this, FetchType.VALUE);
		Declaration memberDecl = ref.memberReference.getDeclaration();
		if (memberDecl == ArrayType.LENGTH_DECL) {
			if ((FetchType) arg != FetchType.VALUE) {
				reportError("Cannot modify the length field of an array", ref.memberReference.posn);
				return null;
			}
			Machine.emit(Prim.pred);
			Machine.emit(Op.LOADI);
			return null;
		}
		
		switch ((FetchType) arg) {
		case ADDRESS:
//			if(ref.memberReference.id.decl.type instanceof ClassType) {
//				Machine.emit(Op.LOADA, ((FieldDecl)ref.memberReference.id.decl).runtimeEntity.displacement);
//			} else {
//				Machine.emit(Op.LOADL, ((FieldDecl) ref.memberReference.id.decl).runtimeEntity.displacement);
//			}
				FieldDecl fd = (FieldDecl) ref.memberReference.id.decl;
				if(fd.isStatic) {
					break;
//					Machine.emit(Prim.fieldref);
				} else {
					Machine.emit(Op.LOADL, fd.runtimeEntity.displacement);
				}
			break;

		case VALUE:
			if(ref.memberReference.id.decl instanceof ClassDecl) {
				if(ref.classReference instanceof ThisRef) {
					break;	
				} 
				Machine.emit(Op.LOADL, ((ClassDecl) ref.memberReference.id.decl).runtimeEntity.size);
			} else {
				FieldDecl fd1 = (FieldDecl) ref.memberReference.id.decl;
				if(fd1.isStatic) {
					Machine.emit(Op.LOAD, Reg.SB, fd1.runtimeEntity.displacement);
					break;
				} else {
					Machine.emit(Op.LOADL, fd1.runtimeEntity.displacement);
				}
				
			}
			Machine.emit(Prim.fieldref);
			break;

		case METHOD:
			ref.memberReference.visit(this, FetchType.METHOD);
			break;
		}

		return null;
	}

	@Override
	public Void visitIdentifier(Identifier id, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitOperator(Operator op, Object arg) {
		//nope
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral num, Object arg) {
		Machine.emit(Op.LOADL, Integer.parseInt(num.spelling));
		return null;
	}

	@Override
	public Void visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		Machine.emit(Op.LOADL, bool.spelling.equals("true") ? 1 : 0);
		return null;
	}

	@Override
	public Void visitNullLiteral(NullLiteral nul, Object arg) {
		Machine.emit(Op.LOADL, Machine.nullRep);
		return null;
	}

	enum FetchType {ADDRESS, VALUE, METHOD};
	int localDisplacement;
	HashMap<Integer, MethodRuntimeEntity> methodDisplacements = new HashMap<Integer, MethodRuntimeEntity>();
	ErrorReporter reporter;
	
	private int allocateSpaceForMembers(FieldDeclList fl) {
		int numAllocated = 0;

		for (FieldDecl f : fl) {
			f.runtimeEntity.displacement = localDisplacement + numAllocated;
			numAllocated++;
		}

		Machine.emit(Op.PUSH, numAllocated);
		localDisplacement += numAllocated;

		return numAllocated;
	}
	private void deallocateSpaceForMembers(FieldDeclList fl) {

		for (FieldDecl f : fl) {
			Machine.emit(Op.POP, f.runtimeEntity.displacement);
		}

	}
	private int allocateSpaceForLocals(StatementList sl) {
		int numAllocated = 0;

		for (Statement s : sl) {
			if (s instanceof VarDeclStmt) {
				VarDecl v = ((VarDeclStmt) s).varDecl;
				v.runtimeEntity.displacement = localDisplacement + numAllocated;
				numAllocated++;
			}
		}

		Machine.emit(Op.PUSH, numAllocated);
		localDisplacement += numAllocated;

		return numAllocated;
	}
	private int getLocalRefDisplacement(LocalRef ref) {
		Declaration decl = ref.id.decl;
		if (decl instanceof VarDecl) {
			return ((VarDecl) decl).runtimeEntity.displacement;
		} else if (decl instanceof ParameterDecl) {
			return ((ParameterDecl) decl).runtimeEntity.displacement;
		} else {
			throw new RuntimeException("LocalRef is not a variable or parameter!");
		}
	}
	
	private void reportError(String s, SourcePosition pos) {
		reporter.reportError(s + " at " + pos);
	}
}
