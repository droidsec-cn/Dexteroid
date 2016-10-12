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
 
package apihandlers.android.content.ContentResolver;

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
import apihandlers.misc.Constants;
import configuration.Config;

public class ApplyBatchAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ApplyBatchAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ApplyBatchAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register contentResolverReg = involvedRegisters.get(0);
		Register contractAuthorityReg = involvedRegisters.get(1);
		Register operationsReg = involvedRegisters.get(2);
		
		SymbolTableEntry contentResolverEntry = localSymSpace.find(contentResolverReg.getName());
		SymbolTableEntry contractAuthorityEntry = localSymSpace.find(contractAuthorityReg.getName());
		SymbolTableEntry operationsEntry = localSymSpace.find(operationsReg.getName());

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		SymbolTableEntry uriStringEntry;
		String dbName="";
	    String uriValue = contractAuthorityEntry.getEntryDetails().getValue();
		
		// Though contentReslverEntry will be untainted, but just in case.
	    returnEntry.addSensitiveDataIfNeeded(operationsEntry);
	    returnEntry.addSensitiveDataIfNeeded(contentResolverEntry);
	    
		Hashtable sensitiveDBUris = Constants.getInstance().getSensitiveDbUris();
	    returnEntry.setInstrInfo(ir.getInstr().getText());
	    returnEntryDetails.setType(ir.getReturnType());
	    returnEntry.setEntryDetails(returnEntryDetails);
        logger.debug("\n ContentResolver.QueryAnalyzer");
	    return returnEntry;
	}
}
