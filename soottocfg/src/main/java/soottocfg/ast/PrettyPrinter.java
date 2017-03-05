package soottocfg.ast;
import soottocfg.ast.Absyn.*;

public class PrettyPrinter
{
  //For certain applications increasing the initial size of the buffer may improve performance.
  private static final int INITIAL_BUFFER_SIZE = 128;
  private static final int INDENT_WIDTH = 2;
  //You may wish to change the parentheses used in precedence.
  private static final String _L_PAREN = new String("(");
  private static final String _R_PAREN = new String(")");
  //You may wish to change render
  private static void render(String s)
  {
    if (s.equals("{"))
    {
       buf_.append("\n");
       indent();
       buf_.append(s);
       _n_ = _n_ + INDENT_WIDTH;
       buf_.append("\n");
       indent();
    }
    else if (s.equals("(") || s.equals("["))
       buf_.append(s);
    else if (s.equals(")") || s.equals("]"))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals("}"))
    {
       int t;
       _n_ = _n_ - INDENT_WIDTH;
       for(t=0; t<INDENT_WIDTH; t++) {
         backup();
       }
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals(","))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals(";"))
    {
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals("")) return;
    else
    {
       buf_.append(s);
       buf_.append(" ");
    }
  }


  //  print and show methods are defined for each category.
  public static String print(soottocfg.ast.Absyn.ProgramFile foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ProgramFile foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Type foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Type foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.BasicType foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.BasicType foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Decl foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Decl foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.DeclBody foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.DeclBody foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListDecl foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListDecl foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.TupleEntry foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.TupleEntry foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListTupleEntry foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListTupleEntry foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.TypeList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.TypeList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Types foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Types foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListTypes foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListTypes foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.FieldDeclaration foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.FieldDeclaration foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListFieldDeclaration foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListFieldDeclaration foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.VarDecl foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.VarDecl foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListVarDecl foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListVarDecl foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.DeclaratorName foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.DeclaratorName foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.MethodDecl foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.MethodDecl foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Parameter foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Parameter foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListParameter foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListParameter foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.MethodBody foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.MethodBody foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Body foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Body foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.LVarStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.LVarStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListLVarStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListLVarStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Stm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Stm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.CommaExpList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.CommaExpList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListCommaExpList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListCommaExpList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.CommaIdentList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.CommaIdentList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListCommaIdentList foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListCommaIdentList foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.GuardStm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.GuardStm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.JumpStm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.JumpStm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.IterStm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.IterStm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.SelectionStm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.SelectionStm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Elseif foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Elseif foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.ListElseif foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.ListElseif foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.HeapStm foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.HeapStm foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Exp foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Exp foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.TupleAcc foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.TupleAcc foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.SpecExp foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.SpecExp foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.SpecExpNP foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.SpecExpNP foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.SpecName foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.SpecName foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Constant foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Constant foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(soottocfg.ast.Absyn.Unary_operator foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(soottocfg.ast.Absyn.Unary_operator foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(soottocfg.ast.Absyn.ProgramFile foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.JhPrg)
    {
       soottocfg.ast.Absyn.JhPrg _jhprg = (soottocfg.ast.Absyn.JhPrg) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_jhprg.listdecl_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Type foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.BuiltIn)
    {
       soottocfg.ast.Absyn.BuiltIn _builtin = (soottocfg.ast.Absyn.BuiltIn) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_builtin.basictype_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.ClassType)
    {
       soottocfg.ast.Absyn.ClassType _classtype = (soottocfg.ast.Absyn.ClassType) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_classtype.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.BasicType foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Tint)
    {
       soottocfg.ast.Absyn.Tint _tint = (soottocfg.ast.Absyn.Tint) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("int");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Tlong)
    {
       soottocfg.ast.Absyn.Tlong _tlong = (soottocfg.ast.Absyn.Tlong) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("long");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Tfloat)
    {
       soottocfg.ast.Absyn.Tfloat _tfloat = (soottocfg.ast.Absyn.Tfloat) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("float");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Tdouble)
    {
       soottocfg.ast.Absyn.Tdouble _tdouble = (soottocfg.ast.Absyn.Tdouble) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("double");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.TVoid)
    {
       soottocfg.ast.Absyn.TVoid _tvoid = (soottocfg.ast.Absyn.TVoid) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("void");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Tboolean)
    {
       soottocfg.ast.Absyn.Tboolean _tboolean = (soottocfg.ast.Absyn.Tboolean) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("boolean");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Decl foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.TDecl)
    {
       soottocfg.ast.Absyn.TDecl _tdecl = (soottocfg.ast.Absyn.TDecl) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("class");
       pp(_tdecl.ident_, 0);
       pp(_tdecl.declbody_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.TDecl2)
    {
       soottocfg.ast.Absyn.TDecl2 _tdecl2 = (soottocfg.ast.Absyn.TDecl2) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("class");
       pp(_tdecl2.ident_1, 0);
       render("extends");
       pp(_tdecl2.ident_2, 0);
       pp(_tdecl2.declbody_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.MDecl)
    {
       soottocfg.ast.Absyn.MDecl _mdecl = (soottocfg.ast.Absyn.MDecl) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_mdecl.typelist_, 0);
       pp(_mdecl.methoddecl_, 0);
       pp(_mdecl.methodbody_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.DeclBody foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.TDeclBody)
    {
       soottocfg.ast.Absyn.TDeclBody _tdeclbody = (soottocfg.ast.Absyn.TDeclBody) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("{");
       pp(_tdeclbody.listfielddeclaration_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.TDeclBody2)
    {
       soottocfg.ast.Absyn.TDeclBody2 _tdeclbody2 = (soottocfg.ast.Absyn.TDeclBody2) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("<");
       pp(_tdeclbody2.listtupleentry_, 0);
       render(">");
       render("{");
       pp(_tdeclbody2.listfielddeclaration_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListDecl foo, int _i_)
  {
     for (java.util.Iterator<Decl> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.TupleEntry foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.NamedTpl)
    {
       soottocfg.ast.Absyn.NamedTpl _namedtpl = (soottocfg.ast.Absyn.NamedTpl) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_namedtpl.ident_, 0);
       render(":");
       pp(_namedtpl.type_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.UNamedTpl)
    {
       soottocfg.ast.Absyn.UNamedTpl _unamedtpl = (soottocfg.ast.Absyn.UNamedTpl) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_unamedtpl.ident_, 0);
       render(":");
       render("unique");
       pp(_unamedtpl.type_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListTupleEntry foo, int _i_)
  {
     for (java.util.Iterator<TupleEntry> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.TypeList foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.TList1)
    {
       soottocfg.ast.Absyn.TList1 _tlist1 = (soottocfg.ast.Absyn.TList1) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_tlist1.type_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.TList2)
    {
       soottocfg.ast.Absyn.TList2 _tlist2 = (soottocfg.ast.Absyn.TList2) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("<");
       pp(_tlist2.listtypes_, 0);
       render(">");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Types foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.TNames)
    {
       soottocfg.ast.Absyn.TNames _tnames = (soottocfg.ast.Absyn.TNames) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_tnames.type_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListTypes foo, int _i_)
  {
     for (java.util.Iterator<Types> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.FieldDeclaration foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Dvar)
    {
       soottocfg.ast.Absyn.Dvar _dvar = (soottocfg.ast.Absyn.Dvar) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_dvar.type_, 0);
       pp(_dvar.listvardecl_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.UDvar)
    {
       soottocfg.ast.Absyn.UDvar _udvar = (soottocfg.ast.Absyn.UDvar) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("unique");
       pp(_udvar.type_, 0);
       pp(_udvar.listvardecl_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListFieldDeclaration foo, int _i_)
  {
     for (java.util.Iterator<FieldDeclaration> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.VarDecl foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.VDecl)
    {
       soottocfg.ast.Absyn.VDecl _vdecl = (soottocfg.ast.Absyn.VDecl) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_vdecl.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListVarDecl foo, int _i_)
  {
     for (java.util.Iterator<VarDecl> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.DeclaratorName foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.DeclName)
    {
       soottocfg.ast.Absyn.DeclName _declname = (soottocfg.ast.Absyn.DeclName) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_declname.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.MethodDecl foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Mth)
    {
       soottocfg.ast.Absyn.Mth _mth = (soottocfg.ast.Absyn.Mth) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_mth.declaratorname_, 0);
       render("(");
       pp(_mth.listparameter_, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Mth2)
    {
       soottocfg.ast.Absyn.Mth2 _mth2 = (soottocfg.ast.Absyn.Mth2) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_mth2.declaratorname_, 0);
       render("(");
       pp(_mth2.listparameter_, 0);
       render(")");
       render("implements");
       pp(_mth2.string_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Parameter foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Param)
    {
       soottocfg.ast.Absyn.Param _param = (soottocfg.ast.Absyn.Param) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_param.type_, 0);
       pp(_param.declaratorname_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Pfinal)
    {
       soottocfg.ast.Absyn.Pfinal _pfinal = (soottocfg.ast.Absyn.Pfinal) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("final");
       pp(_pfinal.type_, 0);
       pp(_pfinal.declaratorname_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListParameter foo, int _i_)
  {
     for (java.util.Iterator<Parameter> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.MethodBody foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.IBody)
    {
       soottocfg.ast.Absyn.IBody _ibody = (soottocfg.ast.Absyn.IBody) foo;
       if (_i_ > 0) render(_L_PAREN);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.MBody)
    {
       soottocfg.ast.Absyn.MBody _mbody = (soottocfg.ast.Absyn.MBody) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_mbody.body_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Body foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.XBody)
    {
       soottocfg.ast.Absyn.XBody _xbody = (soottocfg.ast.Absyn.XBody) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("{");
       pp(_xbody.listlvarstatement_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.LVarStatement foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.LVar)
    {
       soottocfg.ast.Absyn.LVar _lvar = (soottocfg.ast.Absyn.LVar) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_lvar.type_, 0);
       pp(_lvar.listvardecl_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.LVarf)
    {
       soottocfg.ast.Absyn.LVarf _lvarf = (soottocfg.ast.Absyn.LVarf) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("final");
       pp(_lvarf.type_, 0);
       pp(_lvarf.listvardecl_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Statem)
    {
       soottocfg.ast.Absyn.Statem _statem = (soottocfg.ast.Absyn.Statem) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_statem.stm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListLVarStatement foo, int _i_)
  {
     for (java.util.Iterator<LVarStatement> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.Stm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Lbl)
    {
       soottocfg.ast.Absyn.Lbl _lbl = (soottocfg.ast.Absyn.Lbl) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_lbl.ident_, 0);
       render(":");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Asg)
    {
       soottocfg.ast.Absyn.Asg _asg = (soottocfg.ast.Absyn.Asg) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_asg.ident_, 0);
       render("=");
       pp(_asg.exp_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.NewSt)
    {
       soottocfg.ast.Absyn.NewSt _newst = (soottocfg.ast.Absyn.NewSt) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_newst.ident_, 0);
       render("=");
       render("new");
       pp(_newst.type_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Cal)
    {
       soottocfg.ast.Absyn.Cal _cal = (soottocfg.ast.Absyn.Cal) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_cal.listcommaidentlist_, 0);
       render("=");
       pp(_cal.ident_, 0);
       render("(");
       pp(_cal.listcommaexplist_, 0);
       render(")");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.LV)
    {
       soottocfg.ast.Absyn.LV _lv = (soottocfg.ast.Absyn.LV) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("{");
       pp(_lv.listlvarstatement_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Grd)
    {
       soottocfg.ast.Absyn.Grd _grd = (soottocfg.ast.Absyn.Grd) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_grd.guardstm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Jmp)
    {
       soottocfg.ast.Absyn.Jmp _jmp = (soottocfg.ast.Absyn.Jmp) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_jmp.jumpstm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Iter)
    {
       soottocfg.ast.Absyn.Iter _iter = (soottocfg.ast.Absyn.Iter) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_iter.iterstm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Sel)
    {
       soottocfg.ast.Absyn.Sel _sel = (soottocfg.ast.Absyn.Sel) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_sel.selectionstm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Hps)
    {
       soottocfg.ast.Absyn.Hps _hps = (soottocfg.ast.Absyn.Hps) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_hps.heapstm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.CommaExpList foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.CommaExp)
    {
       soottocfg.ast.Absyn.CommaExp _commaexp = (soottocfg.ast.Absyn.CommaExp) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_commaexp.exp_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListCommaExpList foo, int _i_)
  {
     for (java.util.Iterator<CommaExpList> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.CommaIdentList foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.CommaId)
    {
       soottocfg.ast.Absyn.CommaId _commaid = (soottocfg.ast.Absyn.CommaId) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_commaid.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListCommaIdentList foo, int _i_)
  {
     for (java.util.Iterator<CommaIdentList> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.GuardStm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Asrt)
    {
       soottocfg.ast.Absyn.Asrt _asrt = (soottocfg.ast.Absyn.Asrt) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("assert");
       render("(");
       pp(_asrt.exp_, 0);
       render(")");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Asme)
    {
       soottocfg.ast.Absyn.Asme _asme = (soottocfg.ast.Absyn.Asme) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("assume");
       render("(");
       pp(_asme.exp_, 0);
       render(")");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.JumpStm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Glabel)
    {
       soottocfg.ast.Absyn.Glabel _glabel = (soottocfg.ast.Absyn.Glabel) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("goto");
       pp(_glabel.listcommaidentlist_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Return)
    {
       soottocfg.ast.Absyn.Return _return = (soottocfg.ast.Absyn.Return) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("return");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.ReturnExp)
    {
       soottocfg.ast.Absyn.ReturnExp _returnexp = (soottocfg.ast.Absyn.ReturnExp) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("return");
       pp(_returnexp.listcommaexplist_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.IterStm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.While)
    {
       soottocfg.ast.Absyn.While _while = (soottocfg.ast.Absyn.While) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("while");
       render("(");
       pp(_while.exp_, 0);
       render(")");
       pp(_while.stm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Do)
    {
       soottocfg.ast.Absyn.Do _do = (soottocfg.ast.Absyn.Do) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("do");
       pp(_do.stm_, 0);
       render("while");
       render("(");
       pp(_do.exp_, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.SelectionStm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Ifone)
    {
       soottocfg.ast.Absyn.Ifone _ifone = (soottocfg.ast.Absyn.Ifone) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("if");
       render("(");
       pp(_ifone.exp_, 0);
       render(")");
       pp(_ifone.stm_, 0);
       pp(_ifone.listelseif_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.If)
    {
       soottocfg.ast.Absyn.If _if = (soottocfg.ast.Absyn.If) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("if");
       render("(");
       pp(_if.exp_, 0);
       render(")");
       pp(_if.stm_1, 0);
       pp(_if.listelseif_, 0);
       render("else");
       pp(_if.stm_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Elseif foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.EIf)
    {
       soottocfg.ast.Absyn.EIf _eif = (soottocfg.ast.Absyn.EIf) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("else");
       render("if");
       render("(");
       pp(_eif.exp_, 0);
       render(")");
       pp(_eif.stm_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.ListElseif foo, int _i_)
  {
     for (java.util.Iterator<Elseif> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), _i_);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }  }

  private static void pp(soottocfg.ast.Absyn.HeapStm foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.PullSt)
    {
       soottocfg.ast.Absyn.PullSt _pullst = (soottocfg.ast.Absyn.PullSt) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_pullst.listcommaidentlist_, 0);
       render("=");
       render("pull");
       render("(");
       pp(_pullst.listcommaexplist_, 0);
       render(")");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.PushSt)
    {
       soottocfg.ast.Absyn.PushSt _pushst = (soottocfg.ast.Absyn.PushSt) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("push");
       render("(");
       pp(_pushst.listcommaexplist_, 0);
       render(")");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.HavocSt)
    {
       soottocfg.ast.Absyn.HavocSt _havocst = (soottocfg.ast.Absyn.HavocSt) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("havoc");
       pp(_havocst.listcommaidentlist_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Exp foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Etype)
    {
       soottocfg.ast.Absyn.Etype _etype = (soottocfg.ast.Absyn.Etype) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_etype.exp_, 14);
       render("instanceof");
       pp(_etype.type_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Econdition)
    {
       soottocfg.ast.Absyn.Econdition _econdition = (soottocfg.ast.Absyn.Econdition) foo;
       if (_i_ > 2) render(_L_PAREN);
       pp(_econdition.exp_1, 3);
       render("?");
       pp(_econdition.exp_2, 0);
       render(":");
       pp(_econdition.exp_3, 2);
       if (_i_ > 2) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Elor)
    {
       soottocfg.ast.Absyn.Elor _elor = (soottocfg.ast.Absyn.Elor) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_elor.exp_1, 3);
       render("||");
       pp(_elor.exp_2, 4);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eland)
    {
       soottocfg.ast.Absyn.Eland _eland = (soottocfg.ast.Absyn.Eland) foo;
       if (_i_ > 4) render(_L_PAREN);
       pp(_eland.exp_1, 4);
       render("&&");
       pp(_eland.exp_2, 5);
       if (_i_ > 4) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ebitor)
    {
       soottocfg.ast.Absyn.Ebitor _ebitor = (soottocfg.ast.Absyn.Ebitor) foo;
       if (_i_ > 5) render(_L_PAREN);
       pp(_ebitor.exp_1, 5);
       render("|");
       pp(_ebitor.exp_2, 6);
       if (_i_ > 5) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ebitexor)
    {
       soottocfg.ast.Absyn.Ebitexor _ebitexor = (soottocfg.ast.Absyn.Ebitexor) foo;
       if (_i_ > 6) render(_L_PAREN);
       pp(_ebitexor.exp_1, 6);
       render("^");
       pp(_ebitexor.exp_2, 7);
       if (_i_ > 6) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ebitand)
    {
       soottocfg.ast.Absyn.Ebitand _ebitand = (soottocfg.ast.Absyn.Ebitand) foo;
       if (_i_ > 7) render(_L_PAREN);
       pp(_ebitand.exp_1, 7);
       render("&");
       pp(_ebitand.exp_2, 8);
       if (_i_ > 7) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eeq)
    {
       soottocfg.ast.Absyn.Eeq _eeq = (soottocfg.ast.Absyn.Eeq) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_eeq.exp_1, 8);
       render("==");
       pp(_eeq.exp_2, 9);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eneq)
    {
       soottocfg.ast.Absyn.Eneq _eneq = (soottocfg.ast.Absyn.Eneq) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_eneq.exp_1, 8);
       render("!=");
       pp(_eneq.exp_2, 9);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Elthen)
    {
       soottocfg.ast.Absyn.Elthen _elthen = (soottocfg.ast.Absyn.Elthen) foo;
       if (_i_ > 9) render(_L_PAREN);
       pp(_elthen.exp_1, 9);
       render("<");
       pp(_elthen.exp_2, 10);
       if (_i_ > 9) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Egrthen)
    {
       soottocfg.ast.Absyn.Egrthen _egrthen = (soottocfg.ast.Absyn.Egrthen) foo;
       if (_i_ > 9) render(_L_PAREN);
       pp(_egrthen.exp_1, 9);
       render(">");
       pp(_egrthen.exp_2, 10);
       if (_i_ > 9) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ele)
    {
       soottocfg.ast.Absyn.Ele _ele = (soottocfg.ast.Absyn.Ele) foo;
       if (_i_ > 9) render(_L_PAREN);
       pp(_ele.exp_1, 9);
       render("<=");
       pp(_ele.exp_2, 10);
       if (_i_ > 9) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ege)
    {
       soottocfg.ast.Absyn.Ege _ege = (soottocfg.ast.Absyn.Ege) foo;
       if (_i_ > 9) render(_L_PAREN);
       pp(_ege.exp_1, 9);
       render(">=");
       pp(_ege.exp_2, 10);
       if (_i_ > 9) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eleft)
    {
       soottocfg.ast.Absyn.Eleft _eleft = (soottocfg.ast.Absyn.Eleft) foo;
       if (_i_ > 10) render(_L_PAREN);
       pp(_eleft.exp_1, 10);
       render("<<");
       pp(_eleft.exp_2, 11);
       if (_i_ > 10) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eright)
    {
       soottocfg.ast.Absyn.Eright _eright = (soottocfg.ast.Absyn.Eright) foo;
       if (_i_ > 10) render(_L_PAREN);
       pp(_eright.exp_1, 10);
       render(">>");
       pp(_eright.exp_2, 11);
       if (_i_ > 10) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Etrip)
    {
       soottocfg.ast.Absyn.Etrip _etrip = (soottocfg.ast.Absyn.Etrip) foo;
       if (_i_ > 10) render(_L_PAREN);
       pp(_etrip.exp_1, 10);
       render(">>>");
       pp(_etrip.exp_2, 11);
       if (_i_ > 10) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eplus)
    {
       soottocfg.ast.Absyn.Eplus _eplus = (soottocfg.ast.Absyn.Eplus) foo;
       if (_i_ > 11) render(_L_PAREN);
       pp(_eplus.exp_1, 11);
       render("+");
       pp(_eplus.exp_2, 12);
       if (_i_ > 11) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eminus)
    {
       soottocfg.ast.Absyn.Eminus _eminus = (soottocfg.ast.Absyn.Eminus) foo;
       if (_i_ > 11) render(_L_PAREN);
       pp(_eminus.exp_1, 11);
       render("-");
       pp(_eminus.exp_2, 12);
       if (_i_ > 11) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Etimes)
    {
       soottocfg.ast.Absyn.Etimes _etimes = (soottocfg.ast.Absyn.Etimes) foo;
       if (_i_ > 12) render(_L_PAREN);
       pp(_etimes.exp_1, 12);
       render("*");
       pp(_etimes.exp_2, 13);
       if (_i_ > 12) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Ediv)
    {
       soottocfg.ast.Absyn.Ediv _ediv = (soottocfg.ast.Absyn.Ediv) foo;
       if (_i_ > 12) render(_L_PAREN);
       pp(_ediv.exp_1, 12);
       render("/");
       pp(_ediv.exp_2, 13);
       if (_i_ > 12) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Emod)
    {
       soottocfg.ast.Absyn.Emod _emod = (soottocfg.ast.Absyn.Emod) foo;
       if (_i_ > 12) render(_L_PAREN);
       pp(_emod.exp_1, 12);
       render("%");
       pp(_emod.exp_2, 13);
       if (_i_ > 12) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Epreop)
    {
       soottocfg.ast.Absyn.Epreop _epreop = (soottocfg.ast.Absyn.Epreop) foo;
       if (_i_ > 14) render(_L_PAREN);
       pp(_epreop.unary_operator_, 0);
       pp(_epreop.exp_, 15);
       if (_i_ > 14) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Especname)
    {
       soottocfg.ast.Absyn.Especname _especname = (soottocfg.ast.Absyn.Especname) foo;
       if (_i_ > 15) render(_L_PAREN);
       pp(_especname.specname_, 0);
       if (_i_ > 15) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Efld)
    {
       soottocfg.ast.Absyn.Efld _efld = (soottocfg.ast.Absyn.Efld) foo;
       if (_i_ > 15) render(_L_PAREN);
       pp(_efld.tupleacc_, 0);
       if (_i_ > 15) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Econst)
    {
       soottocfg.ast.Absyn.Econst _econst = (soottocfg.ast.Absyn.Econst) foo;
       if (_i_ > 15) render(_L_PAREN);
       pp(_econst.constant_, 0);
       if (_i_ > 15) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Evar)
    {
       soottocfg.ast.Absyn.Evar _evar = (soottocfg.ast.Absyn.Evar) foo;
       if (_i_ > 16) render(_L_PAREN);
       pp(_evar.ident_, 0);
       if (_i_ > 16) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.TupleAcc foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Tplvar)
    {
       soottocfg.ast.Absyn.Tplvar _tplvar = (soottocfg.ast.Absyn.Tplvar) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_tplvar.specexp_, 0);
       render(".");
       pp(_tplvar.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.SpecExp foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Cep)
    {
       soottocfg.ast.Absyn.Cep _cep = (soottocfg.ast.Absyn.Cep) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("(");
       pp(_cep.exp_, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Cnp)
    {
       soottocfg.ast.Absyn.Cnp _cnp = (soottocfg.ast.Absyn.Cnp) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_cnp.specexpnp_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Cthis)
    {
       soottocfg.ast.Absyn.Cthis _cthis = (soottocfg.ast.Absyn.Cthis) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_cthis.specname_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.SpecExpNP foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.CNLit)
    {
       soottocfg.ast.Absyn.CNLit _cnlit = (soottocfg.ast.Absyn.CNLit) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_cnlit.constant_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.CNPfld)
    {
       soottocfg.ast.Absyn.CNPfld _cnpfld = (soottocfg.ast.Absyn.CNPfld) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_cnpfld.tupleacc_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.SpecName foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.SSnull)
    {
       soottocfg.ast.Absyn.SSnull _ssnull = (soottocfg.ast.Absyn.SSnull) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("null");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Constant foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Edouble)
    {
       soottocfg.ast.Absyn.Edouble _edouble = (soottocfg.ast.Absyn.Edouble) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_edouble.double_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Eint)
    {
       soottocfg.ast.Absyn.Eint _eint = (soottocfg.ast.Absyn.Eint) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_eint.integer_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Etrue)
    {
       soottocfg.ast.Absyn.Etrue _etrue = (soottocfg.ast.Absyn.Etrue) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("true");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Efalse)
    {
       soottocfg.ast.Absyn.Efalse _efalse = (soottocfg.ast.Absyn.Efalse) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("false");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(soottocfg.ast.Absyn.Unary_operator foo, int _i_)
  {
    if (foo instanceof soottocfg.ast.Absyn.Plus)
    {
       soottocfg.ast.Absyn.Plus _plus = (soottocfg.ast.Absyn.Plus) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("+");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Negative)
    {
       soottocfg.ast.Absyn.Negative _negative = (soottocfg.ast.Absyn.Negative) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("-");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Complement)
    {
       soottocfg.ast.Absyn.Complement _complement = (soottocfg.ast.Absyn.Complement) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("~");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof soottocfg.ast.Absyn.Logicalneg)
    {
       soottocfg.ast.Absyn.Logicalneg _logicalneg = (soottocfg.ast.Absyn.Logicalneg) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("!");
       if (_i_ > 0) render(_R_PAREN);
    }
  }


  private static void sh(soottocfg.ast.Absyn.ProgramFile foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.JhPrg)
    {
       soottocfg.ast.Absyn.JhPrg _jhprg = (soottocfg.ast.Absyn.JhPrg) foo;
       render("(");
       render("JhPrg");
       render("[");
       sh(_jhprg.listdecl_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Type foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.BuiltIn)
    {
       soottocfg.ast.Absyn.BuiltIn _builtin = (soottocfg.ast.Absyn.BuiltIn) foo;
       render("(");
       render("BuiltIn");
       sh(_builtin.basictype_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.ClassType)
    {
       soottocfg.ast.Absyn.ClassType _classtype = (soottocfg.ast.Absyn.ClassType) foo;
       render("(");
       render("ClassType");
       sh(_classtype.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.BasicType foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Tint)
    {
       soottocfg.ast.Absyn.Tint _tint = (soottocfg.ast.Absyn.Tint) foo;
       render("Tint");
    }
    if (foo instanceof soottocfg.ast.Absyn.Tlong)
    {
       soottocfg.ast.Absyn.Tlong _tlong = (soottocfg.ast.Absyn.Tlong) foo;
       render("Tlong");
    }
    if (foo instanceof soottocfg.ast.Absyn.Tfloat)
    {
       soottocfg.ast.Absyn.Tfloat _tfloat = (soottocfg.ast.Absyn.Tfloat) foo;
       render("Tfloat");
    }
    if (foo instanceof soottocfg.ast.Absyn.Tdouble)
    {
       soottocfg.ast.Absyn.Tdouble _tdouble = (soottocfg.ast.Absyn.Tdouble) foo;
       render("Tdouble");
    }
    if (foo instanceof soottocfg.ast.Absyn.TVoid)
    {
       soottocfg.ast.Absyn.TVoid _tvoid = (soottocfg.ast.Absyn.TVoid) foo;
       render("TVoid");
    }
    if (foo instanceof soottocfg.ast.Absyn.Tboolean)
    {
       soottocfg.ast.Absyn.Tboolean _tboolean = (soottocfg.ast.Absyn.Tboolean) foo;
       render("Tboolean");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Decl foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.TDecl)
    {
       soottocfg.ast.Absyn.TDecl _tdecl = (soottocfg.ast.Absyn.TDecl) foo;
       render("(");
       render("TDecl");
       sh(_tdecl.ident_);
       sh(_tdecl.declbody_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.TDecl2)
    {
       soottocfg.ast.Absyn.TDecl2 _tdecl2 = (soottocfg.ast.Absyn.TDecl2) foo;
       render("(");
       render("TDecl2");
       sh(_tdecl2.ident_1);
       sh(_tdecl2.ident_2);
       sh(_tdecl2.declbody_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.MDecl)
    {
       soottocfg.ast.Absyn.MDecl _mdecl = (soottocfg.ast.Absyn.MDecl) foo;
       render("(");
       render("MDecl");
       sh(_mdecl.typelist_);
       sh(_mdecl.methoddecl_);
       sh(_mdecl.methodbody_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.DeclBody foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.TDeclBody)
    {
       soottocfg.ast.Absyn.TDeclBody _tdeclbody = (soottocfg.ast.Absyn.TDeclBody) foo;
       render("(");
       render("TDeclBody");
       render("[");
       sh(_tdeclbody.listfielddeclaration_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.TDeclBody2)
    {
       soottocfg.ast.Absyn.TDeclBody2 _tdeclbody2 = (soottocfg.ast.Absyn.TDeclBody2) foo;
       render("(");
       render("TDeclBody2");
       render("[");
       sh(_tdeclbody2.listtupleentry_);
       render("]");
       render("[");
       sh(_tdeclbody2.listfielddeclaration_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListDecl foo)
  {
     for (java.util.Iterator<Decl> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.TupleEntry foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.NamedTpl)
    {
       soottocfg.ast.Absyn.NamedTpl _namedtpl = (soottocfg.ast.Absyn.NamedTpl) foo;
       render("(");
       render("NamedTpl");
       sh(_namedtpl.ident_);
       sh(_namedtpl.type_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.UNamedTpl)
    {
       soottocfg.ast.Absyn.UNamedTpl _unamedtpl = (soottocfg.ast.Absyn.UNamedTpl) foo;
       render("(");
       render("UNamedTpl");
       sh(_unamedtpl.ident_);
       sh(_unamedtpl.type_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListTupleEntry foo)
  {
     for (java.util.Iterator<TupleEntry> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.TypeList foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.TList1)
    {
       soottocfg.ast.Absyn.TList1 _tlist1 = (soottocfg.ast.Absyn.TList1) foo;
       render("(");
       render("TList1");
       sh(_tlist1.type_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.TList2)
    {
       soottocfg.ast.Absyn.TList2 _tlist2 = (soottocfg.ast.Absyn.TList2) foo;
       render("(");
       render("TList2");
       render("[");
       sh(_tlist2.listtypes_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Types foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.TNames)
    {
       soottocfg.ast.Absyn.TNames _tnames = (soottocfg.ast.Absyn.TNames) foo;
       render("(");
       render("TNames");
       sh(_tnames.type_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListTypes foo)
  {
     for (java.util.Iterator<Types> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.FieldDeclaration foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Dvar)
    {
       soottocfg.ast.Absyn.Dvar _dvar = (soottocfg.ast.Absyn.Dvar) foo;
       render("(");
       render("Dvar");
       sh(_dvar.type_);
       render("[");
       sh(_dvar.listvardecl_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.UDvar)
    {
       soottocfg.ast.Absyn.UDvar _udvar = (soottocfg.ast.Absyn.UDvar) foo;
       render("(");
       render("UDvar");
       sh(_udvar.type_);
       render("[");
       sh(_udvar.listvardecl_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListFieldDeclaration foo)
  {
     for (java.util.Iterator<FieldDeclaration> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.VarDecl foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.VDecl)
    {
       soottocfg.ast.Absyn.VDecl _vdecl = (soottocfg.ast.Absyn.VDecl) foo;
       render("(");
       render("VDecl");
       sh(_vdecl.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListVarDecl foo)
  {
     for (java.util.Iterator<VarDecl> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.DeclaratorName foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.DeclName)
    {
       soottocfg.ast.Absyn.DeclName _declname = (soottocfg.ast.Absyn.DeclName) foo;
       render("(");
       render("DeclName");
       sh(_declname.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.MethodDecl foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Mth)
    {
       soottocfg.ast.Absyn.Mth _mth = (soottocfg.ast.Absyn.Mth) foo;
       render("(");
       render("Mth");
       sh(_mth.declaratorname_);
       render("[");
       sh(_mth.listparameter_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Mth2)
    {
       soottocfg.ast.Absyn.Mth2 _mth2 = (soottocfg.ast.Absyn.Mth2) foo;
       render("(");
       render("Mth2");
       sh(_mth2.declaratorname_);
       render("[");
       sh(_mth2.listparameter_);
       render("]");
       sh(_mth2.string_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Parameter foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Param)
    {
       soottocfg.ast.Absyn.Param _param = (soottocfg.ast.Absyn.Param) foo;
       render("(");
       render("Param");
       sh(_param.type_);
       sh(_param.declaratorname_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Pfinal)
    {
       soottocfg.ast.Absyn.Pfinal _pfinal = (soottocfg.ast.Absyn.Pfinal) foo;
       render("(");
       render("Pfinal");
       sh(_pfinal.type_);
       sh(_pfinal.declaratorname_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListParameter foo)
  {
     for (java.util.Iterator<Parameter> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.MethodBody foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.IBody)
    {
       soottocfg.ast.Absyn.IBody _ibody = (soottocfg.ast.Absyn.IBody) foo;
       render("IBody");
    }
    if (foo instanceof soottocfg.ast.Absyn.MBody)
    {
       soottocfg.ast.Absyn.MBody _mbody = (soottocfg.ast.Absyn.MBody) foo;
       render("(");
       render("MBody");
       sh(_mbody.body_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Body foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.XBody)
    {
       soottocfg.ast.Absyn.XBody _xbody = (soottocfg.ast.Absyn.XBody) foo;
       render("(");
       render("XBody");
       render("[");
       sh(_xbody.listlvarstatement_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.LVarStatement foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.LVar)
    {
       soottocfg.ast.Absyn.LVar _lvar = (soottocfg.ast.Absyn.LVar) foo;
       render("(");
       render("LVar");
       sh(_lvar.type_);
       render("[");
       sh(_lvar.listvardecl_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.LVarf)
    {
       soottocfg.ast.Absyn.LVarf _lvarf = (soottocfg.ast.Absyn.LVarf) foo;
       render("(");
       render("LVarf");
       sh(_lvarf.type_);
       render("[");
       sh(_lvarf.listvardecl_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Statem)
    {
       soottocfg.ast.Absyn.Statem _statem = (soottocfg.ast.Absyn.Statem) foo;
       render("(");
       render("Statem");
       sh(_statem.stm_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListLVarStatement foo)
  {
     for (java.util.Iterator<LVarStatement> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.Stm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Lbl)
    {
       soottocfg.ast.Absyn.Lbl _lbl = (soottocfg.ast.Absyn.Lbl) foo;
       render("(");
       render("Lbl");
       sh(_lbl.ident_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Asg)
    {
       soottocfg.ast.Absyn.Asg _asg = (soottocfg.ast.Absyn.Asg) foo;
       render("(");
       render("Asg");
       sh(_asg.ident_);
       sh(_asg.exp_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.NewSt)
    {
       soottocfg.ast.Absyn.NewSt _newst = (soottocfg.ast.Absyn.NewSt) foo;
       render("(");
       render("NewSt");
       sh(_newst.ident_);
       sh(_newst.type_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Cal)
    {
       soottocfg.ast.Absyn.Cal _cal = (soottocfg.ast.Absyn.Cal) foo;
       render("(");
       render("Cal");
       render("[");
       sh(_cal.listcommaidentlist_);
       render("]");
       sh(_cal.ident_);
       render("[");
       sh(_cal.listcommaexplist_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.LV)
    {
       soottocfg.ast.Absyn.LV _lv = (soottocfg.ast.Absyn.LV) foo;
       render("(");
       render("LV");
       render("[");
       sh(_lv.listlvarstatement_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Grd)
    {
       soottocfg.ast.Absyn.Grd _grd = (soottocfg.ast.Absyn.Grd) foo;
       render("(");
       render("Grd");
       sh(_grd.guardstm_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Jmp)
    {
       soottocfg.ast.Absyn.Jmp _jmp = (soottocfg.ast.Absyn.Jmp) foo;
       render("(");
       render("Jmp");
       sh(_jmp.jumpstm_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Iter)
    {
       soottocfg.ast.Absyn.Iter _iter = (soottocfg.ast.Absyn.Iter) foo;
       render("(");
       render("Iter");
       sh(_iter.iterstm_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Sel)
    {
       soottocfg.ast.Absyn.Sel _sel = (soottocfg.ast.Absyn.Sel) foo;
       render("(");
       render("Sel");
       sh(_sel.selectionstm_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Hps)
    {
       soottocfg.ast.Absyn.Hps _hps = (soottocfg.ast.Absyn.Hps) foo;
       render("(");
       render("Hps");
       sh(_hps.heapstm_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.CommaExpList foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.CommaExp)
    {
       soottocfg.ast.Absyn.CommaExp _commaexp = (soottocfg.ast.Absyn.CommaExp) foo;
       render("(");
       render("CommaExp");
       sh(_commaexp.exp_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListCommaExpList foo)
  {
     for (java.util.Iterator<CommaExpList> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.CommaIdentList foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.CommaId)
    {
       soottocfg.ast.Absyn.CommaId _commaid = (soottocfg.ast.Absyn.CommaId) foo;
       render("(");
       render("CommaId");
       sh(_commaid.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListCommaIdentList foo)
  {
     for (java.util.Iterator<CommaIdentList> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.GuardStm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Asrt)
    {
       soottocfg.ast.Absyn.Asrt _asrt = (soottocfg.ast.Absyn.Asrt) foo;
       render("(");
       render("Asrt");
       sh(_asrt.exp_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Asme)
    {
       soottocfg.ast.Absyn.Asme _asme = (soottocfg.ast.Absyn.Asme) foo;
       render("(");
       render("Asme");
       sh(_asme.exp_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.JumpStm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Glabel)
    {
       soottocfg.ast.Absyn.Glabel _glabel = (soottocfg.ast.Absyn.Glabel) foo;
       render("(");
       render("Glabel");
       render("[");
       sh(_glabel.listcommaidentlist_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Return)
    {
       soottocfg.ast.Absyn.Return _return = (soottocfg.ast.Absyn.Return) foo;
       render("Return");
    }
    if (foo instanceof soottocfg.ast.Absyn.ReturnExp)
    {
       soottocfg.ast.Absyn.ReturnExp _returnexp = (soottocfg.ast.Absyn.ReturnExp) foo;
       render("(");
       render("ReturnExp");
       render("[");
       sh(_returnexp.listcommaexplist_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.IterStm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.While)
    {
       soottocfg.ast.Absyn.While _while = (soottocfg.ast.Absyn.While) foo;
       render("(");
       render("While");
       sh(_while.exp_);
       sh(_while.stm_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Do)
    {
       soottocfg.ast.Absyn.Do _do = (soottocfg.ast.Absyn.Do) foo;
       render("(");
       render("Do");
       sh(_do.stm_);
       sh(_do.exp_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.SelectionStm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Ifone)
    {
       soottocfg.ast.Absyn.Ifone _ifone = (soottocfg.ast.Absyn.Ifone) foo;
       render("(");
       render("Ifone");
       sh(_ifone.exp_);
       sh(_ifone.stm_);
       render("[");
       sh(_ifone.listelseif_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.If)
    {
       soottocfg.ast.Absyn.If _if = (soottocfg.ast.Absyn.If) foo;
       render("(");
       render("If");
       sh(_if.exp_);
       sh(_if.stm_1);
       render("[");
       sh(_if.listelseif_);
       render("]");
       sh(_if.stm_2);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Elseif foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.EIf)
    {
       soottocfg.ast.Absyn.EIf _eif = (soottocfg.ast.Absyn.EIf) foo;
       render("(");
       render("EIf");
       sh(_eif.exp_);
       sh(_eif.stm_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.ListElseif foo)
  {
     for (java.util.Iterator<Elseif> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(soottocfg.ast.Absyn.HeapStm foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.PullSt)
    {
       soottocfg.ast.Absyn.PullSt _pullst = (soottocfg.ast.Absyn.PullSt) foo;
       render("(");
       render("PullSt");
       render("[");
       sh(_pullst.listcommaidentlist_);
       render("]");
       render("[");
       sh(_pullst.listcommaexplist_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.PushSt)
    {
       soottocfg.ast.Absyn.PushSt _pushst = (soottocfg.ast.Absyn.PushSt) foo;
       render("(");
       render("PushSt");
       render("[");
       sh(_pushst.listcommaexplist_);
       render("]");
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.HavocSt)
    {
       soottocfg.ast.Absyn.HavocSt _havocst = (soottocfg.ast.Absyn.HavocSt) foo;
       render("(");
       render("HavocSt");
       render("[");
       sh(_havocst.listcommaidentlist_);
       render("]");
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Exp foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Etype)
    {
       soottocfg.ast.Absyn.Etype _etype = (soottocfg.ast.Absyn.Etype) foo;
       render("(");
       render("Etype");
       sh(_etype.exp_);
       sh(_etype.type_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Econdition)
    {
       soottocfg.ast.Absyn.Econdition _econdition = (soottocfg.ast.Absyn.Econdition) foo;
       render("(");
       render("Econdition");
       sh(_econdition.exp_1);
       sh(_econdition.exp_2);
       sh(_econdition.exp_3);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Elor)
    {
       soottocfg.ast.Absyn.Elor _elor = (soottocfg.ast.Absyn.Elor) foo;
       render("(");
       render("Elor");
       sh(_elor.exp_1);
       sh(_elor.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eland)
    {
       soottocfg.ast.Absyn.Eland _eland = (soottocfg.ast.Absyn.Eland) foo;
       render("(");
       render("Eland");
       sh(_eland.exp_1);
       sh(_eland.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ebitor)
    {
       soottocfg.ast.Absyn.Ebitor _ebitor = (soottocfg.ast.Absyn.Ebitor) foo;
       render("(");
       render("Ebitor");
       sh(_ebitor.exp_1);
       sh(_ebitor.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ebitexor)
    {
       soottocfg.ast.Absyn.Ebitexor _ebitexor = (soottocfg.ast.Absyn.Ebitexor) foo;
       render("(");
       render("Ebitexor");
       sh(_ebitexor.exp_1);
       sh(_ebitexor.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ebitand)
    {
       soottocfg.ast.Absyn.Ebitand _ebitand = (soottocfg.ast.Absyn.Ebitand) foo;
       render("(");
       render("Ebitand");
       sh(_ebitand.exp_1);
       sh(_ebitand.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eeq)
    {
       soottocfg.ast.Absyn.Eeq _eeq = (soottocfg.ast.Absyn.Eeq) foo;
       render("(");
       render("Eeq");
       sh(_eeq.exp_1);
       sh(_eeq.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eneq)
    {
       soottocfg.ast.Absyn.Eneq _eneq = (soottocfg.ast.Absyn.Eneq) foo;
       render("(");
       render("Eneq");
       sh(_eneq.exp_1);
       sh(_eneq.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Elthen)
    {
       soottocfg.ast.Absyn.Elthen _elthen = (soottocfg.ast.Absyn.Elthen) foo;
       render("(");
       render("Elthen");
       sh(_elthen.exp_1);
       sh(_elthen.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Egrthen)
    {
       soottocfg.ast.Absyn.Egrthen _egrthen = (soottocfg.ast.Absyn.Egrthen) foo;
       render("(");
       render("Egrthen");
       sh(_egrthen.exp_1);
       sh(_egrthen.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ele)
    {
       soottocfg.ast.Absyn.Ele _ele = (soottocfg.ast.Absyn.Ele) foo;
       render("(");
       render("Ele");
       sh(_ele.exp_1);
       sh(_ele.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ege)
    {
       soottocfg.ast.Absyn.Ege _ege = (soottocfg.ast.Absyn.Ege) foo;
       render("(");
       render("Ege");
       sh(_ege.exp_1);
       sh(_ege.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eleft)
    {
       soottocfg.ast.Absyn.Eleft _eleft = (soottocfg.ast.Absyn.Eleft) foo;
       render("(");
       render("Eleft");
       sh(_eleft.exp_1);
       sh(_eleft.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eright)
    {
       soottocfg.ast.Absyn.Eright _eright = (soottocfg.ast.Absyn.Eright) foo;
       render("(");
       render("Eright");
       sh(_eright.exp_1);
       sh(_eright.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Etrip)
    {
       soottocfg.ast.Absyn.Etrip _etrip = (soottocfg.ast.Absyn.Etrip) foo;
       render("(");
       render("Etrip");
       sh(_etrip.exp_1);
       sh(_etrip.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eplus)
    {
       soottocfg.ast.Absyn.Eplus _eplus = (soottocfg.ast.Absyn.Eplus) foo;
       render("(");
       render("Eplus");
       sh(_eplus.exp_1);
       sh(_eplus.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eminus)
    {
       soottocfg.ast.Absyn.Eminus _eminus = (soottocfg.ast.Absyn.Eminus) foo;
       render("(");
       render("Eminus");
       sh(_eminus.exp_1);
       sh(_eminus.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Etimes)
    {
       soottocfg.ast.Absyn.Etimes _etimes = (soottocfg.ast.Absyn.Etimes) foo;
       render("(");
       render("Etimes");
       sh(_etimes.exp_1);
       sh(_etimes.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Ediv)
    {
       soottocfg.ast.Absyn.Ediv _ediv = (soottocfg.ast.Absyn.Ediv) foo;
       render("(");
       render("Ediv");
       sh(_ediv.exp_1);
       sh(_ediv.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Emod)
    {
       soottocfg.ast.Absyn.Emod _emod = (soottocfg.ast.Absyn.Emod) foo;
       render("(");
       render("Emod");
       sh(_emod.exp_1);
       sh(_emod.exp_2);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Epreop)
    {
       soottocfg.ast.Absyn.Epreop _epreop = (soottocfg.ast.Absyn.Epreop) foo;
       render("(");
       render("Epreop");
       sh(_epreop.unary_operator_);
       sh(_epreop.exp_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Especname)
    {
       soottocfg.ast.Absyn.Especname _especname = (soottocfg.ast.Absyn.Especname) foo;
       render("(");
       render("Especname");
       sh(_especname.specname_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Efld)
    {
       soottocfg.ast.Absyn.Efld _efld = (soottocfg.ast.Absyn.Efld) foo;
       render("(");
       render("Efld");
       sh(_efld.tupleacc_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Econst)
    {
       soottocfg.ast.Absyn.Econst _econst = (soottocfg.ast.Absyn.Econst) foo;
       render("(");
       render("Econst");
       sh(_econst.constant_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Evar)
    {
       soottocfg.ast.Absyn.Evar _evar = (soottocfg.ast.Absyn.Evar) foo;
       render("(");
       render("Evar");
       sh(_evar.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.TupleAcc foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Tplvar)
    {
       soottocfg.ast.Absyn.Tplvar _tplvar = (soottocfg.ast.Absyn.Tplvar) foo;
       render("(");
       render("Tplvar");
       sh(_tplvar.specexp_);
       sh(_tplvar.ident_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.SpecExp foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Cep)
    {
       soottocfg.ast.Absyn.Cep _cep = (soottocfg.ast.Absyn.Cep) foo;
       render("(");
       render("Cep");
       sh(_cep.exp_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Cnp)
    {
       soottocfg.ast.Absyn.Cnp _cnp = (soottocfg.ast.Absyn.Cnp) foo;
       render("(");
       render("Cnp");
       sh(_cnp.specexpnp_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Cthis)
    {
       soottocfg.ast.Absyn.Cthis _cthis = (soottocfg.ast.Absyn.Cthis) foo;
       render("(");
       render("Cthis");
       sh(_cthis.specname_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.SpecExpNP foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.CNLit)
    {
       soottocfg.ast.Absyn.CNLit _cnlit = (soottocfg.ast.Absyn.CNLit) foo;
       render("(");
       render("CNLit");
       sh(_cnlit.constant_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.CNPfld)
    {
       soottocfg.ast.Absyn.CNPfld _cnpfld = (soottocfg.ast.Absyn.CNPfld) foo;
       render("(");
       render("CNPfld");
       sh(_cnpfld.tupleacc_);
       render(")");
    }
  }

  private static void sh(soottocfg.ast.Absyn.SpecName foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.SSnull)
    {
       soottocfg.ast.Absyn.SSnull _ssnull = (soottocfg.ast.Absyn.SSnull) foo;
       render("SSnull");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Constant foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Edouble)
    {
       soottocfg.ast.Absyn.Edouble _edouble = (soottocfg.ast.Absyn.Edouble) foo;
       render("(");
       render("Edouble");
       sh(_edouble.double_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Eint)
    {
       soottocfg.ast.Absyn.Eint _eint = (soottocfg.ast.Absyn.Eint) foo;
       render("(");
       render("Eint");
       sh(_eint.integer_);
       render(")");
    }
    if (foo instanceof soottocfg.ast.Absyn.Etrue)
    {
       soottocfg.ast.Absyn.Etrue _etrue = (soottocfg.ast.Absyn.Etrue) foo;
       render("Etrue");
    }
    if (foo instanceof soottocfg.ast.Absyn.Efalse)
    {
       soottocfg.ast.Absyn.Efalse _efalse = (soottocfg.ast.Absyn.Efalse) foo;
       render("Efalse");
    }
  }

  private static void sh(soottocfg.ast.Absyn.Unary_operator foo)
  {
    if (foo instanceof soottocfg.ast.Absyn.Plus)
    {
       soottocfg.ast.Absyn.Plus _plus = (soottocfg.ast.Absyn.Plus) foo;
       render("Plus");
    }
    if (foo instanceof soottocfg.ast.Absyn.Negative)
    {
       soottocfg.ast.Absyn.Negative _negative = (soottocfg.ast.Absyn.Negative) foo;
       render("Negative");
    }
    if (foo instanceof soottocfg.ast.Absyn.Complement)
    {
       soottocfg.ast.Absyn.Complement _complement = (soottocfg.ast.Absyn.Complement) foo;
       render("Complement");
    }
    if (foo instanceof soottocfg.ast.Absyn.Logicalneg)
    {
       soottocfg.ast.Absyn.Logicalneg _logicalneg = (soottocfg.ast.Absyn.Logicalneg) foo;
       render("Logicalneg");
    }
  }


  private static void pp(Integer n, int _i_) { buf_.append(n); buf_.append(" "); }
  private static void pp(Double d, int _i_) { buf_.append(d); buf_.append(" "); }
  private static void pp(String s, int _i_) { buf_.append(s); buf_.append(" "); }
  private static void pp(Character c, int _i_) { buf_.append("'" + c.toString() + "'"); buf_.append(" "); }
  private static void sh(Integer n) { render(n.toString()); }
  private static void sh(Double d) { render(d.toString()); }
  private static void sh(Character c) { render(c.toString()); }
  private static void sh(String s) { printQuoted(s); }
  private static void printQuoted(String s) { render("\"" + s + "\""); }
  private static void indent()
  {
    int n = _n_;
    while (n > 0)
    {
      buf_.append(" ");
      n--;
    }
  }
  private static void backup()
  {
     if (buf_.charAt(buf_.length() - 1) == ' ') {
      buf_.setLength(buf_.length() - 1);
    }
  }
  private static void trim()
  {
     while (buf_.length() > 0 && buf_.charAt(0) == ' ')
        buf_.deleteCharAt(0); 
    while (buf_.length() > 0 && buf_.charAt(buf_.length()-1) == ' ')
        buf_.deleteCharAt(buf_.length()-1);
  }
  private static int _n_ = 0;
  private static StringBuilder buf_ = new StringBuilder(INITIAL_BUFFER_SIZE);
}

