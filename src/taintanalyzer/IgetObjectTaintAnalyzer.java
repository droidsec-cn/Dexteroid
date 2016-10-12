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

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

import symboltable.Context;
import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;

public class IgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;

	public IgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IgetObjectTaintAnalyzer.class);

	}

	// 	0x2a iget-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;	
	//  0xe iget-object v2, v6, Lcom/myexample/motivatingexample/MainActivity;->message Ljava/lang/String;	
	public Object analyzeInstruction() {
		String instrText = ir.getInstr().getText();
		logger.debug(ir.getInstr().getText());

		Register destReg = ir.getInvolvedRegisters().get(0); //v0
		Register srcReg = ir.getInvolvedRegisters().get(1); //v3
		String objectName = ir.getMethodOrObjectName();
		SymbolTableEntry srcLocalEntry = localSymSpace.find(srcReg.getName(), objectName);
		SymbolTableEntry destLocalEntry;
		if (srcLocalEntry != null) {
			destLocalEntry = (SymbolTableEntry) srcLocalEntry.clone();
			destLocalEntry.getEntryDetails().setType(srcLocalEntry.getEntryDetails().getType());
		} else {
			//Create a dummy entry
			destLocalEntry = new SymbolTableEntry();
			EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();
			
			destEntryDetails.setType(destReg.getType()); // Lcom/test/maliciousactivity/User;	
			destEntryDetails.setValue(" ");
			destEntryDetails.setTainted(false);
			destEntryDetails.setConstant(false);
			destEntryDetails.setRecord(false);
			destLocalEntry.setEntryDetails(destEntryDetails);
		}
		destLocalEntry.setName(destReg.getName());
		destLocalEntry.setLineNumber(ir.getLineNumber());
		destLocalEntry.setInstrInfo(this.ir.getInstr().getText());
		this.localSymSpace.addEntry(destLocalEntry);
		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		logger.debug("\n <<<<<<<<<IgetTaintAnalyzer>>>>>>>>>>>>");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}

}
