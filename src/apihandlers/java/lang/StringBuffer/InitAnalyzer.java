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
 
package apihandlers.java.lang.StringBuffer;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
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
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
	}

	public Object analyzeInstruction() {
		int regCount = ir.getInvolvedRegisters().size();
		if (regCount == 2) {
			Register reg1 = ir.getInvolvedRegisters().get(0); //v0
			Register reg2 = ir.getInvolvedRegisters().get(1); //v1
			SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
			SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());

			if (callerEntry != null) {
				callerEntry.setLineNumber(ir.getLineNumber());
				callerEntry.addSensitiveDataIfNeeded(inputParamEntry);
				callerEntry.getEntryDetails().setField(false);
				callerEntry.getEntryDetails().setRecord(false);
			}
		} else if (regCount == 1) {
			Register reg1 = ir.getInvolvedRegisters().get(0); //v1
			SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());

			boolean isNewEntry = false;
			if (reg1Entry == null) {
				reg1Entry = new SymbolTableEntry();
				isNewEntry = true;
			}
			EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
			reg1EntryDetails.setType("Ljava/lang/StringBuffer;");
			reg1Entry.setLineNumber(ir.getLineNumber());
			reg1EntryDetails.setConstant(false);
			reg1EntryDetails.setTainted(false);
			reg1Entry.setInstrInfo(ir.getInstr().getText());
			reg1EntryDetails.setField(false);
			reg1EntryDetails.setRecord(false);
			reg1EntryDetails.setValue(" ");
			reg1Entry.setEntryDetails(reg1EntryDetails);

			if (isNewEntry) {
				this.localSymSpace.addEntry(reg1Entry);
			}
		}
		logger.debug("\n <InitAnalyzer>");
		return null;
	}
}
