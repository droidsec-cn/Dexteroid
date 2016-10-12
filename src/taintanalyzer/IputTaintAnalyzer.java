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

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.Context;
import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IputTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	
	public IputTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IputTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register srcReg = ir.getInvolvedRegisters().get(0);
		Register destReg = ir.getInvolvedRegisters().get(1);

		SymbolTableEntry srcLocalEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry destLocalEntry = localSymSpace.find(destReg.getName());
		String objectName = ir.getMethodOrObjectName();

		SymbolTableEntry field = null; //= new SymbolTableEntry();
		if (srcLocalEntry != null) {
			boolean isNewEntry = false;
			if (destLocalEntry == null) {
				destLocalEntry = new SymbolTableEntry();
				destLocalEntry.setName(destReg.getName());
				destLocalEntry.setLineNumber(ir.getLineNumber());
				isNewEntry = true;
			}
			// Making a deep copy for copy-by-value items
			//Since we are putting a new object, it will just replace the existing item if there is any.
			field = new SymbolTableEntry(srcLocalEntry);
			field.setName(objectName);
			field.setInstrInfo(this.ir.getInstr().getText());
			field.setLineNumber(ir.getLineNumber());
			field.getEntryDetails().setType(ir.getReturnType());

			EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();
			destEntryDetails.setRecord(true);
			destEntryDetails.setType(ir.getCallerAPIName());
			destLocalEntry.addFieldDirectlyToRecordEntryByKey(field.getName(), field);
			destLocalEntry.setEntryDetails(destEntryDetails);
			if(isNewEntry){
				this.localSymSpace.addEntry(destLocalEntry);
			}
		}
		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		logger.debug("\n IputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
