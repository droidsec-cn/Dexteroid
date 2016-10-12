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
 
package apihandlers.android.graphics.PointF;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
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
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
	}

	public Object analyzeInstruction() {
		ArrayList<Register> allRegs = ir.getInvolvedRegisters();
		Register destReg = allRegs.get(0); //v2
		SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());

		if (allRegs.size() == 3) {
			Register xReg = allRegs.get(1); //v0
			Register yReg = allRegs.get(2); //v1
			SymbolTableEntry xEntry = localSymSpace.find(xReg.getName());
			SymbolTableEntry yEntry = localSymSpace.find(yReg.getName());
			boolean isNewEntry = false;
			if (destEntry == null) {
				destEntry = new SymbolTableEntry();
				isNewEntry = true;
			}
			EntryDetails details = destEntry.getEntryDetails();
			Hashtable recordFieldList = (Hashtable) details.getRecordFieldList();
			if (recordFieldList == null) {
				recordFieldList = new Hashtable();
			}
			if (xEntry != null) {
				SymbolTableEntry x1Entry = new SymbolTableEntry(xEntry);
				x1Entry.setName("x");
				recordFieldList.put(x1Entry.getName(), x1Entry);
			}
			if (yEntry != null) {
				SymbolTableEntry y1Entry = new SymbolTableEntry(yEntry);
				y1Entry.setName("y");
				recordFieldList.put(y1Entry.getName(), y1Entry);
			}
			details.setRecordFieldList(recordFieldList);
			destEntry.setEntryDetails(details);
			
			if (isNewEntry) {
				this.localSymSpace.addEntry(destEntry);
			}
		} else if (allRegs.size() == 1) {
			if (destEntry == null) {
				destEntry = new SymbolTableEntry();
				EntryDetails details = destEntry.getEntryDetails();
				details.setType(destReg.getType());
				destEntry.setName(destReg.getName());
				destEntry.setEntryDetails(details);
				this.localSymSpace.addEntry(destEntry);
			}
		}
		logger.debug("\n PointF -> InitAnalyzer");
		return null;
	}
}
