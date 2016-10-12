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
 
package apihandlers.android.util.Log;

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

public class LoggingAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private SymbolSpace globalSymSpace;

	public LoggingAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(LoggingAnalyzer.class);
	}

	public Object analyzeInstruction() {
		ArrayList<Register> involvedRisters = ir.getInvolvedRegisters();
		Register param2Reg = involvedRisters.get(1);
		SymbolTableEntry param2Entry = localSymSpace.find(param2Reg.getName());
		this.globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		SymbolTableEntry logcatEntry = globalSymSpace.find("logcat");

		boolean isNewEntry = false;
		if (logcatEntry == null) {
			logcatEntry = new SymbolTableEntry();
			isNewEntry = true;
		}
		EntryDetails logcatEntryDetails = logcatEntry.getEntryDetails();
		ArrayList<SourceInfo> logcatSiList = logcatEntryDetails.getSourceInfoList();

		if (param2Entry != null) {
			if (param2Entry.getEntryDetails().isTainted()) {
				logcatEntryDetails.setTainted(true);
				ArrayList<SourceInfo> paramSiList = param2Entry.getEntryDetails().getSourceInfoList();
				if (paramSiList != null && paramSiList.size() > 0) {
					if (logcatSiList == null) {
						logcatSiList = new ArrayList<SourceInfo>();
					}
					for (SourceInfo si : paramSiList) {
						if (!logcatSiList.contains(si)) {
							logcatSiList.add(si);
						}
					}
				}
			}
		}
		logcatEntryDetails.setSourceInfoList(logcatSiList);
		logcatEntryDetails.setType("Landroid/util/Log;");
		logcatEntry.setEntryDetails(logcatEntryDetails);

		if (isNewEntry) {
			logcatEntry.setName("logcat");
			globalSymSpace.addEntry(logcatEntry);
			globalSymSpace.logInfoSymbolSpace();
		}
		logger.debug("\n LoggingAnalyzer");
		return null;
	}
}
