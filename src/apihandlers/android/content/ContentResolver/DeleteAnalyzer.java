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

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import apihandlers.misc.Constants;
import configuration.Config;

public class DeleteAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	
	public DeleteAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(DeleteAnalyzer.class);
	}

	public Object analyzeInstruction(){
		Register callerAPIReg = ir.getInvolvedRegisters().get(0);
		Register uriReg = ir.getInvolvedRegisters().get(1);
		SymbolTableEntry contentResolverEntry = localSymSpace.find(callerAPIReg.getName());

		//Setting up first parameter (uri)
		Hashtable sensitiveDBUris = Constants.getInstance().getSensitiveDbUris();
		Hashtable recordFieldList;
		SymbolTableEntry uriStringEntry = null;
		
    	if(contentResolverEntry != null){
    		 recordFieldList = contentResolverEntry.getEntryDetails().getRecordFieldList();
    		 if(recordFieldList == null){
    			 recordFieldList = new Hashtable();
    		 }
    		 
			SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
			boolean uriEntryTainted = false;
	     	   
	        if(uriEntry != null){
	        	//Since Uri is an object, we will pass shallow copy.
	        	SymbolTableEntry clonedUriEntry=null;
	        	uriEntryTainted = uriEntry.getEntryDetails().isTainted();
	
	        	clonedUriEntry = (SymbolTableEntry) uriEntry.clone();
	        	recordFieldList.put("uri", clonedUriEntry);   // Uri uri
	        }
        
			//Setting up second parameter (projection)
			Register projectionReg = ir.getInvolvedRegisters().get(2);
			SymbolTableEntry projectionEntry = localSymSpace.find(projectionReg.getName());
	        if(projectionEntry != null){
	        
	        	SymbolTableEntry clonedProjectionEntry=null;
	       		clonedProjectionEntry = (SymbolTableEntry) projectionEntry.clone();
	        	recordFieldList.put("projection", clonedProjectionEntry);   
	        }
        
			Register selectionReg = ir.getInvolvedRegisters().get(3);
			SymbolTableEntry selectionEntry = localSymSpace.find(selectionReg.getName());
	        if(selectionEntry != null){
	        
	        	SymbolTableEntry clonedSelectionEntry=null;
	        	clonedSelectionEntry = (SymbolTableEntry) selectionEntry.clone();
	        	recordFieldList.put("selection", clonedSelectionEntry);   
	        }
	        contentResolverEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    	}
    	return null;
	}
}
