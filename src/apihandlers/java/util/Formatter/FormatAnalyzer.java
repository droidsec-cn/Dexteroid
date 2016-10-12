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

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class FormatAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public FormatAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(FormatAnalyzer.class);
	}

	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  
		Register reg2 = ir.getInvolvedRegisters().get(1);  
		Register reg3 = ir.getInvolvedRegisters().get(2);  

		SymbolTableEntry returnEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg3.getName());
        logger.debug("start AppendAnalyzer");

        if(returnEntry != null){
        	returnEntry.getEntryDetails().setType(reg1.getType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	logger.debug("isReg1 Tainted? " + returnEntry.getEntryDetails().isTainted());
        	if(inputParamEntry != null){
        		boolean tainted = false;
        		String sourceAPI = "";
        		String instrInfo = "";
        		SymbolTableEntry inputStoredEntry = this.localSymSpace.find(returnEntry.getName(), "input");  

        		returnEntry.addSensitiveDataIfNeeded(inputParamEntry);
        		returnEntry.addSensitiveDataIfNeeded(inputStoredEntry);
           		if(returnEntry.getEntryDetails().isTainted()){
        			inputStoredEntry.getEntryDetails().setTainted(tainted);
           		}
        		returnEntry.setInstrInfo(instrInfo);
          	}
        	returnEntry.getEntryDetails().setField(false);
        	returnEntry.getEntryDetails().setRecord(false);
        	return returnEntry;
       }
       logger.debug("\n <AppendAnalyzer>");
       return null;
	}
}
