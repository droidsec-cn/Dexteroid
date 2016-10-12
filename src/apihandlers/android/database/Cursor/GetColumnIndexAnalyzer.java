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
 
package apihandlers.android.database.Cursor;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetColumnIndexAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetColumnIndexAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(GetColumnIndexAnalyzer.class);
	}

	public Object analyzeInstruction() {
		String cursorRegName = ir.getInvolvedRegisters().get(0).getName();
		String inputParamRegName = ir.getInvolvedRegisters().get(1).getName();
		SymbolTableEntry cursorEntry = localSymSpace.find(cursorRegName);
		SymbolTableEntry inputParamEntry = localSymSpace.find(inputParamRegName);

		String columnName = "";
		if (inputParamEntry != null) {
			columnName = inputParamEntry.getEntryDetails().getValue();
		}
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();

		String value = columnName;
		returnEntryDetails.setValue(value); // TODO need to update it.
		returnEntry.setInstrInfo("");

		returnEntry.addSensitiveDataIfNeeded(cursorEntry);
		returnEntryDetails.setConstant(false);
		returnEntryDetails.setRecord(false);
		returnEntryDetails.setField(false);
		returnEntry.setEntryDetails(returnEntryDetails);

		logger.debug("\n Cursor.GetColumnIndexAnalyzer");
		return returnEntry;
	}
}
