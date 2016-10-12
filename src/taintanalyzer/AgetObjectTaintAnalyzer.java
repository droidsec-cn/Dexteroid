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
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;

	public AgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AgetObjectTaintAnalyzer.class);
	}

	// aget-object v3, v7, v8
	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);
		Register indexReg = ir.getInvolvedRegisters().get(2);

		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry indexEntry = localSymSpace.find(indexReg.getName());
		SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());
		Hashtable<String, String> immutableTypes = Config.getInstance().getImmutableObjects();
		EntryDetails destEntryDetails;
		if (srcEntry != null) {
			if (destEntry != null) {
				this.localSymSpace.removeEntry(destEntry.getName());
				String entryType = srcEntry.getEntryDetails().getType();
				int dimCount = 0;
				if (entryType != null && !entryType.isEmpty()) {
					dimCount = getDimensionCount(entryType);
				}
				if (dimCount > 1) { //for more than one-dimensional array
					destEntry = (SymbolTableEntry) srcEntry.clone();
				} else if (dimCount <= 1) {
					if (immutableTypes.containsKey(entryType)) {
						destEntry = new SymbolTableEntry(srcEntry);
					} else {
						destEntry = (SymbolTableEntry) srcEntry.clone();
					}
				}
				destEntryDetails = destEntry.getEntryDetails();
			}else{
				destEntry = new SymbolTableEntry();
				destEntryDetails = destEntry.getEntryDetails();
				destEntryDetails.setType(ir.getReturnType());
				destEntry.setName(destReg.getName());
				destEntry.setLineNumber(ir.getLineNumber());
				destEntry.setInstrInfo("");
				destEntryDetails.setValue("");
				destEntryDetails.setConstant(false);
			}
			destEntry.addSensitiveDataIfNeeded(indexEntry);
			destEntryDetails.setField(false);
			destEntryDetails.setRecord(false);
			destEntry.setEntryDetails(destEntryDetails);
			localSymSpace.addEntry(destEntry);
		} 
		logger.debug("\n AgetTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}

	int getDimensionCount(String type) {
		int count = 0;
		char[] typeArr = type.toCharArray();
		for (char c : typeArr) {
			if (c == '[') {
				count++;
			}
		}
		return count++;
	}
}
