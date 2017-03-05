package soottocfg.ast;
import soottocfg.ast.Absyn.*;
/** BNFC-Generated Composition Visitor
*/

public class ComposVisitor<A> implements
  soottocfg.ast.Absyn.ProgramFile.Visitor<soottocfg.ast.Absyn.ProgramFile,A>,
  soottocfg.ast.Absyn.Type.Visitor<soottocfg.ast.Absyn.Type,A>,
  soottocfg.ast.Absyn.BasicType.Visitor<soottocfg.ast.Absyn.BasicType,A>,
  soottocfg.ast.Absyn.Decl.Visitor<soottocfg.ast.Absyn.Decl,A>,
  soottocfg.ast.Absyn.DeclBody.Visitor<soottocfg.ast.Absyn.DeclBody,A>,
  soottocfg.ast.Absyn.TupleEntry.Visitor<soottocfg.ast.Absyn.TupleEntry,A>,
  soottocfg.ast.Absyn.TypeList.Visitor<soottocfg.ast.Absyn.TypeList,A>,
  soottocfg.ast.Absyn.Types.Visitor<soottocfg.ast.Absyn.Types,A>,
  soottocfg.ast.Absyn.FieldDeclaration.Visitor<soottocfg.ast.Absyn.FieldDeclaration,A>,
  soottocfg.ast.Absyn.VarDecl.Visitor<soottocfg.ast.Absyn.VarDecl,A>,
  soottocfg.ast.Absyn.DeclaratorName.Visitor<soottocfg.ast.Absyn.DeclaratorName,A>,
  soottocfg.ast.Absyn.MethodDecl.Visitor<soottocfg.ast.Absyn.MethodDecl,A>,
  soottocfg.ast.Absyn.Parameter.Visitor<soottocfg.ast.Absyn.Parameter,A>,
  soottocfg.ast.Absyn.MethodBody.Visitor<soottocfg.ast.Absyn.MethodBody,A>,
  soottocfg.ast.Absyn.Body.Visitor<soottocfg.ast.Absyn.Body,A>,
  soottocfg.ast.Absyn.LVarStatement.Visitor<soottocfg.ast.Absyn.LVarStatement,A>,
  soottocfg.ast.Absyn.Stm.Visitor<soottocfg.ast.Absyn.Stm,A>,
  soottocfg.ast.Absyn.CommaExpList.Visitor<soottocfg.ast.Absyn.CommaExpList,A>,
  soottocfg.ast.Absyn.CommaIdentList.Visitor<soottocfg.ast.Absyn.CommaIdentList,A>,
  soottocfg.ast.Absyn.GuardStm.Visitor<soottocfg.ast.Absyn.GuardStm,A>,
  soottocfg.ast.Absyn.JumpStm.Visitor<soottocfg.ast.Absyn.JumpStm,A>,
  soottocfg.ast.Absyn.IterStm.Visitor<soottocfg.ast.Absyn.IterStm,A>,
  soottocfg.ast.Absyn.SelectionStm.Visitor<soottocfg.ast.Absyn.SelectionStm,A>,
  soottocfg.ast.Absyn.Elseif.Visitor<soottocfg.ast.Absyn.Elseif,A>,
  soottocfg.ast.Absyn.HeapStm.Visitor<soottocfg.ast.Absyn.HeapStm,A>,
  soottocfg.ast.Absyn.Exp.Visitor<soottocfg.ast.Absyn.Exp,A>,
  soottocfg.ast.Absyn.TupleAcc.Visitor<soottocfg.ast.Absyn.TupleAcc,A>,
  soottocfg.ast.Absyn.SpecExp.Visitor<soottocfg.ast.Absyn.SpecExp,A>,
  soottocfg.ast.Absyn.SpecExpNP.Visitor<soottocfg.ast.Absyn.SpecExpNP,A>,
  soottocfg.ast.Absyn.SpecName.Visitor<soottocfg.ast.Absyn.SpecName,A>,
  soottocfg.ast.Absyn.Constant.Visitor<soottocfg.ast.Absyn.Constant,A>,
  soottocfg.ast.Absyn.Unary_operator.Visitor<soottocfg.ast.Absyn.Unary_operator,A>
{
/* ProgramFile */
    public ProgramFile visit(soottocfg.ast.Absyn.JhPrg p, A arg)
    {
      ListDecl listdecl_ = new ListDecl();
      for (Decl x : p.listdecl_)
      {
        listdecl_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.JhPrg(listdecl_);
    }
/* Type */
    public Type visit(soottocfg.ast.Absyn.BuiltIn p, A arg)
    {
      BasicType basictype_ = p.basictype_.accept(this, arg);
      return new soottocfg.ast.Absyn.BuiltIn(basictype_);
    }    public Type visit(soottocfg.ast.Absyn.ClassType p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.ClassType(ident_);
    }
/* BasicType */
    public BasicType visit(soottocfg.ast.Absyn.Tint p, A arg)
    {
      return new soottocfg.ast.Absyn.Tint();
    }    public BasicType visit(soottocfg.ast.Absyn.Tlong p, A arg)
    {
      return new soottocfg.ast.Absyn.Tlong();
    }    public BasicType visit(soottocfg.ast.Absyn.Tfloat p, A arg)
    {
      return new soottocfg.ast.Absyn.Tfloat();
    }    public BasicType visit(soottocfg.ast.Absyn.Tdouble p, A arg)
    {
      return new soottocfg.ast.Absyn.Tdouble();
    }    public BasicType visit(soottocfg.ast.Absyn.TVoid p, A arg)
    {
      return new soottocfg.ast.Absyn.TVoid();
    }    public BasicType visit(soottocfg.ast.Absyn.Tboolean p, A arg)
    {
      return new soottocfg.ast.Absyn.Tboolean();
    }
/* Decl */
    public Decl visit(soottocfg.ast.Absyn.TDecl p, A arg)
    {
      String ident_ = p.ident_;
      DeclBody declbody_ = p.declbody_.accept(this, arg);
      return new soottocfg.ast.Absyn.TDecl(ident_, declbody_);
    }    public Decl visit(soottocfg.ast.Absyn.TDecl2 p, A arg)
    {
      String ident_1 = p.ident_1;
      String ident_2 = p.ident_2;
      DeclBody declbody_ = p.declbody_.accept(this, arg);
      return new soottocfg.ast.Absyn.TDecl2(ident_1, ident_2, declbody_);
    }    public Decl visit(soottocfg.ast.Absyn.MDecl p, A arg)
    {
      TypeList typelist_ = p.typelist_.accept(this, arg);
      MethodDecl methoddecl_ = p.methoddecl_.accept(this, arg);
      MethodBody methodbody_ = p.methodbody_.accept(this, arg);
      return new soottocfg.ast.Absyn.MDecl(typelist_, methoddecl_, methodbody_);
    }
/* DeclBody */
    public DeclBody visit(soottocfg.ast.Absyn.TDeclBody p, A arg)
    {
      ListFieldDeclaration listfielddeclaration_ = new ListFieldDeclaration();
      for (FieldDeclaration x : p.listfielddeclaration_)
      {
        listfielddeclaration_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.TDeclBody(listfielddeclaration_);
    }    public DeclBody visit(soottocfg.ast.Absyn.TDeclBody2 p, A arg)
    {
      ListTupleEntry listtupleentry_ = new ListTupleEntry();
      for (TupleEntry x : p.listtupleentry_)
      {
        listtupleentry_.add(x.accept(this,arg));
      }
      ListFieldDeclaration listfielddeclaration_ = new ListFieldDeclaration();
      for (FieldDeclaration x : p.listfielddeclaration_)
      {
        listfielddeclaration_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.TDeclBody2(listtupleentry_, listfielddeclaration_);
    }
/* TupleEntry */
    public TupleEntry visit(soottocfg.ast.Absyn.NamedTpl p, A arg)
    {
      String ident_ = p.ident_;
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.NamedTpl(ident_, type_);
    }    public TupleEntry visit(soottocfg.ast.Absyn.UNamedTpl p, A arg)
    {
      String ident_ = p.ident_;
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.UNamedTpl(ident_, type_);
    }
/* TypeList */
    public TypeList visit(soottocfg.ast.Absyn.TList1 p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.TList1(type_);
    }    public TypeList visit(soottocfg.ast.Absyn.TList2 p, A arg)
    {
      ListTypes listtypes_ = new ListTypes();
      for (Types x : p.listtypes_)
      {
        listtypes_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.TList2(listtypes_);
    }
/* Types */
    public Types visit(soottocfg.ast.Absyn.TNames p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.TNames(type_);
    }
/* FieldDeclaration */
    public FieldDeclaration visit(soottocfg.ast.Absyn.Dvar p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      ListVarDecl listvardecl_ = new ListVarDecl();
      for (VarDecl x : p.listvardecl_)
      {
        listvardecl_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.Dvar(type_, listvardecl_);
    }    public FieldDeclaration visit(soottocfg.ast.Absyn.UDvar p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      ListVarDecl listvardecl_ = new ListVarDecl();
      for (VarDecl x : p.listvardecl_)
      {
        listvardecl_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.UDvar(type_, listvardecl_);
    }
/* VarDecl */
    public VarDecl visit(soottocfg.ast.Absyn.VDecl p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.VDecl(ident_);
    }
/* DeclaratorName */
    public DeclaratorName visit(soottocfg.ast.Absyn.DeclName p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.DeclName(ident_);
    }
/* MethodDecl */
    public MethodDecl visit(soottocfg.ast.Absyn.Mth p, A arg)
    {
      DeclaratorName declaratorname_ = p.declaratorname_.accept(this, arg);
      ListParameter listparameter_ = new ListParameter();
      for (Parameter x : p.listparameter_)
      {
        listparameter_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.Mth(declaratorname_, listparameter_);
    }    public MethodDecl visit(soottocfg.ast.Absyn.Mth2 p, A arg)
    {
      DeclaratorName declaratorname_ = p.declaratorname_.accept(this, arg);
      ListParameter listparameter_ = new ListParameter();
      for (Parameter x : p.listparameter_)
      {
        listparameter_.add(x.accept(this,arg));
      }
      String string_ = p.string_;
      return new soottocfg.ast.Absyn.Mth2(declaratorname_, listparameter_, string_);
    }
/* Parameter */
    public Parameter visit(soottocfg.ast.Absyn.Param p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      DeclaratorName declaratorname_ = p.declaratorname_.accept(this, arg);
      return new soottocfg.ast.Absyn.Param(type_, declaratorname_);
    }    public Parameter visit(soottocfg.ast.Absyn.Pfinal p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      DeclaratorName declaratorname_ = p.declaratorname_.accept(this, arg);
      return new soottocfg.ast.Absyn.Pfinal(type_, declaratorname_);
    }
/* MethodBody */
    public MethodBody visit(soottocfg.ast.Absyn.IBody p, A arg)
    {
      return new soottocfg.ast.Absyn.IBody();
    }    public MethodBody visit(soottocfg.ast.Absyn.MBody p, A arg)
    {
      Body body_ = p.body_.accept(this, arg);
      return new soottocfg.ast.Absyn.MBody(body_);
    }
/* Body */
    public Body visit(soottocfg.ast.Absyn.XBody p, A arg)
    {
      ListLVarStatement listlvarstatement_ = new ListLVarStatement();
      for (LVarStatement x : p.listlvarstatement_)
      {
        listlvarstatement_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.XBody(listlvarstatement_);
    }
/* LVarStatement */
    public LVarStatement visit(soottocfg.ast.Absyn.LVar p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      ListVarDecl listvardecl_ = new ListVarDecl();
      for (VarDecl x : p.listvardecl_)
      {
        listvardecl_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.LVar(type_, listvardecl_);
    }    public LVarStatement visit(soottocfg.ast.Absyn.LVarf p, A arg)
    {
      Type type_ = p.type_.accept(this, arg);
      ListVarDecl listvardecl_ = new ListVarDecl();
      for (VarDecl x : p.listvardecl_)
      {
        listvardecl_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.LVarf(type_, listvardecl_);
    }    public LVarStatement visit(soottocfg.ast.Absyn.Statem p, A arg)
    {
      Stm stm_ = p.stm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Statem(stm_);
    }
/* Stm */
    public Stm visit(soottocfg.ast.Absyn.Lbl p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.Lbl(ident_);
    }    public Stm visit(soottocfg.ast.Absyn.Asg p, A arg)
    {
      String ident_ = p.ident_;
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Asg(ident_, exp_);
    }    public Stm visit(soottocfg.ast.Absyn.NewSt p, A arg)
    {
      String ident_ = p.ident_;
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.NewSt(ident_, type_);
    }    public Stm visit(soottocfg.ast.Absyn.Cal p, A arg)
    {
      ListCommaIdentList listcommaidentlist_ = new ListCommaIdentList();
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        listcommaidentlist_.add(x.accept(this,arg));
      }
      String ident_ = p.ident_;
      ListCommaExpList listcommaexplist_ = new ListCommaExpList();
      for (CommaExpList x : p.listcommaexplist_)
      {
        listcommaexplist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.Cal(listcommaidentlist_, ident_, listcommaexplist_);
    }    public Stm visit(soottocfg.ast.Absyn.LV p, A arg)
    {
      ListLVarStatement listlvarstatement_ = new ListLVarStatement();
      for (LVarStatement x : p.listlvarstatement_)
      {
        listlvarstatement_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.LV(listlvarstatement_);
    }    public Stm visit(soottocfg.ast.Absyn.Grd p, A arg)
    {
      GuardStm guardstm_ = p.guardstm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Grd(guardstm_);
    }    public Stm visit(soottocfg.ast.Absyn.Jmp p, A arg)
    {
      JumpStm jumpstm_ = p.jumpstm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Jmp(jumpstm_);
    }    public Stm visit(soottocfg.ast.Absyn.Iter p, A arg)
    {
      IterStm iterstm_ = p.iterstm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Iter(iterstm_);
    }    public Stm visit(soottocfg.ast.Absyn.Sel p, A arg)
    {
      SelectionStm selectionstm_ = p.selectionstm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Sel(selectionstm_);
    }    public Stm visit(soottocfg.ast.Absyn.Hps p, A arg)
    {
      HeapStm heapstm_ = p.heapstm_.accept(this, arg);
      return new soottocfg.ast.Absyn.Hps(heapstm_);
    }
/* CommaExpList */
    public CommaExpList visit(soottocfg.ast.Absyn.CommaExp p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.CommaExp(exp_);
    }
/* CommaIdentList */
    public CommaIdentList visit(soottocfg.ast.Absyn.CommaId p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.CommaId(ident_);
    }
/* GuardStm */
    public GuardStm visit(soottocfg.ast.Absyn.Asrt p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Asrt(exp_);
    }    public GuardStm visit(soottocfg.ast.Absyn.Asme p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Asme(exp_);
    }
/* JumpStm */
    public JumpStm visit(soottocfg.ast.Absyn.Glabel p, A arg)
    {
      ListCommaIdentList listcommaidentlist_ = new ListCommaIdentList();
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        listcommaidentlist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.Glabel(listcommaidentlist_);
    }    public JumpStm visit(soottocfg.ast.Absyn.Return p, A arg)
    {
      return new soottocfg.ast.Absyn.Return();
    }    public JumpStm visit(soottocfg.ast.Absyn.ReturnExp p, A arg)
    {
      ListCommaExpList listcommaexplist_ = new ListCommaExpList();
      for (CommaExpList x : p.listcommaexplist_)
      {
        listcommaexplist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.ReturnExp(listcommaexplist_);
    }
/* IterStm */
    public IterStm visit(soottocfg.ast.Absyn.While p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      Stm stm_ = p.stm_.accept(this, arg);
      return new soottocfg.ast.Absyn.While(exp_, stm_);
    }    public IterStm visit(soottocfg.ast.Absyn.Do p, A arg)
    {
      Stm stm_ = p.stm_.accept(this, arg);
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Do(stm_, exp_);
    }
/* SelectionStm */
    public SelectionStm visit(soottocfg.ast.Absyn.Ifone p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      Stm stm_ = p.stm_.accept(this, arg);
      ListElseif listelseif_ = new ListElseif();
      for (Elseif x : p.listelseif_)
      {
        listelseif_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.Ifone(exp_, stm_, listelseif_);
    }    public SelectionStm visit(soottocfg.ast.Absyn.If p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      Stm stm_1 = p.stm_1.accept(this, arg);
      ListElseif listelseif_ = new ListElseif();
      for (Elseif x : p.listelseif_)
      {
        listelseif_.add(x.accept(this,arg));
      }
      Stm stm_2 = p.stm_2.accept(this, arg);
      return new soottocfg.ast.Absyn.If(exp_, stm_1, listelseif_, stm_2);
    }
/* Elseif */
    public Elseif visit(soottocfg.ast.Absyn.EIf p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      Stm stm_ = p.stm_.accept(this, arg);
      return new soottocfg.ast.Absyn.EIf(exp_, stm_);
    }
/* HeapStm */
    public HeapStm visit(soottocfg.ast.Absyn.PullSt p, A arg)
    {
      ListCommaIdentList listcommaidentlist_ = new ListCommaIdentList();
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        listcommaidentlist_.add(x.accept(this,arg));
      }
      ListCommaExpList listcommaexplist_ = new ListCommaExpList();
      for (CommaExpList x : p.listcommaexplist_)
      {
        listcommaexplist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.PullSt(listcommaidentlist_, listcommaexplist_);
    }    public HeapStm visit(soottocfg.ast.Absyn.PushSt p, A arg)
    {
      ListCommaExpList listcommaexplist_ = new ListCommaExpList();
      for (CommaExpList x : p.listcommaexplist_)
      {
        listcommaexplist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.PushSt(listcommaexplist_);
    }    public HeapStm visit(soottocfg.ast.Absyn.HavocSt p, A arg)
    {
      ListCommaIdentList listcommaidentlist_ = new ListCommaIdentList();
      for (CommaIdentList x : p.listcommaidentlist_)
      {
        listcommaidentlist_.add(x.accept(this,arg));
      }
      return new soottocfg.ast.Absyn.HavocSt(listcommaidentlist_);
    }
/* Exp */
    public Exp visit(soottocfg.ast.Absyn.Etype p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      Type type_ = p.type_.accept(this, arg);
      return new soottocfg.ast.Absyn.Etype(exp_, type_);
    }    public Exp visit(soottocfg.ast.Absyn.Econdition p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      Exp exp_3 = p.exp_3.accept(this, arg);
      return new soottocfg.ast.Absyn.Econdition(exp_1, exp_2, exp_3);
    }    public Exp visit(soottocfg.ast.Absyn.Elor p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Elor(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eland p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eland(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ebitor p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ebitor(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ebitexor p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ebitexor(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ebitand p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ebitand(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eeq p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eeq(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eneq p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eneq(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Elthen p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Elthen(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Egrthen p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Egrthen(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ele p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ele(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ege p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ege(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eleft p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eleft(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eright p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eright(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Etrip p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Etrip(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eplus p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eplus(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Eminus p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Eminus(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Etimes p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Etimes(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Ediv p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Ediv(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Emod p, A arg)
    {
      Exp exp_1 = p.exp_1.accept(this, arg);
      Exp exp_2 = p.exp_2.accept(this, arg);
      return new soottocfg.ast.Absyn.Emod(exp_1, exp_2);
    }    public Exp visit(soottocfg.ast.Absyn.Epreop p, A arg)
    {
      Unary_operator unary_operator_ = p.unary_operator_.accept(this, arg);
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Epreop(unary_operator_, exp_);
    }    public Exp visit(soottocfg.ast.Absyn.Especname p, A arg)
    {
      SpecName specname_ = p.specname_.accept(this, arg);
      return new soottocfg.ast.Absyn.Especname(specname_);
    }    public Exp visit(soottocfg.ast.Absyn.Efld p, A arg)
    {
      TupleAcc tupleacc_ = p.tupleacc_.accept(this, arg);
      return new soottocfg.ast.Absyn.Efld(tupleacc_);
    }    public Exp visit(soottocfg.ast.Absyn.Econst p, A arg)
    {
      Constant constant_ = p.constant_.accept(this, arg);
      return new soottocfg.ast.Absyn.Econst(constant_);
    }    public Exp visit(soottocfg.ast.Absyn.Evar p, A arg)
    {
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.Evar(ident_);
    }
/* TupleAcc */
    public TupleAcc visit(soottocfg.ast.Absyn.Tplvar p, A arg)
    {
      SpecExp specexp_ = p.specexp_.accept(this, arg);
      String ident_ = p.ident_;
      return new soottocfg.ast.Absyn.Tplvar(specexp_, ident_);
    }
/* SpecExp */
    public SpecExp visit(soottocfg.ast.Absyn.Cep p, A arg)
    {
      Exp exp_ = p.exp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Cep(exp_);
    }    public SpecExp visit(soottocfg.ast.Absyn.Cnp p, A arg)
    {
      SpecExpNP specexpnp_ = p.specexpnp_.accept(this, arg);
      return new soottocfg.ast.Absyn.Cnp(specexpnp_);
    }    public SpecExp visit(soottocfg.ast.Absyn.Cthis p, A arg)
    {
      SpecName specname_ = p.specname_.accept(this, arg);
      return new soottocfg.ast.Absyn.Cthis(specname_);
    }
/* SpecExpNP */
    public SpecExpNP visit(soottocfg.ast.Absyn.CNLit p, A arg)
    {
      Constant constant_ = p.constant_.accept(this, arg);
      return new soottocfg.ast.Absyn.CNLit(constant_);
    }    public SpecExpNP visit(soottocfg.ast.Absyn.CNPfld p, A arg)
    {
      TupleAcc tupleacc_ = p.tupleacc_.accept(this, arg);
      return new soottocfg.ast.Absyn.CNPfld(tupleacc_);
    }
/* SpecName */
    public SpecName visit(soottocfg.ast.Absyn.SSnull p, A arg)
    {
      return new soottocfg.ast.Absyn.SSnull();
    }
/* Constant */
    public Constant visit(soottocfg.ast.Absyn.Edouble p, A arg)
    {
      Double double_ = p.double_;
      return new soottocfg.ast.Absyn.Edouble(double_);
    }    public Constant visit(soottocfg.ast.Absyn.Eint p, A arg)
    {
      Integer integer_ = p.integer_;
      return new soottocfg.ast.Absyn.Eint(integer_);
    }    public Constant visit(soottocfg.ast.Absyn.Etrue p, A arg)
    {
      return new soottocfg.ast.Absyn.Etrue();
    }    public Constant visit(soottocfg.ast.Absyn.Efalse p, A arg)
    {
      return new soottocfg.ast.Absyn.Efalse();
    }
/* Unary_operator */
    public Unary_operator visit(soottocfg.ast.Absyn.Plus p, A arg)
    {
      return new soottocfg.ast.Absyn.Plus();
    }    public Unary_operator visit(soottocfg.ast.Absyn.Negative p, A arg)
    {
      return new soottocfg.ast.Absyn.Negative();
    }    public Unary_operator visit(soottocfg.ast.Absyn.Complement p, A arg)
    {
      return new soottocfg.ast.Absyn.Complement();
    }    public Unary_operator visit(soottocfg.ast.Absyn.Logicalneg p, A arg)
    {
      return new soottocfg.ast.Absyn.Logicalneg();
    }
}