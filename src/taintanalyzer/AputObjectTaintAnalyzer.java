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
import java.util.Random;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AputObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	
	public AputObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AputObjectTaintAnalyzer.class);
	}
	
	//Comment: I am going to mark the whole array as tainted if any of the index or the src entry is tainted. I will also mark it as record= false onwards.
	public Object analyzeInstruction() {
		Register srcReg = ir.getInvolvedRegisters().get(0);
		Register destReg = ir.getInvolvedRegisters().get(1);
		Register indexReg = ir.getInvolvedRegisters().get(2);
		SymbolTableEntry srcLocalEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry destLocalEntry = localSymSpace.find(destReg.getName());
		SymbolTableEntry indexLocalEntry = localSymSpace.find(indexReg.getName());
		Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
		if (srcLocalEntry != null) {
			boolean isNewEntry = false;
			if (destLocalEntry == null) {
				destLocalEntry = new SymbolTableEntry();
				isNewEntry = true;
			}
			EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();
			destEntryDetails.setValue(" ");
			destLocalEntry.setLineNumber(ir.getLineNumber());
			destLocalEntry.addSensitiveDataIfNeeded(indexLocalEntry);
			destLocalEntry.addSensitiveDataIfNeeded(srcLocalEntry);
			destEntryDetails.setField(false);
			destEntryDetails.setRecord(false);
			destLocalEntry.setEntryDetails(destEntryDetails);
			if(isNewEntry){
				localSymSpace.addEntry(destLocalEntry);
			}
		}
		logger.debug("\n AputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
