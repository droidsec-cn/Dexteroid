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

import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class FilledNewArrayTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * 0xb4 filled-new-array v0, v5, [I
	 * 0xba move-result-object v0
	 * filled-new-array {parameters},type_id
	 */
	public FilledNewArrayTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(NewArrayTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register param1Reg;
		Register param2Reg;
		param1Reg = ir.getInvolvedRegisters().get(0); // v0
		param2Reg = ir.getInvolvedRegisters().get(1); // v5
		SymbolTableEntry param1Entry = localSymSpace.find(param1Reg.getName());
		SymbolTableEntry param2Entry = localSymSpace.find(param2Reg.getName());
		SymbolTableEntry arrayEntry = new SymbolTableEntry();
		EntryDetails arrayEntryDetails = arrayEntry.getEntryDetails();

		arrayEntry.setInstrInfo(ir.getInstr().getText());
		arrayEntry.setLineNumber(ir.getLineNumber());
		arrayEntry.setName(param1Reg.getName());
		arrayEntryDetails.setType(ir.getReturnType());

		Hashtable recordFieldList = (Hashtable) arrayEntryDetails.getRecordFieldList();
		if (param1Entry != null) {
			arrayEntry.addFieldDirectlyToRecordEntryByKey("key1", param1Entry);
		}
		if (param2Entry != null) {
			arrayEntry.addFieldDirectlyToRecordEntryByKey("key2", param2Entry);
		}
		arrayEntryDetails.setRecordFieldList(recordFieldList);
		arrayEntry.setEntryDetails(arrayEntryDetails);

		logger.debug("\n FilledNewArrayTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return arrayEntry;
	}

}
