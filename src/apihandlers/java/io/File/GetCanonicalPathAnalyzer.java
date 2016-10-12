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
 
package apihandlers.java.io.File;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetCanonicalPathAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetCanonicalPathAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(GetCanonicalPathAnalyzer.class);
	}

	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register fileReg = involvedRegisters.get(0);
        SymbolTableEntry fileEntry = localSymSpace.find(fileReg.getName());
        SymbolTableEntry filePathEntry = new SymbolTableEntry();
        
        if(fileEntry != null){
			filePathEntry = new SymbolTableEntry(fileEntry); // deep copy
        	filePathEntry.getEntryDetails().setValue(fileEntry.getEntryDetails().getValue());
        	logger.error("GETCANONICAl = " + filePathEntry.getEntryDetails().getValue());
        }
    	filePathEntry.getEntryDetails().setType(ir.getReturnType());
        logger.debug("\n CreateTempFileAnalyzer");
	   return filePathEntry;
	}
}
