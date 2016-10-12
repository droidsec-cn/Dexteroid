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

import models.cfg.APK;
import models.cfg.ClassObj;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class SputObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	APK apk;

	public SputObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		logger = Logger.getLogger(SputObjectTaintAnalyzer.class);
		apk = ta.getApk();
	}
	
	public Object analyzeInstruction() {
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		SymbolTableEntry destEntry;
		SymbolTableEntry srcEntry;
		Register srcReg = ir.getInvolvedRegisters().get(0);
		String objectName = ir.getCallerAPIName();
		String fieldName = ir.getMethodOrObjectName();
		String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());

		Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
		srcEntry = localSymSpace.find(srcReg.getName());
		if (srcEntry != null) {
			String returnType = ir.getReturnType().trim();
			if (immutableObjects.containsKey(returnType)) {
				destEntry = new SymbolTableEntry(srcEntry); //deep copy
			} else {
				destEntry = (SymbolTableEntry) srcEntry.clone(); //shallow copy
			}
		} else {
			destEntry = new SymbolTableEntry();
		}
		destEntry.setInstrInfo(ir.getInstr().getText());
		destEntry.setLineNumber(ir.getLineNumber());
		destEntry.setName(qualifiedAPIName);
		
		if (srcEntry != null) {
			String srcEntryType = srcEntry.getEntryDetails().getType();
			if (!srcEntryType.isEmpty()) {
				destEntry.getEntryDetails().setType(srcEntryType);
			} else {
				destEntry.getEntryDetails().setType(ir.getReturnType());
			}
		} else {
			destEntry.getEntryDetails().setType(ir.getReturnType());
		}
		globalSymSpace.addEntry(destEntry);
		logger.debug("\n SputObject-TaintAnalyzer");
		logger.debug("\n Printing Global SymSpace");
		globalSymSpace.logInfoSymbolSpace();

		return null;
	}

}
