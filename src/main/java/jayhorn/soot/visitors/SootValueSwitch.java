/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaef and Stephan Arlt
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package jayhorn.soot.visitors;

import jayhorn.solver.ProverExpr;
import soot.Local;
import soot.SootMethod;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.tagkit.Tag;

/**
 * @author schaef
 */
public class SootValueSwitch implements JimpleValueSwitch {

	
	public ProverExpr getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invokeExpr(InvokeExpr arg0) {
		SootMethod m = arg0.getMethod();
		if (m.isJavaLibraryMethod()) {
			for (Tag t : m.getTags()) {
				System.out.println(t.getClass().toString() + ": \t"+t);
			}
		}

	}
	
	
	@Override
	public void caseClassConstant(ClassConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseDoubleConstant(DoubleConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseFloatConstant(FloatConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseIntConstant(IntConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseLongConstant(LongConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseMethodHandle(MethodHandle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNullConstant(NullConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseStringConstant(StringConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultCase(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseAddExpr(AddExpr arg0) {		
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseAndExpr(AndExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
		
	}

	@Override
	public void caseCastExpr(CastExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseCmpExpr(CmpExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
		
	}

	@Override
	public void caseCmpgExpr(CmpgExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
		
	}

	@Override
	public void caseCmplExpr(CmplExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
		
	}

	@Override
	public void caseDivExpr(DivExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
		
	}

	@Override
	public void caseDynamicInvokeExpr(DynamicInvokeExpr arg0) {
		// TODO Auto-generated method stub
		invokeExpr(arg0);
		
	}

	@Override
	public void caseEqExpr(EqExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);

	}

	@Override
	public void caseGeExpr(GeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);

	}

	@Override
	public void caseGtExpr(GtExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);

	}

	@Override
	public void caseInstanceOfExpr(InstanceOfExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
		// TODO Auto-generated method stub
		invokeExpr(arg0);
		
	}

	@Override
	public void caseLeExpr(LeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);

	}

	@Override
	public void caseLengthExpr(LengthExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseLtExpr(LtExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);

	}

	@Override
	public void caseMulExpr(MulExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNeExpr(NeExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNegExpr(NegExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNewArrayExpr(NewArrayExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNewExpr(NewExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNewMultiArrayExpr(NewMultiArrayExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseOrExpr(OrExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseRemExpr(RemExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseShlExpr(ShlExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseShrExpr(ShrExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		// TODO Auto-generated method stub
		invokeExpr(arg0);
		
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		// TODO Auto-generated method stub
		invokeExpr(arg0);
		
	}

	@Override
	public void caseSubExpr(SubExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseUshrExpr(UshrExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		// TODO Auto-generated method stub
		invokeExpr(arg0);
	}

	@Override
	public void caseXorExpr(XorExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseArrayRef(ArrayRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseCaughtExceptionRef(CaughtExceptionRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseInstanceFieldRef(InstanceFieldRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseParameterRef(ParameterRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseStaticFieldRef(StaticFieldRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseThisRef(ThisRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseLocal(Local arg0) {
		// TODO Auto-generated method stub
		
	}

}
