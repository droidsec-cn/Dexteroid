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
 
package apihandlers.java.util.Formatter;

import java.util.ArrayList;
import java.util.Hashtable;

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

public class InitAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(InitAnalyzer.class);
	}
	
	public Object analyzeInstruction(){
		int regCount = ir.getInvolvedRegisters().size();
		if(regCount == 2){
			Register reg1 = ir.getInvolvedRegisters().get(0);  
			Register reg2 = ir.getInvolvedRegisters().get(1);  
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());

			boolean isNewEntry = false;
			if (callerEntry == null) {
				callerEntry = new SymbolTableEntry();
				isNewEntry = true;
			}	
        	callerEntry.setLineNumber(ir.getLineNumber());
        	if(inputParamEntry != null){
        		SymbolTableEntry shallowCopiedInput = (SymbolTableEntry) inputParamEntry.clone();
        		shallowCopiedInput.setName("input");
        		Hashtable recordFieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
        		if(recordFieldList == null){
        			recordFieldList = new Hashtable();
        		}
        		recordFieldList.put(shallowCopiedInput.getName(), shallowCopiedInput);
        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
        		callerEntry.addSensitiveDataIfNeeded(inputParamEntry);
        	}            	
        	if(isNewEntry){
            	this.localSymSpace.addEntry(callerEntry);
	       }
		}
       logger.debug("\n <InitAnalyzer>");
       return null;
	}
}
