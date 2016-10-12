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
 
package apihandlers.android.content.Intent;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
	}

	public Object analyzeInstruction() {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());

		if (involvedRegisters.size() == 3) {
			Register param1Reg = involvedRegisters.get(1);
			Register param2Reg = involvedRegisters.get(2);

			SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
			SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());

			if (param1Reg.getType().equalsIgnoreCase("Ljava/lang/String;")) {
				// Case#1 Intent(String action, Uri uri)
				SymbolTableEntry actionEntry = null;
				SymbolTableEntry uriEntry = null;

				if (param1Entry != null) {
					actionEntry = new SymbolTableEntry(param1Entry); //deep copy
					actionEntry.setName("actionName");
				}
				if (param2Entry != null) {
					//We actually don't need this copy. We can just add this into the symboltable.
					uriEntry = (SymbolTableEntry) param2Entry.clone(); //shallow copy
					uriEntry.setName("uri");
				}
				if (callerApiEntry != null) {
					Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
					if (recordFieldList == null)
						recordFieldList = new Hashtable();
					recordFieldList.put("actionName", actionEntry);
					if (uriEntry != null)
						recordFieldList.put("uriObject", uriEntry);
					callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				}
			}
		} else if (involvedRegisters.size() == 2) {
			Register param1Reg = involvedRegisters.get(1);
			SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());

			if (param1Reg.getType().equalsIgnoreCase("Ljava/lang/String;")) {
				SymbolTableEntry actionEntry = null;
				if (param1Entry != null) {
					actionEntry = new SymbolTableEntry(param1Entry); //deep copy
					actionEntry.setName("actionName");
					if (callerApiEntry != null) {
						callerApiEntry.addFieldDirectlyToRecordEntryByKey("actionName", actionEntry);
					}
				}
			}
		}
		logger.debug("\n intent.InitAnalyzer");
		return null;
	}
}
