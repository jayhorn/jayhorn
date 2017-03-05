package soottocfg.ast;

import soottocfg.ast.Absyn.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/** BNFC-Generated Fold Visitor */
public abstract class FoldVisitor<R,A> implements AllVisitor<R,A> {
    public abstract R leaf(A arg);
    public abstract R combine(R x, R y, A arg);

/* ProgramFile */
    public R visit(soottocfg.ast.Absyn.JhPrg p, A arg) {
      R r = leaf(arg);
      for (Decl x : p.listdecl_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* Type */
    public R visit(soottocfg.ast.Absyn.BuiltIn p, A arg) {
      R r = leaf(arg);
      r = combine(p.basictype_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.ClassType p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* BasicType */
    public R visit(soottocfg.ast.Absyn.Tint p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Tlong p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Tfloat p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Tdouble p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.TVoid p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Tboolean p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* Decl */
    public R visit(soottocfg.ast.Absyn.TDecl p, A arg) {
      R r = leaf(arg);
      r = combine(p.declbody_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.TDecl2 p, A arg) {
      R r = leaf(arg);
      r = combine(p.declbody_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.MDecl p, A arg) {
      R r = leaf(arg);
      r = combine(p.typelist_.accept(this, arg), r, arg);
      r = combine(p.methoddecl_.accept(this, arg), r, arg);
      r = combine(p.methodbody_.accept(this, arg), r, arg);
      return r;
    }

/* DeclBody */
    public R visit(soottocfg.ast.Absyn.TDeclBody p, A arg) {
      R r = leaf(arg);
      for (FieldDeclaration x : p.listfielddeclaration_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.TDeclBody2 p, A arg) {
      R r = leaf(arg);
      for (TupleEntry x : p.listtupleentry_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      for (FieldDeclaration x : p.listfielddeclaration_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* TupleEntry */
    public R visit(soottocfg.ast.Absyn.NamedTpl p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.UNamedTpl p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }

/* TypeList */
    public R visit(soottocfg.ast.Absyn.TList1 p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.TList2 p, A arg) {
      R r = leaf(arg);
      for (Types x : p.listtypes_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* Types */
    public R visit(soottocfg.ast.Absyn.TNames p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }

/* FieldDeclaration */
    public R visit(soottocfg.ast.Absyn.Dvar p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      for (VarDecl x : p.listvardecl_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.UDvar p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      for (VarDecl x : p.listvardecl_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* VarDecl */
    public R visit(soottocfg.ast.Absyn.VDecl p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* DeclaratorName */
    public R visit(soottocfg.ast.Absyn.DeclName p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* MethodDecl */
    public R visit(soottocfg.ast.Absyn.Mth p, A arg) {
      R r = leaf(arg);
      r = combine(p.declaratorname_.accept(this, arg), r, arg);
      for (Parameter x : p.listparameter_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Mth2 p, A arg) {
      R r = leaf(arg);
      r = combine(p.declaratorname_.accept(this, arg), r, arg);
      for (Parameter x : p.listparameter_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* Parameter */
    public R visit(soottocfg.ast.Absyn.Param p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      r = combine(p.declaratorname_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Pfinal p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      r = combine(p.declaratorname_.accept(this, arg), r, arg);
      return r;
    }

/* MethodBody */
    public R visit(soottocfg.ast.Absyn.IBody p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.MBody p, A arg) {
      R r = leaf(arg);
      r = combine(p.body_.accept(this, arg), r, arg);
      return r;
    }

/* Body */
    public R visit(soottocfg.ast.Absyn.XBody p, A arg) {
      R r = leaf(arg);
      for (LVarStatement x : p.listlvarstatement_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* LVarStatement */
    public R visit(soottocfg.ast.Absyn.LVar p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      for (VarDecl x : p.listvardecl_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.LVarf p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      for (VarDecl x : p.listvardecl_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Statem p, A arg) {
      R r = leaf(arg);
      r = combine(p.stm_.accept(this, arg), r, arg);
      return r;
    }

/* Stm */
    public R visit(soottocfg.ast.Absyn.Lbl p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Asg p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.NewSt p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Cal p, A arg) {
      R r = leaf(arg);
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      for (CommaExpList x : p.listcommaexplist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.LV p, A arg) {
      R r = leaf(arg);
      for (LVarStatement x : p.listlvarstatement_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Grd p, A arg) {
      R r = leaf(arg);
      r = combine(p.guardstm_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Jmp p, A arg) {
      R r = leaf(arg);
      r = combine(p.jumpstm_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Iter p, A arg) {
      R r = leaf(arg);
      r = combine(p.iterstm_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Sel p, A arg) {
      R r = leaf(arg);
      r = combine(p.selectionstm_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Hps p, A arg) {
      R r = leaf(arg);
      r = combine(p.heapstm_.accept(this, arg), r, arg);
      return r;
    }

/* CommaExpList */
    public R visit(soottocfg.ast.Absyn.CommaExp p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }

/* CommaIdentList */
    public R visit(soottocfg.ast.Absyn.CommaId p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* GuardStm */
    public R visit(soottocfg.ast.Absyn.Asrt p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Asme p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }

/* JumpStm */
    public R visit(soottocfg.ast.Absyn.Glabel p, A arg) {
      R r = leaf(arg);
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Return p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.ReturnExp p, A arg) {
      R r = leaf(arg);
      for (CommaExpList x : p.listcommaexplist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* IterStm */
    public R visit(soottocfg.ast.Absyn.While p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.stm_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Do p, A arg) {
      R r = leaf(arg);
      r = combine(p.stm_.accept(this, arg), r, arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }

/* SelectionStm */
    public R visit(soottocfg.ast.Absyn.Ifone p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.stm_.accept(this, arg), r, arg);
      for (Elseif x : p.listelseif_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.If p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.stm_1.accept(this, arg), r, arg);
      for (Elseif x : p.listelseif_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      r = combine(p.stm_2.accept(this, arg), r, arg);
      return r;
    }

/* Elseif */
    public R visit(soottocfg.ast.Absyn.EIf p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.stm_.accept(this, arg), r, arg);
      return r;
    }

/* HeapStm */
    public R visit(soottocfg.ast.Absyn.PullSt p, A arg) {
      R r = leaf(arg);
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      for (CommaExpList x : p.listcommaexplist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.PushSt p, A arg) {
      R r = leaf(arg);
      for (CommaExpList x : p.listcommaexplist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(soottocfg.ast.Absyn.HavocSt p, A arg) {
      R r = leaf(arg);
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* Exp */
    public R visit(soottocfg.ast.Absyn.Etype p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Econdition p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      r = combine(p.exp_3.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Elor p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eland p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ebitor p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ebitexor p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ebitand p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eeq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eneq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Elthen p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Egrthen p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ele p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ege p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eleft p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eright p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Etrip p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eplus p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eminus p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Etimes p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Ediv p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Emod p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Epreop p, A arg) {
      R r = leaf(arg);
      r = combine(p.unary_operator_.accept(this, arg), r, arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Especname p, A arg) {
      R r = leaf(arg);
      r = combine(p.specname_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Efld p, A arg) {
      R r = leaf(arg);
      r = combine(p.tupleacc_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Econst p, A arg) {
      R r = leaf(arg);
      r = combine(p.constant_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Evar p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* TupleAcc */
    public R visit(soottocfg.ast.Absyn.Tplvar p, A arg) {
      R r = leaf(arg);
      r = combine(p.specexp_.accept(this, arg), r, arg);
      return r;
    }

/* SpecExp */
    public R visit(soottocfg.ast.Absyn.Cep p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Cnp p, A arg) {
      R r = leaf(arg);
      r = combine(p.specexpnp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Cthis p, A arg) {
      R r = leaf(arg);
      r = combine(p.specname_.accept(this, arg), r, arg);
      return r;
    }

/* SpecExpNP */
    public R visit(soottocfg.ast.Absyn.CNLit p, A arg) {
      R r = leaf(arg);
      r = combine(p.constant_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.CNPfld p, A arg) {
      R r = leaf(arg);
      r = combine(p.tupleacc_.accept(this, arg), r, arg);
      return r;
    }

/* SpecName */
    public R visit(soottocfg.ast.Absyn.SSnull p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* Constant */
    public R visit(soottocfg.ast.Absyn.Edouble p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Eint p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Etrue p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Efalse p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* Unary_operator */
    public R visit(soottocfg.ast.Absyn.Plus p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Negative p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Complement p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(soottocfg.ast.Absyn.Logicalneg p, A arg) {
      R r = leaf(arg);
      return r;
    }


}
