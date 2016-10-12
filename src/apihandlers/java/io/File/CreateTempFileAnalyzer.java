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

import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class CreateTempFileAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public CreateTempFileAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(CreateTempFileAnalyzer.class);
	}
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register prefixReg = involvedRegisters.get(0);
		Register suffixReg = involvedRegisters.get(1);

		Register directoryReg=null;
		SymbolTableEntry directoryEntry= null;
		if(involvedRegisters.size() > 2){
			directoryReg = involvedRegisters.get(2);
		    directoryEntry = localSymSpace.find(directoryReg.getName());
		}	
        SymbolTableEntry prefixEntry = localSymSpace.find(prefixReg.getName());
        SymbolTableEntry suffixEntry = localSymSpace.find(suffixReg.getName());
        String filePath = "<DefaultTempDirectory>";
        String prefix= "<file>";
        String suffix = ".tmp";

        if(directoryEntry != null){
        	String value = directoryEntry.getEntryDetails().getValue();
        	if(value != null )  // TODO && Integer.parseInt(value) != 0  when null is passed as third parameter
        		filePath = value;
        }
        if(prefixEntry != null && prefixEntry.getEntryDetails().getValue() != null){
        	String value = prefixEntry.getEntryDetails().getValue();
        	if(value != null)
        		prefix = value;
        }
        if(suffixEntry != null && suffixEntry.getEntryDetails().getValue() != null){
        	String value = suffixEntry.getEntryDetails().getValue();
        	if(value != null)
        		suffix = value;
        }
        filePath = filePath.concat("/").concat(prefix).concat(suffix);
        SymbolTableEntry returnEntry = new SymbolTableEntry();
        EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
        
        logger.debug("CreateTempFileAnalyzer::" + filePath);
        returnEntry.setName("");   // will be set by move-result instruction.
        returnEntryDetails.setType(ir.getReturnType()); 
        returnEntry.setLineNumber(ir.getLineNumber());
        returnEntryDetails.setTainted(false);
        returnEntryDetails.setConstant(false);
        returnEntryDetails.setField(false);
    	
        returnEntryDetails.setRecord(false);
        returnEntryDetails.setValue(filePath);
        returnEntry.setInstrInfo(ir.getInstr().getText());
		returnEntry.setEntryDetails(returnEntryDetails);
	    return returnEntry;
	}
}
