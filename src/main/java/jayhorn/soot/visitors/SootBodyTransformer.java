/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaeaeaeaeaeaeaeaeaef and Stephan Arlt
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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;

/**
 * Boogie Body Transformer
 * 
 * @author schaef
 */
public class SootBodyTransformer extends BodyTransformer {

	public SootBodyTransformer() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void internalTransform(Body arg0, String arg1, Map arg2) {		
		try {
			transformStmtList(arg0);
		} catch (Throwable e) {
			throw e;
		}
	}

	/**
	 * Transforms a list of statements
	 * 
	 * @param body
	 *            Body
	 */
	private void transformStmtList(Body body) {
						
		ExceptionalUnitGraph tug = new ExceptionalUnitGraph(
				body.getMethod().getActiveBody(), UnitThrowAnalysis.v());

		Iterator<Unit> stmtIt = tug.iterator();
		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();
			
			SootStmtSwitch bss = new SootStmtSwitch();
			s.apply(bss);			
		}
	}
	
}
