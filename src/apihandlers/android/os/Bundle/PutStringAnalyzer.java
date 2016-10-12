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
 
package apihandlers.android.os.Bundle;

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

public class PutStringAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutStringAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(PutStringAnalyzer.class);
	}

	public Object analyzeInstruction() {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register bundleReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);

		SymbolTableEntry bundleEntry = this.localSymSpace.find(bundleReg.getName());
		if (bundleEntry != null) {
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			if (keyEntry != null && valueEntry != null) {
				SymbolTableEntry fieldEntry = new SymbolTableEntry(valueEntry); //deep copy
				bundleEntry.addFieldDirectlyToRecordEntryByKey(keyEntry.getEntryDetails().getValue().trim(), fieldEntry);
			}
		}
		logger.debug("\n Bundle.putString()");
		return null;
	}
}
