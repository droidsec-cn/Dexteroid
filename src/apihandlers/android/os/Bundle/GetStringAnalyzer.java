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

public class GetStringAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetStringAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(GetStringAnalyzer.class);
	}

	public Object analyzeInstruction() {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register bundleReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		SymbolTableEntry bundleEntry = this.localSymSpace.find(bundleReg.getName());

		if (bundleEntry != null) {
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			if (keyEntry != null) {
				String keyValue = keyEntry.getEntryDetails().getValue();
				Hashtable recordFieldList = bundleEntry.getEntryDetails().getRecordFieldList();

				if (recordFieldList != null && recordFieldList.containsKey(keyValue)) {
					SymbolTableEntry valueEntry = (SymbolTableEntry) recordFieldList.get(keyValue);
					if (valueEntry != null) {
						SymbolTableEntry returnEntry = new SymbolTableEntry(valueEntry);
						return returnEntry;
					}
				}
			}
		}
		logger.debug("\n Bundle.getString()");
		return null;
	}
}
