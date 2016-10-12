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

public class InvokeVirtualDefaultTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	ArrayList<Register> involvedRegisters;
	SymbolTableEntry returnEntry;

	public InvokeVirtualDefaultTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		involvedRegisters = new ArrayList<Register>();
		returnEntry = new SymbolTableEntry();
		logger = Logger.getLogger(InvokeVirtualDefaultTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		boolean tainted = false;
		involvedRegisters = ir.getInvolvedRegisters();
		String returnType = ir.getReturnType();
		if (involvedRegisters.size() > 0) {
			Register callereReg = involvedRegisters.get(0);
			SymbolTableEntry callerEntry = this.localSymSpace.find(callereReg.getName());
			if (callerEntry != null) {
				EntryDetails callerEntryDetails = callerEntry.getEntryDetails();

				if (returnType.trim().equalsIgnoreCase("V")) {
					ArrayList<SourceInfo> srcInfoList = callerEntryDetails.getSourceInfoList();
					for (int i = 1; i < involvedRegisters.size(); i++) {
						Register reg = involvedRegisters.get(i);
						SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
						callerEntry.addSensitiveDataIfNeeded(entry);
					}
					callerEntry.setEntryDetails(callerEntryDetails);
					logger.debug("\n InvokeDirectDefaultTaintAnalyzer");
					localSymSpace.logInfoSymbolSpace();
					return null;
				} else {
					// CASE#2
					SymbolTableEntry destEntry = new SymbolTableEntry();
					EntryDetails destEntryDetails = destEntry.getEntryDetails();
					for (int i = 0; i < involvedRegisters.size(); i++) {
						Register reg = involvedRegisters.get(i);
						SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
						destEntry.addSensitiveDataIfNeeded(entry);
						callerEntry.addSensitiveDataIfNeeded(entry);
					}
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setInstrInfo(ir.getInstr().getText());
					destEntryDetails.setType(returnType);
					
					callerEntry.setEntryDetails(callerEntryDetails);
					destEntry.setEntryDetails(destEntryDetails);
					return destEntry;
				}
			}
		}
		return null;
	}
}
