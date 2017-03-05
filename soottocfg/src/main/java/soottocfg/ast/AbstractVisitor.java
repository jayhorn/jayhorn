package soottocfg.ast;
import soottocfg.ast.Absyn.*;
/** BNFC-Generated Abstract Visitor */
public class AbstractVisitor<R,A> implements AllVisitor<R,A> {
/* ProgramFile */
    public R visit(soottocfg.ast.Absyn.JhPrg p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.ProgramFile p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Type */
    public R visit(soottocfg.ast.Absyn.BuiltIn p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.ClassType p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Type p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* BasicType */
    public R visit(soottocfg.ast.Absyn.Tint p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Tlong p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Tfloat p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Tdouble p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.TVoid p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Tboolean p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.BasicType p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Decl */
    public R visit(soottocfg.ast.Absyn.TDecl p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.TDecl2 p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.MDecl p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Decl p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* DeclBody */
    public R visit(soottocfg.ast.Absyn.TDeclBody p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.TDeclBody2 p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.DeclBody p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* TupleEntry */
    public R visit(soottocfg.ast.Absyn.NamedTpl p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.UNamedTpl p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.TupleEntry p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* TypeList */
    public R visit(soottocfg.ast.Absyn.TList1 p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.TList2 p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.TypeList p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Types */
    public R visit(soottocfg.ast.Absyn.TNames p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Types p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* FieldDeclaration */
    public R visit(soottocfg.ast.Absyn.Dvar p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.UDvar p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.FieldDeclaration p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* VarDecl */
    public R visit(soottocfg.ast.Absyn.VDecl p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.VarDecl p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* DeclaratorName */
    public R visit(soottocfg.ast.Absyn.DeclName p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.DeclaratorName p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* MethodDecl */
    public R visit(soottocfg.ast.Absyn.Mth p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Mth2 p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.MethodDecl p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Parameter */
    public R visit(soottocfg.ast.Absyn.Param p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Pfinal p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Parameter p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* MethodBody */
    public R visit(soottocfg.ast.Absyn.IBody p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.MBody p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.MethodBody p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Body */
    public R visit(soottocfg.ast.Absyn.XBody p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Body p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* LVarStatement */
    public R visit(soottocfg.ast.Absyn.LVar p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.LVarf p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Statem p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.LVarStatement p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Stm */
    public R visit(soottocfg.ast.Absyn.Lbl p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Asg p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.NewSt p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Cal p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.LV p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Grd p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Jmp p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Iter p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Sel p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Hps p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Stm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* CommaExpList */
    public R visit(soottocfg.ast.Absyn.CommaExp p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.CommaExpList p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* CommaIdentList */
    public R visit(soottocfg.ast.Absyn.CommaId p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.CommaIdentList p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* GuardStm */
    public R visit(soottocfg.ast.Absyn.Asrt p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Asme p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.GuardStm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* JumpStm */
    public R visit(soottocfg.ast.Absyn.Glabel p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Return p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.ReturnExp p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.JumpStm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* IterStm */
    public R visit(soottocfg.ast.Absyn.While p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Do p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.IterStm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SelectionStm */
    public R visit(soottocfg.ast.Absyn.Ifone p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.If p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.SelectionStm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Elseif */
    public R visit(soottocfg.ast.Absyn.EIf p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Elseif p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* HeapStm */
    public R visit(soottocfg.ast.Absyn.PullSt p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.PushSt p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.HavocSt p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.HeapStm p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Exp */
    public R visit(soottocfg.ast.Absyn.Etype p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Econdition p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Elor p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Eland p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Ebitor p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Ebitexor p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Ebitand p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Eeq p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Eneq p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Elthen p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Egrthen p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Ele p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Ege p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Eleft p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Eright p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Etrip p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Eplus p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Eminus p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Etimes p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Ediv p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Emod p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Epreop p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Especname p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Efld p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Econst p, A arg) { return visitDefault(p, arg); }

    public R visit(soottocfg.ast.Absyn.Evar p, A arg) { return visitDefault(p, arg); }



    public R visitDefault(soottocfg.ast.Absyn.Exp p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* TupleAcc */
    public R visit(soottocfg.ast.Absyn.Tplvar p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.TupleAcc p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SpecExp */
    public R visit(soottocfg.ast.Absyn.Cep p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Cnp p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Cthis p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.SpecExp p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SpecExpNP */
    public R visit(soottocfg.ast.Absyn.CNLit p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.CNPfld p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.SpecExpNP p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SpecName */
    public R visit(soottocfg.ast.Absyn.SSnull p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.SpecName p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Constant */
    public R visit(soottocfg.ast.Absyn.Edouble p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Eint p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Etrue p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Efalse p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Constant p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Unary_operator */
    public R visit(soottocfg.ast.Absyn.Plus p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Negative p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Complement p, A arg) { return visitDefault(p, arg); }
    public R visit(soottocfg.ast.Absyn.Logicalneg p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(soottocfg.ast.Absyn.Unary_operator p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }

}
