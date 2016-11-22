/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.ErrorReporter;
import miniJava.CodeGenerator.ClassRuntimeEntity;
import miniJava.ContextualAnalyzer.Helper;
import  miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ClassDecl extends Declaration {

  public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl, SourcePosition posn) {
	  super(cn, null, posn);
	  fieldDeclList = fdl;
	  methodDeclList = mdl;
	  id = new Identifier(new Token(TokenKind.ID, cn, posn));
	  
	  runtimeEntity.size = fdl.size();
	  int fieldDisplacement = 0;
	  for(FieldDecl fd : fdl) {
		  fd.runtimeEntity.displacement = fieldDisplacement++;
	  }
  }
  
  public <A,R> R visit(Visitor<A, R> v, A o) {
      return v.visitClassDecl(this, o);
  }
      
  public FieldDeclList fieldDeclList;
  public MethodDeclList methodDeclList;
  public ClassRuntimeEntity runtimeEntity = new ClassRuntimeEntity(0,0);
//  public Identifier id;
  
  private boolean access(MemberDecl md, Identifier id, boolean privateAccess, ErrorReporter reporter) {
	  
	  if(md.isPrivate && !privateAccess) {
		  reporter.reportError("Cannot access private member " + md.id + " here" + " at " +  id.posn);
		  return false; 
	  }
//	  if(!isStaticRef && md.isStatic) {
//		  reporter.reportError("Static member " + md.name + " can only be referenced through parent class." + " at " + id.posn);
//		  return false;
//	  }
//	  if(isStaticRef && !md.isStatic) {
//		  reporter.reportError(md.name + " is not a static member of " + super.name + " at " + id.posn);
//		  return false;
//	  }
	  
	  return true;
  }
  
  public FieldDecl getFieldDeclaration(Identifier id, boolean privateAccess, ErrorReporter reporter) {
	  for(FieldDecl fd : fieldDeclList) {
		  if (fd.name.equals(id.spelling)) {
			  if(access(fd, id, privateAccess, reporter)) {
				  return fd;
			  }
			  return null;
		  }
	  }
	  return null;
  }
  public MethodDecl getMethodDeclaration(Identifier id, boolean privateAccess, ErrorReporter reporter ) {
	  for(MethodDecl md : methodDeclList) {
		  if(md.name.equals(id.spelling)) {
			  if(access(md, id, privateAccess, reporter)) {
				  return md;
			  }
		  }
	  }
	  return null;
  }
  public MemberDecl getMemberDeclaration(Identifier id,boolean privateAccess, ErrorReporter reporter) {
		MemberDecl md = getFieldDeclaration(id, privateAccess, reporter);
		if (md == null){
			md = getMethodDeclaration(id, privateAccess, reporter);
		}
			return md;
	}
}
