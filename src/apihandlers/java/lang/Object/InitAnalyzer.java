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
 
package apihandlers.java.lang.Object;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;


import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

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
		Register destReg = ir.getInvolvedRegisters().get(0);
        SymbolTableEntry entry=localSymSpace.find(destReg.getName());
        String regType = ir.getInstr().getCurrPkgClassName();
        if(entry != null && entry.getEntryDetails().getType().equalsIgnoreCase(regType)){
        	//This means that entry has already been defined there. Weird thing in Dalvik byte code, I know. :)
        	// Ignore in a case where an entry has been defined already. This case arises when we <init> method of a class.
        	// We push parameter entries then, which also includes 'this' variable. We do that object creation there because 
        	// in some of <init> methods, object.init() is not called. So we just wanted to make sure we would have that object. 

        }else{
    	    entry = new SymbolTableEntry();
    	    EntryDetails entryDetails = entry.getEntryDetails();

    	    entry.setName(destReg.getName());
       	    entry.setLineNumber(ir.getLineNumber());
     	    entryDetails.setType(regType);
     	    entryDetails.setTainted(false);
     	    entryDetails.setConstant(false);
     	    entryDetails.setField(false);
     	    entryDetails.setRecord(false);
     	    entry.setEntryDetails(entryDetails);
	   	    localSymSpace.addEntry(entry);
       }
       logger.debug("\n InitAnalyzer");
       return null;
	}
}
