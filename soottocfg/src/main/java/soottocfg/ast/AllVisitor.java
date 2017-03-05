package soottocfg.ast;

import soottocfg.ast.Absyn.*;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  soottocfg.ast.Absyn.ProgramFile.Visitor<R,A>,
  soottocfg.ast.Absyn.Type.Visitor<R,A>,
  soottocfg.ast.Absyn.BasicType.Visitor<R,A>,
  soottocfg.ast.Absyn.Decl.Visitor<R,A>,
  soottocfg.ast.Absyn.DeclBody.Visitor<R,A>,
  soottocfg.ast.Absyn.TupleEntry.Visitor<R,A>,
  soottocfg.ast.Absyn.TypeList.Visitor<R,A>,
  soottocfg.ast.Absyn.Types.Visitor<R,A>,
  soottocfg.ast.Absyn.FieldDeclaration.Visitor<R,A>,
  soottocfg.ast.Absyn.VarDecl.Visitor<R,A>,
  soottocfg.ast.Absyn.DeclaratorName.Visitor<R,A>,
  soottocfg.ast.Absyn.MethodDecl.Visitor<R,A>,
  soottocfg.ast.Absyn.Parameter.Visitor<R,A>,
  soottocfg.ast.Absyn.MethodBody.Visitor<R,A>,
  soottocfg.ast.Absyn.Body.Visitor<R,A>,
  soottocfg.ast.Absyn.LVarStatement.Visitor<R,A>,
  soottocfg.ast.Absyn.Stm.Visitor<R,A>,
  soottocfg.ast.Absyn.CommaExpList.Visitor<R,A>,
  soottocfg.ast.Absyn.CommaIdentList.Visitor<R,A>,
  soottocfg.ast.Absyn.GuardStm.Visitor<R,A>,
  soottocfg.ast.Absyn.JumpStm.Visitor<R,A>,
  soottocfg.ast.Absyn.IterStm.Visitor<R,A>,
  soottocfg.ast.Absyn.SelectionStm.Visitor<R,A>,
  soottocfg.ast.Absyn.Elseif.Visitor<R,A>,
  soottocfg.ast.Absyn.HeapStm.Visitor<R,A>,
  soottocfg.ast.Absyn.Exp.Visitor<R,A>,
  soottocfg.ast.Absyn.TupleAcc.Visitor<R,A>,
  soottocfg.ast.Absyn.SpecExp.Visitor<R,A>,
  soottocfg.ast.Absyn.SpecExpNP.Visitor<R,A>,
  soottocfg.ast.Absyn.SpecName.Visitor<R,A>,
  soottocfg.ast.Absyn.Constant.Visitor<R,A>,
  soottocfg.ast.Absyn.Unary_operator.Visitor<R,A>
{}
