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
 
package apihandlers.android.content.ContentResolver;

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

public class QueryAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public QueryAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(QueryAnalyzer.class);
	}

	public Object analyzeInstruction() {
		SymbolTableEntry cursorEntry = new SymbolTableEntry();
		Hashtable recordFieldList = new Hashtable();

		// We will set each field one by one into the Cursor object.
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		// Setting up first parameter (uri)
		boolean uriEntryTainted = getAndAddFieldToRecordEntry(1, "uri", recordFieldList);
		boolean projectEntryTainted = getAndAddFieldToRecordEntry(2, "projection", recordFieldList);
		boolean selectionEntryTainted = getAndAddFieldToRecordEntry(3, "selection", recordFieldList);
		boolean selectionArgsEntryTainted = getAndAddFieldToRecordEntry(4, "selectionArgs", recordFieldList);
		boolean sortOrderEntryTainted = getAndAddFieldToRecordEntry(5, "sortOrder", recordFieldList);
		boolean cancelSigEntryTainted = false;
		// For the second prototype.
		if (involvedRegisters.size() == 7) {
			// Setting up second parameter (cancellationSignal)
			Register cancellationSignalReg = ir.getInvolvedRegisters().get(6);
			SymbolTableEntry cancellationSignalEntry = localSymSpace.find(cancellationSignalReg.getName());

			if (cancellationSignalEntry != null) {
				SymbolTableEntry clonedCancellationSignalEntry = null;
				cancelSigEntryTainted = cancellationSignalEntry.getEntryDetails().isTainted();
				clonedCancellationSignalEntry = (SymbolTableEntry) cancellationSignalEntry.clone();
				recordFieldList.put("cancellationSignal", clonedCancellationSignalEntry);
			}
		}
		// Setting up the cursor object
		// cursorEntry.setName(destReg.getName()); Name will be set by 'move'
		// instruction

		SymbolTableEntry calledApiEntry = localSymSpace.find(involvedRegisters.get(0).getName());
		EntryDetails cursorEntryDetails = cursorEntry.getEntryDetails();
		cursorEntry.setLineNumber(ir.getLineNumber());
		cursorEntry.setInstrInfo("");

		if (uriEntryTainted
				|| projectEntryTainted
				|| selectionEntryTainted
				|| selectionArgsEntryTainted
				|| sortOrderEntryTainted
				|| cancelSigEntryTainted
				|| (calledApiEntry != null && calledApiEntry.getEntryDetails()
						.isTainted())){
			cursorEntryDetails.setTainted(true);
		}
		cursorEntryDetails.setConstant(false);
		cursorEntryDetails.setField(false);
		cursorEntryDetails.setType(ir.getReturnType());
		cursorEntryDetails.setRecordFieldList(recordFieldList);
		cursorEntryDetails.setRecord(true);

		cursorEntryDetails.setValue("");
		cursorEntry.setEntryDetails(cursorEntryDetails);

		logger.debug("\n ContentResolver.QueryAnalyzer");
		return cursorEntry;
	}
	
	public boolean getAndAddFieldToRecordEntry(int regNo, String fieldName, Hashtable recordFieldList){
		Register selectionArgsReg = ir.getInvolvedRegisters().get(4);
		SymbolTableEntry newEntry = localSymSpace.find(selectionArgsReg.getName());
		boolean isTainted = addFieldToRecordEntry(newEntry, fieldName, recordFieldList);
		return isTainted;
	}
	
	public boolean addFieldToRecordEntry(SymbolTableEntry fieldEntry, String fieldName, Hashtable recordFieldList){
		boolean output = false;
		if (fieldEntry != null) {
			SymbolTableEntry clonedProjectionEntry = null;
			output = fieldEntry.getEntryDetails().isTainted();
			clonedProjectionEntry = (SymbolTableEntry) fieldEntry.clone();
			recordFieldList.put(fieldName, clonedProjectionEntry);
		}
		return output;
	}
}
