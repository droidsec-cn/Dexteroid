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
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import apihandlers.misc.Constants;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetStringAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetStringAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(GetStringAnalyzer.class);
	}

	public Object analyzeInstruction() {
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		String cursorRegName = ir.getInvolvedRegisters().get(0).getName();
		String columnIndexRegName = ir.getInvolvedRegisters().get(1).getName();

		SymbolTableEntry cursorEntry = localSymSpace.find(cursorRegName);
		SymbolTableEntry colmIndexEntry = localSymSpace.find(columnIndexRegName);
		SymbolTableEntry uriStringEntry = null;
		Hashtable sensitiveDBUris = Constants.getInstance().getSensitiveDbUris();

		if (cursorEntry != null) {
			EntryDetails cursorEntryDetails = cursorEntry.getEntryDetails();
			Hashtable cursorFieldList = cursorEntryDetails.getRecordFieldList();
			String dbName = "";
			if (cursorFieldList != null && cursorFieldList.containsKey("uri")) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) cursorFieldList.get("uri");
				String uriValue = uriEntry.getEntryDetails().getValue();
				if (uriEntry != null) {
					Hashtable uriFieldList = uriEntry.getEntryDetails().getRecordFieldList();

					if (uriFieldList != null && uriFieldList.containsKey("uriString")) {
						uriStringEntry = (SymbolTableEntry) uriFieldList.get("uriString");
						if (uriStringEntry != null) {
							dbName = uriStringEntry.getEntryDetails().getValue();
						}
					} else if ((uriValue != null) && (!uriValue.isEmpty())) {
						// in a case (Contacts), where direct uri is assigned instead of being parsed via uriString.
						dbName = uriValue;
					}
				}
			}
			String columnName = "";
			if (colmIndexEntry != null) {
				columnName = colmIndexEntry.getEntryDetails().getValue();
			}
			String value = " ";
			boolean isTainted = false;
			SourceInfo si = new SourceInfo();
			si.setSrcInstr(ir.getInstr().getText());

			if (sensitiveDBUris.containsKey(dbName)) {
				isTainted = true;
				value = "[database Uri] = " + dbName + ", [column Name/Index] = " + columnName;
				si.setSrcAPI(value);
				ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
				if (siList == null)
					siList = new ArrayList<SourceInfo>();
				if (!siList.contains(si))
					siList.add(si);
				returnEntryDetails.setSourceInfoList(siList);

			} else if (cursorEntry.getEntryDetails().isTainted()) {
				handleTaintedEntry(returnEntryDetails, cursorEntry, si);
			} else if (colmIndexEntry.getEntryDetails().isTainted()) {
				handleTaintedEntry(returnEntryDetails, colmIndexEntry, si);
			}
			returnEntry.getEntryDetails().setTainted(isTainted);
			returnEntry.setLineNumber(ir.getLineNumber());
			returnEntry.setName(""); // will be set by 'move' instruction.
			returnEntryDetails.setType(ir.getReturnType());
			returnEntryDetails.setValue(value);
			returnEntry.setInstrInfo("");

			returnEntryDetails.setConstant(false);
			returnEntryDetails.setRecord(false);
			returnEntryDetails.setField(false);
			returnEntry.setEntryDetails(returnEntryDetails);
		}
		logger.debug("\n Cursor.GetStringAnalyzer");
		return returnEntry;
	}

	public void handleTaintedEntry(EntryDetails returnEntryDetails, SymbolTableEntry otherEntry, SourceInfo si) {
		returnEntryDetails.setTainted(true);
		String value = otherEntry.getEntryDetails().getValue();
		si.setSrcAPI(value);
		ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
		if (siList == null)
			siList = new ArrayList<SourceInfo>();
		if (!siList.contains(si))
			siList.add(si);
		returnEntryDetails.setSourceInfoList(siList);
	}
}
