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
 
package apihandlers.android.content.Intent;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetExtrasAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetExtrasAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetExtrasAnalyzer.class);
	}
	
	public Object analyzeInstruction(){
		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
	
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
 		returnEntryDetails.setConstant(false);
 		returnEntryDetails.setField(false);
 		returnEntryDetails.setRecord(false);
	 	   returnEntryDetails.setTainted(false);

		AndroidManifest am = Config.getInstance().getAndroidManifest();
		String currPkgClsInfo = instr.getCurrPkgClassName();
		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);
		ArrayList<String> permList = am.getUsedPermStrList();
		
		if(compInfo != null){
			if(compInfo.getType().equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString())
					&& (ta.getCurrComponentName().equalsIgnoreCase("onReceive"))
					){
		 	   returnEntryDetails.setTainted(true);

		 	   SourceInfo si = new SourceInfo();
		 	   si.setSrcAPI(qualifiedAPIName);
		 	   si.setSrcInstr(ir.getInstr().getText());
		 	   ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList(); 
		 	   if(siList == null)
		 		   siList = new ArrayList<SourceInfo>();
		 	    siList.add(si); //It is a new symboltableEntry
			}
		}
 	   returnEntryDetails.setType(ir.getReturnType());
 	   returnEntry.setInstrInfo(ir.getInstr().getText());
 	   returnEntryDetails.setValue("");
 	   returnEntry.setEntryDetails(returnEntryDetails);
 	   localSymSpace.logInfoSymbolSpace();
	   return returnEntry;
	}
}
