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
 
package apihandlers.android.os.Handler;

import java.util.ArrayList;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;
import configuration.Config;

public class SendMessageAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SendMessageAnalyzer(TaintAnalyzer ta){
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SendMessageAnalyzer.class);
	}

	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerAPIReg = involvedRegisters.get(0);
		Register inputReg = involvedRegisters.get(1);
		
		SymbolTableEntry callerAPIEntry = this.localSymSpace.find(callerAPIReg.getName());
		SymbolTableEntry inputEntry = this.localSymSpace.find(inputReg.getName());
		String instrTxt = ir.getInstr().getText();
		String instrSplitWithArrow[] = instrTxt.split("->");
		String arrowRightSideSplit[] = instrSplitWithArrow[1].split("[(]");
		MethodHandler mHandler = new MethodHandler(ta);
   	    APK apk = ta.getApk();
   	    
		if(callerAPIEntry != null){
	  	    MethodSignature newMS = MethodSignature.getMethodSignatureFromCurrInstruction(ir);
	   	    newMS.setName("handleMessage");
	   	    newMS.setPkgClsName(callerAPIEntry.getEntryDetails().getType());
	   	    newMS.setReturnType("V");
		    CFG newCFG = apk.findMethodBySignature(newMS);
		    if(newCFG == null){
			   newMS.setReturnType("Z");
			   newCFG = apk.findMethodBySignature(newMS);
		    }
		    if(newCFG != null){
			   logger.debug("cfg key -> " + newCFG.getKey());
			   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
			   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + newCFG.getSignature().getParams().size());

 			   boolean result = mHandler.handleMethodCall(newCFG);
 			   newCFG.nullifyBBOutSets();
 			   if(result){
	 			   Object obj = ta.getInstrReturnedObject();
	 			   if(null != obj){
	 				   SymbolTableEntry entry = (SymbolTableEntry) obj;
	 			       logger.debug("\n InvokeTaintAnalyzer");
	 			       localSymSpace.logInfoSymbolSpace();

	 			       logger.debug("\n </end> Global Entry");
	 			       Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();
	 			       return entry;
	 			   }
 			   }
			}
		}
     	logger.debug("\n Bundle.getString()");
	    return null;
	}
}
