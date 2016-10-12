/*
*	This file is part of Dexteroid.
* 
*	Copyright (C) 2016, Mohsin Junaid <mohsinjuni at gmail dot com>
*	All rights reserved.
* 
*	Dexteroid is free software: you can redistribute it and/or modify
*	it under the terms of the GNU Affero General Public License as published
*	by the Free Software Foundation, either version 3 of the License, or
*	(at your option) any later version.
* 
*	Dexteroid is distributed in the hope that it will be useful,
*	but WITHOUT ANY WARRANTY; without even the implied warranty of
*	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*	GNU Affero General Public License for more details.
* 
*	You should have received a copy of the GNU Affero General Public License
*	along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
 
package taintanalyzer.instranalyzers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;

public class ReturnTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	TaintAnalyzer ta;

	// 0x00 return-void ,0x1c return-object v0, 0x23 return v0	
	public ReturnTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		logger = Logger.getLogger(ReturnTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		String instrType = ir.getInstr().getTypeBySyntax();
		logger.debug(ir.getInstr().getText());

		if (instrType.equalsIgnoreCase("return-void")) {
			SymbolTableEntry returnEntry = null;
			if (ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("<init>")
					|| ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("init")
					|| ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("<clinit>")) {
				returnEntry = getRecord(ir.getInstr().getCurrPkgClassName());
			}
			return returnEntry;
		} else {
			Register destReg = ir.getInvolvedRegisters().get(0);
			SymbolTableEntry entry = localSymSpace.find(destReg.getName());
			if (entry != null) {
				return entry;
			} else
				return null;
		}
	}

	private SymbolTableEntry getRecord(String currPkgClsName) {
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		Stack entries = this.localSymSpace.getEntries();
		returnEntry.getEntryDetails().setType(currPkgClsName);

		for (int i = entries.size() - 1; i >= 0; i--) {
			Hashtable ht = (Hashtable) entries.get(i);
			if (ht != null) {
				Enumeration<String> enumKey = ht.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry ent = (SymbolTableEntry) ht.get(key);

					if (ent != null && ent.getEntryDetails().getType() != null) {
						if (ent.getEntryDetails().getType().equalsIgnoreCase(currPkgClsName)) {
							returnEntry = (SymbolTableEntry) ent.clone(); // We can probably just return it as it is. We may not need to clone it either. 
							logger.debug(" stack items count after <init> methodCall " + entries.size());
							return returnEntry;
						}
					}
				}
			}
		}
		logger.debug(" stack size after <init> methodCall " + entries.size());
		return returnEntry;
	}

}
