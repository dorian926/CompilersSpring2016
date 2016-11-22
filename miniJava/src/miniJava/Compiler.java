package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import mJAM.Disassembler;
import mJAM.Interpreter;
import mJAM.ObjectFile;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.CodeGenerator.CodeGeneration;
import miniJava.ContextualAnalyzer.MemberIdentify;
import miniJava.ContextualAnalyzer.Helper;
import miniJava.ContextualAnalyzer.Checker;
import miniJava.ContextualAnalyzer.IdentificationTable;
import miniJava.ContextualAnalyzer.ReplaceReference;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.SourceFile;

public class Compiler {
	public static void main(String[] args) {
		SourceFile source = new SourceFile(args[0]);
		String fileName = args[0];
		if(fileName == null) {
			System.out.println("Input file " + args[0] + " not found");
			System.exit(4);
		}
		if((!fileName.endsWith(".java")) && (!fileName.endsWith(".mjava"))) {
			System.out.println("Not a corrct java or mjava file extenstion");
			System.exit(4);
		}
		ErrorReporter reporter = new ErrorReporter();
		Scanner scanner = new Scanner(source, reporter);
		Parser parser = new Parser(scanner, reporter);
		AST ast = parser.parse();
		
		System.out.println("Syntactic analysis ... ");
		
		System.out.print("Syntactic analysis complete:  ");
		if (reporter.hasErrors()) {
			System.out.println("INVALID arithmetic expression");
			System.exit(4);
		}
		else {
			System.out.println("valid arithmetic expression");
//			ASTDisplay display = new ASTDisplay();
//	        display.showTree(ast);
	        
	        System.out.println("Contextual analysis ... ");
	        
			Checker check = new Checker(reporter, ast);
			MethodDecl mainMethod = check.typeCheck((miniJava.AbstractSyntaxTrees.Package) ast);
			
			System.out.print("Contextual analysis complete:  ");
			if(reporter.hasErrors()) {
				System.out.println("Contextual analysis failed");
				System.exit(4);
			} else {
				System.out.println("Contextual analysis successful!");
				//Code Generation
				String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
				CodeGeneration codeGenerator = new CodeGeneration(reporter);
				codeGenerator.visitPackage((miniJava.AbstractSyntaxTrees.Package) ast, mainMethod);
				
				String codeName = prefix + ".mJAM";
				ObjectFile objF = new ObjectFile(codeName);
				System.out.print("Writing object code file " + codeName
						+ " ... ");
				if (objF.write()) {
					System.out.println("FAILED!");
					return;
				} else {
					System.out.println("SUCCEEDED");
				}
				System.out.print("Writing assembly file ... ");
				Disassembler d = new Disassembler(codeName);
				if (d.disassemble()) {
					System.out.println("FAILED!");
					return;
				} else {
					System.out.println("SUCCEEDED");
				}

				/* run code */
				System.out.println("Running code ... ");
				Interpreter.interpret(codeName);
//				Interpreter.debug(codeName, d.getASM());
 				System.out.println("*** mJAM execution completed");
				System.exit(0);
			}
		}
	}
}
