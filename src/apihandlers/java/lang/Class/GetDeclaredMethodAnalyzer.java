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
 
package apihandlers.java.lang.Class;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class GetDeclaredMethodAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetDeclaredMethodAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetDeclaredMethodAnalyzer.class);
	}

	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);
		Register param2Reg = involvedRegisters.get(2);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());
		
		SymbolTableEntry destEntry = null;
		if(callerApiEntry != null){
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    destEntry.getEntryDetails().setType(ir.getReturnType());
    	    Hashtable recordFieldList = destEntry.getEntryDetails().getRecordFieldList();
    	    
    	    destEntry.addFieldDirectlyToRecordEntryByKey("class", callerApiEntry);
    	    if(param1Entry != null){
    	    	destEntry.getEntryDetails().setValue(param1Entry.getEntryDetails().getValue());
    	    	destEntry.addFieldDirectlyToRecordEntryByKey("method", param1Entry);
    	    }
    	    destEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    	    destEntry.setInstrInfo(ir.getInstr().getText());
       	       		   
			return destEntry;
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}
