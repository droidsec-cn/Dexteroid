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
 
package apihandlers.java.lang.Runtime;

import java.util.ArrayList;
import java.util.Enumeration;
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

public class ExecAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private SymbolSpace globalSymSpace;

	public ExecAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ExecAnalyzer.class);
	}

	public Object analyzeInstruction(){
		globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		Register callerApiReg = ir.getInvolvedRegisters().get(0);
		Register param1Reg = ir.getInvolvedRegisters().get(1);
     	   
        SymbolTableEntry callerEntry=localSymSpace.find(callerApiReg.getName());
        SymbolTableEntry paramEntry=localSymSpace.find(param1Reg.getName());
        SymbolTableEntry returnEntry = new SymbolTableEntry();
        EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
        
        returnEntry.setName(""); //Actually set by move- instruction
        returnEntry.setLineNumber(ir.getLineNumber());
        returnEntry.setInstrInfo(ir.getInstr().getText());
        returnEntryDetails.setType(ir.getReturnType());
        ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
        String regType = ir.getInstr().getCurrPkgClassName();
    	boolean isLogcatOperation = false;

    	if(paramEntry != null){
    		Hashtable recordFieldList = paramEntry.getEntryDetails().getRecordFieldList();
    		if(recordFieldList != null){
		    	String[] args; // = new String[fieldListSize];
		    	String logcatOperation = "";
		    	
		    	if(recordFieldList!= null && recordFieldList.size() > 0){
		    		int fieldListSize = recordFieldList.size();
		    		args = new String[fieldListSize];
		    		
		    		Enumeration<String> keys = recordFieldList.keys();
		    		int i=0;
		    		while(keys.hasMoreElements()){
		    			String key = keys.nextElement();
		    			SymbolTableEntry entry = (SymbolTableEntry) recordFieldList.get(key);
		    			String value = entry.getEntryDetails().getValue();
		    			
		    			if(value != null){
		        			args[i] = value;
		        			i++;
		    			}
		    		}
		    		//TODO We don't go to much precise details. If log is written, it is read using
		    		// logcat. For now, I am not going to specifics of .v or .d or .c etc. Will refine it if it is needed laters.
		    		if(args[0].trim().equalsIgnoreCase("logcat") || args[0].trim().startsWith("logcat")
		    				|| args[0].trim().equalsIgnoreCase("'logcat'") || args[0].trim().startsWith("'logcat")){
		    			logcatOperation = args[0];
		    			isLogcatOperation = true;
		    		}
		    	}else{
		    		String value = paramEntry.getEntryDetails().getValue();
		    		if(value != null && !value.isEmpty()){
		    			if(value.trim().equalsIgnoreCase("logcat") || value.trim().startsWith("logcat")
		    					|| value.trim().equalsIgnoreCase("'logcat'") || value.trim().startsWith("'logcat")){
		    				isLogcatOperation = true;
		    				logcatOperation = value;
		    			}
		    		}
		    	}
		        if(isLogcatOperation){
					SymbolTableEntry logcatEntry = globalSymSpace.find("logcat");
					if(logcatEntry != null){
						if(logcatEntry.getEntryDetails().isTainted()){
							ArrayList<SourceInfo> logcatSiList = logcatEntry.getEntryDetails().getSourceInfoList();
							if(logcatSiList != null && logcatSiList.size() > 0 ){
								if(siList == null)
									siList = new ArrayList<SourceInfo>();
								siList.addAll(logcatSiList); // It's a new entry. addAll is fine here.
								returnEntryDetails.setTainted(true);
							}
						}
					}
		        }
		        returnEntryDetails.setSourceInfoList(siList);
		        returnEntryDetails.setValue(logcatOperation);
		        returnEntry.setEntryDetails(returnEntryDetails);
    		}
    	}
	    logger.debug("\n Runtime.exec analyzer");
        return returnEntry;
	}
}
