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
 
package apihandlers.android.net.Uri;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ParseAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ParseAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ParseAnalyzer.class);
	}

	public Object analyzeInstruction() {
		SymbolTableEntry uriStringEntry = null;
		SymbolTableEntry uriEntry = new SymbolTableEntry();
		Register destReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry inputParamEntry = localSymSpace.find(destReg.getName());

		if (inputParamEntry != null) {
			uriStringEntry = new SymbolTableEntry(inputParamEntry);
			uriStringEntry.setName("uriString");
			uriEntry.getEntryDetails().setType(ir.getReturnType()); // it should be Landroid/net/Uri or something like that.
			uriEntry.getEntryDetails().setConstant(false);
			uriEntry.getEntryDetails().setField(false);
			uriEntry.getEntryDetails().setRecord(true);
			uriEntry.setLineNumber(ir.getLineNumber());

			uriEntry.addSensitiveDataIfNeeded(uriStringEntry);
			uriEntry.setInstrInfo(ir.getInstr().getText());
			Hashtable recordFieldList = (Hashtable) uriEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList == null)
				recordFieldList = new Hashtable();
			recordFieldList.put("uriString", uriStringEntry);
			uriEntry.getEntryDetails().setRecordFieldList(recordFieldList);
		} else {
			uriEntry = new SymbolTableEntry();
			uriEntry.setName("");
			uriEntry.getEntryDetails().setType(ir.getReturnType());
			uriEntry.setLineNumber(ir.getLineNumber());
			uriEntry.setInstrInfo(ir.getInstr().getText());
		}
		logger.debug("\n Uri.ParseAnalyzer");
		return uriEntry;
	}
}
