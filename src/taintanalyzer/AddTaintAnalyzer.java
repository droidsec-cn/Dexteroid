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

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AddTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	
	public AddTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AddTaintAnalyzer.class);
	}
	
	// 	0x1c add-int v3, v7, v2
	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0); // v3 
		Register srcReg = ir.getInvolvedRegisters().get(1); // v7
		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());
		if (destReg.getName().equals(srcReg.getName())) {
			//Do nothing case.
			// add-int/... v3 v3 1 
		} else {
			// add-int/... v3 v7 1 . Create a new entry and store result into it.

			SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());
			EntryDetails destEntryDetails;
			boolean isNewEntry = false;
			if (destEntry == null) {
				isNewEntry = true;
				destEntry = new SymbolTableEntry();
				destEntryDetails = destEntry.getEntryDetails();
				if (srcEntry != null) {
					if (srcEntry.getEntryDetails().getType() != null)
						destEntryDetails.setType(srcEntry.getEntryDetails().getType());
					else
						destEntryDetails.setType(destReg.getType());
					destEntryDetails.setConstant(srcEntry.getEntryDetails().isConstant());

				} else {
					destEntryDetails.setType(destReg.getType());
					destEntryDetails.setConstant(false);
				}
			}
			destEntryDetails = destEntry.getEntryDetails();
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setInstrInfo("");
			destEntry.addSensitiveDataIfNeeded(srcEntry);
			destEntryDetails.setField(false);
			destEntryDetails.setRecord(false);
			destEntryDetails.setValue("");
			destEntry.setEntryDetails(destEntryDetails);
			if(isNewEntry){
				localSymSpace.addEntry(destEntry); //This automatically replaces the existing entry.
			}
		}
		logger.debug("\n AddTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
