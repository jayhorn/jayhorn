package soottocfg.ast;
import soottocfg.ast.Absyn.*;
/*** BNFC-Generated Visitor Design Pattern Skeleton. ***/
/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use. 
   Replace the R and A parameters with the desired return
   and context types.*/

public class VisitSkel
{
  public class ProgramFileVisitor<R,A> implements ProgramFile.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.JhPrg p, A arg)
    { /* Code For JhPrg Goes Here */
      for (Decl x: p.listdecl_)
      { /* ... */ }
      return null;
    }
  }
  public class TypeVisitor<R,A> implements Type.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.BuiltIn p, A arg)
    { /* Code For BuiltIn Goes Here */
      p.basictype_.accept(new BasicTypeVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.ClassType p, A arg)
    { /* Code For ClassType Goes Here */
      //p.ident_;
      return null;
    }
  }
  public class BasicTypeVisitor<R,A> implements BasicType.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Tint p, A arg)
    { /* Code For Tint Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Tlong p, A arg)
    { /* Code For Tlong Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Tfloat p, A arg)
    { /* Code For Tfloat Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Tdouble p, A arg)
    { /* Code For Tdouble Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.TVoid p, A arg)
    { /* Code For TVoid Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Tboolean p, A arg)
    { /* Code For Tboolean Goes Here */
      return null;
    }
  }
  public class DeclVisitor<R,A> implements Decl.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.TDecl p, A arg)
    { /* Code For TDecl Goes Here */
      //p.ident_;
      p.declbody_.accept(new DeclBodyVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.TDecl2 p, A arg)
    { /* Code For TDecl2 Goes Here */
      //p.ident_1;
      //p.ident_2;
      p.declbody_.accept(new DeclBodyVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.MDecl p, A arg)
    { /* Code For MDecl Goes Here */
      p.typelist_.accept(new TypeListVisitor<R,A>(), arg);
      p.methoddecl_.accept(new MethodDeclVisitor<R,A>(), arg);
      p.methodbody_.accept(new MethodBodyVisitor<R,A>(), arg);
      return null;
    }
  }
  public class DeclBodyVisitor<R,A> implements DeclBody.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.TDeclBody p, A arg)
    { /* Code For TDeclBody Goes Here */
      for (FieldDeclaration x: p.listfielddeclaration_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.TDeclBody2 p, A arg)
    { /* Code For TDeclBody2 Goes Here */
      for (TupleEntry x: p.listtupleentry_)
      { /* ... */ }
      for (FieldDeclaration x: p.listfielddeclaration_)
      { /* ... */ }
      return null;
    }
  }
  public class TupleEntryVisitor<R,A> implements TupleEntry.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.NamedTpl p, A arg)
    { /* Code For NamedTpl Goes Here */
      //p.ident_;
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.UNamedTpl p, A arg)
    { /* Code For UNamedTpl Goes Here */
      //p.ident_;
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }
  }
  public class TypeListVisitor<R,A> implements TypeList.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.TList1 p, A arg)
    { /* Code For TList1 Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.TList2 p, A arg)
    { /* Code For TList2 Goes Here */
      for (Types x: p.listtypes_)
      { /* ... */ }
      return null;
    }
  }
  public class TypesVisitor<R,A> implements Types.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.TNames p, A arg)
    { /* Code For TNames Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }
  }
  public class FieldDeclarationVisitor<R,A> implements FieldDeclaration.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Dvar p, A arg)
    { /* Code For Dvar Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      for (VarDecl x: p.listvardecl_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.UDvar p, A arg)
    { /* Code For UDvar Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      for (VarDecl x: p.listvardecl_)
      { /* ... */ }
      return null;
    }
  }
  public class VarDeclVisitor<R,A> implements VarDecl.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.VDecl p, A arg)
    { /* Code For VDecl Goes Here */
      //p.ident_;
      return null;
    }
  }
  public class DeclaratorNameVisitor<R,A> implements DeclaratorName.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.DeclName p, A arg)
    { /* Code For DeclName Goes Here */
      //p.ident_;
      return null;
    }
  }
  public class MethodDeclVisitor<R,A> implements MethodDecl.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Mth p, A arg)
    { /* Code For Mth Goes Here */
      p.declaratorname_.accept(new DeclaratorNameVisitor<R,A>(), arg);
      for (Parameter x: p.listparameter_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.Mth2 p, A arg)
    { /* Code For Mth2 Goes Here */
      p.declaratorname_.accept(new DeclaratorNameVisitor<R,A>(), arg);
      for (Parameter x: p.listparameter_)
      { /* ... */ }
      //p.string_;
      return null;
    }
  }
  public class ParameterVisitor<R,A> implements Parameter.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Param p, A arg)
    { /* Code For Param Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      p.declaratorname_.accept(new DeclaratorNameVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Pfinal p, A arg)
    { /* Code For Pfinal Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      p.declaratorname_.accept(new DeclaratorNameVisitor<R,A>(), arg);
      return null;
    }
  }
  public class MethodBodyVisitor<R,A> implements MethodBody.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.IBody p, A arg)
    { /* Code For IBody Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.MBody p, A arg)
    { /* Code For MBody Goes Here */
      p.body_.accept(new BodyVisitor<R,A>(), arg);
      return null;
    }
  }
  public class BodyVisitor<R,A> implements Body.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.XBody p, A arg)
    { /* Code For XBody Goes Here */
      for (LVarStatement x: p.listlvarstatement_)
      { /* ... */ }
      return null;
    }
  }
  public class LVarStatementVisitor<R,A> implements LVarStatement.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.LVar p, A arg)
    { /* Code For LVar Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      for (VarDecl x: p.listvardecl_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.LVarf p, A arg)
    { /* Code For LVarf Goes Here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      for (VarDecl x: p.listvardecl_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.Statem p, A arg)
    { /* Code For Statem Goes Here */
      p.stm_.accept(new StmVisitor<R,A>(), arg);
      return null;
    }
  }
  public class StmVisitor<R,A> implements Stm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Lbl p, A arg)
    { /* Code For Lbl Goes Here */
      //p.ident_;
      return null;
    }    public R visit(soottocfg.ast.Absyn.Asg p, A arg)
    { /* Code For Asg Goes Here */
      //p.ident_;
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.NewSt p, A arg)
    { /* Code For NewSt Goes Here */
      //p.ident_;
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Cal p, A arg)
    { /* Code For Cal Goes Here */
      for (CommaIdentList x: p.listcommaidentlist_)
      { /* ... */ }
      //p.ident_;
      for (CommaExpList x: p.listcommaexplist_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.LV p, A arg)
    { /* Code For LV Goes Here */
      for (LVarStatement x: p.listlvarstatement_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.Grd p, A arg)
    { /* Code For Grd Goes Here */
      p.guardstm_.accept(new GuardStmVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Jmp p, A arg)
    { /* Code For Jmp Goes Here */
      p.jumpstm_.accept(new JumpStmVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Iter p, A arg)
    { /* Code For Iter Goes Here */
      p.iterstm_.accept(new IterStmVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Sel p, A arg)
    { /* Code For Sel Goes Here */
      p.selectionstm_.accept(new SelectionStmVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Hps p, A arg)
    { /* Code For Hps Goes Here */
      p.heapstm_.accept(new HeapStmVisitor<R,A>(), arg);
      return null;
    }
  }
  public class CommaExpListVisitor<R,A> implements CommaExpList.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.CommaExp p, A arg)
    { /* Code For CommaExp Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
  }
  public class CommaIdentListVisitor<R,A> implements CommaIdentList.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.CommaId p, A arg)
    { /* Code For CommaId Goes Here */
      //p.ident_;
      return null;
    }
  }
  public class GuardStmVisitor<R,A> implements GuardStm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Asrt p, A arg)
    { /* Code For Asrt Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Asme p, A arg)
    { /* Code For Asme Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
  }
  public class JumpStmVisitor<R,A> implements JumpStm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Glabel p, A arg)
    { /* Code For Glabel Goes Here */
      for (CommaIdentList x: p.listcommaidentlist_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.Return p, A arg)
    { /* Code For Return Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.ReturnExp p, A arg)
    { /* Code For ReturnExp Goes Here */
      for (CommaExpList x: p.listcommaexplist_)
      { /* ... */ }
      return null;
    }
  }
  public class IterStmVisitor<R,A> implements IterStm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.While p, A arg)
    { /* Code For While Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.stm_.accept(new StmVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Do p, A arg)
    { /* Code For Do Goes Here */
      p.stm_.accept(new StmVisitor<R,A>(), arg);
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
  }
  public class SelectionStmVisitor<R,A> implements SelectionStm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Ifone p, A arg)
    { /* Code For Ifone Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.stm_.accept(new StmVisitor<R,A>(), arg);
      for (Elseif x: p.listelseif_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.If p, A arg)
    { /* Code For If Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.stm_1.accept(new StmVisitor<R,A>(), arg);
      for (Elseif x: p.listelseif_)
      { /* ... */ }
      p.stm_2.accept(new StmVisitor<R,A>(), arg);
      return null;
    }
  }
  public class ElseifVisitor<R,A> implements Elseif.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.EIf p, A arg)
    { /* Code For EIf Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.stm_.accept(new StmVisitor<R,A>(), arg);
      return null;
    }
  }
  public class HeapStmVisitor<R,A> implements HeapStm.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.PullSt p, A arg)
    { /* Code For PullSt Goes Here */
      for (CommaIdentList x: p.listcommaidentlist_)
      { /* ... */ }
      for (CommaExpList x: p.listcommaexplist_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.PushSt p, A arg)
    { /* Code For PushSt Goes Here */
      for (CommaExpList x: p.listcommaexplist_)
      { /* ... */ }
      return null;
    }    public R visit(soottocfg.ast.Absyn.HavocSt p, A arg)
    { /* Code For HavocSt Goes Here */
      for (CommaIdentList x: p.listcommaidentlist_)
      { /* ... */ }
      return null;
    }
  }
  public class ExpVisitor<R,A> implements Exp.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Etype p, A arg)
    { /* Code For Etype Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Econdition p, A arg)
    { /* Code For Econdition Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      p.exp_3.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Elor p, A arg)
    { /* Code For Elor Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Eland p, A arg)
    { /* Code For Eland Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Ebitor p, A arg)
    { /* Code For Ebitor Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Ebitexor p, A arg)
    { /* Code For Ebitexor Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Ebitand p, A arg)
    { /* Code For Ebitand Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Eeq p, A arg)
    { /* Code For Eeq Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Eneq p, A arg)
    { /* Code For Eneq Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Elthen p, A arg)
    { /* Code For Elthen Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Egrthen p, A arg)
    { /* Code For Egrthen Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Ele p, A arg)
    { /* Code For Ele Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Ege p, A arg)
    { /* Code For Ege Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Eleft p, A arg)
    { /* Code For Eleft Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Eright p, A arg)
    { /* Code For Eright Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Etrip p, A arg)
    { /* Code For Etrip Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Eplus p, A arg)
    { /* Code For Eplus Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Eminus p, A arg)
    { /* Code For Eminus Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Etimes p, A arg)
    { /* Code For Etimes Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Ediv p, A arg)
    { /* Code For Ediv Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Emod p, A arg)
    { /* Code For Emod Goes Here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Epreop p, A arg)
    { /* Code For Epreop Goes Here */
      p.unary_operator_.accept(new Unary_operatorVisitor<R,A>(), arg);
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Especname p, A arg)
    { /* Code For Especname Goes Here */
      p.specname_.accept(new SpecNameVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Efld p, A arg)
    { /* Code For Efld Goes Here */
      p.tupleacc_.accept(new TupleAccVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Econst p, A arg)
    { /* Code For Econst Goes Here */
      p.constant_.accept(new ConstantVisitor<R,A>(), arg);
      return null;
    }        public R visit(soottocfg.ast.Absyn.Evar p, A arg)
    { /* Code For Evar Goes Here */
      //p.ident_;
      return null;
    }            
  }
  public class TupleAccVisitor<R,A> implements TupleAcc.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Tplvar p, A arg)
    { /* Code For Tplvar Goes Here */
      p.specexp_.accept(new SpecExpVisitor<R,A>(), arg);
      //p.ident_;
      return null;
    }
  }
  public class SpecExpVisitor<R,A> implements SpecExp.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Cep p, A arg)
    { /* Code For Cep Goes Here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Cnp p, A arg)
    { /* Code For Cnp Goes Here */
      p.specexpnp_.accept(new SpecExpNPVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.Cthis p, A arg)
    { /* Code For Cthis Goes Here */
      p.specname_.accept(new SpecNameVisitor<R,A>(), arg);
      return null;
    }
  }
  public class SpecExpNPVisitor<R,A> implements SpecExpNP.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.CNLit p, A arg)
    { /* Code For CNLit Goes Here */
      p.constant_.accept(new ConstantVisitor<R,A>(), arg);
      return null;
    }    public R visit(soottocfg.ast.Absyn.CNPfld p, A arg)
    { /* Code For CNPfld Goes Here */
      p.tupleacc_.accept(new TupleAccVisitor<R,A>(), arg);
      return null;
    }
  }
  public class SpecNameVisitor<R,A> implements SpecName.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.SSnull p, A arg)
    { /* Code For SSnull Goes Here */
      return null;
    }
  }
  public class ConstantVisitor<R,A> implements Constant.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Edouble p, A arg)
    { /* Code For Edouble Goes Here */
      //p.double_;
      return null;
    }    public R visit(soottocfg.ast.Absyn.Eint p, A arg)
    { /* Code For Eint Goes Here */
      //p.integer_;
      return null;
    }    public R visit(soottocfg.ast.Absyn.Etrue p, A arg)
    { /* Code For Etrue Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Efalse p, A arg)
    { /* Code For Efalse Goes Here */
      return null;
    }
  }
  public class Unary_operatorVisitor<R,A> implements Unary_operator.Visitor<R,A>
  {
    public R visit(soottocfg.ast.Absyn.Plus p, A arg)
    { /* Code For Plus Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Negative p, A arg)
    { /* Code For Negative Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Complement p, A arg)
    { /* Code For Complement Goes Here */
      return null;
    }    public R visit(soottocfg.ast.Absyn.Logicalneg p, A arg)
    { /* Code For Logicalneg Goes Here */
      return null;
    }
  }
}