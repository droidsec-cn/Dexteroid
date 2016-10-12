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

import handler.InvokeHandler;

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

public class RegisterContentObserverAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public RegisterContentObserverAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(RegisterContentObserverAnalyzer.class);
		this.ta = ta;
	}
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register cResolverReg = involvedRegisters.get(0);
		Register uriReg = involvedRegisters.get(1);
		Register booleanReg = involvedRegisters.get(2);
		Register cObserverReg = involvedRegisters.get(3);
		
		SymbolTableEntry cResolverEntry = localSymSpace.find(cResolverReg.getName());
		SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
		SymbolTableEntry booleanEntry = localSymSpace.find(booleanReg.getName());
		SymbolTableEntry cObserverEntry = localSymSpace.find(cObserverReg.getName());
		
		SymbolTableEntry inputEntry = new SymbolTableEntry();
		inputEntry.getEntryDetails().setType("Z");
		inputEntry.setName("v250");
		this.localSymSpace.addEntry(inputEntry);
		
		String text = "		0xcc invoke-virtual " + cObserverReg.getName() + ", v250, ";
		text += cObserverEntry.getEntryDetails().getType();
		text += "->onChange(Z)V";

		Instruction newInstr = new Instruction();
		newInstr.setText(text);
		InvokeHandler invokeHandler = new InvokeHandler(newInstr, null);
		InstructionResponse newIR = invokeHandler.execute();

		logger.debug("<isntr> = " + text);
		MethodHandler mHandler = new MethodHandler(ta);
		mHandler.setIr(newIR);
   	    MethodSignature ms = MethodSignature.getMethodSignatureFromCurrInstruction(newIR);

   	    APK apk = ta.getApk();
   	    if(ms != null){
 		   CFG cfg = apk.findMethodBySignature(ms);
 		   if(cfg != null){
 			   logger.debug("cfg key -> " + cfg.getKey());
 			   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
 			   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

  			   boolean result = mHandler.handleMethodCall(cfg);
  			   if(result){
 	 			   Object obj = ta.getInstrReturnedObject();
 	 			   if(null != obj){
 	 				   SymbolTableEntry entry = (SymbolTableEntry) obj;
 	 			       localSymSpace.logInfoSymbolSpace();
 	 			       return entry;
 	 			   }
  			   }
 		   }
 	   }
	   return null;
	}
}
