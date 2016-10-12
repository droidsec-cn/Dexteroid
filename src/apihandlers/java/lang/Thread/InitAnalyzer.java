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
 
package apihandlers.java.lang.Thread;

import java.util.ArrayList;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
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

public class InitAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction() {
		int regCount = ir.getInvolvedRegisters().size();
		Register reg1 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());
		if(reg1Entry != null){
			if (regCount > 1){
				ArrayList<Register> regs = ir.getInvolvedRegisters();
				Register runnableReg = null;
				for(int i=1; i < regs.size(); i++){
					Register reg = regs.get(i);
					if(reg.getType().equalsIgnoreCase("Ljava/lang/Runnable;")){
						runnableReg = reg;
						break;
					}
				}
				if(runnableReg != null){
					SymbolTableEntry runnableEntry = localSymSpace.find(runnableReg.getName());
					if (runnableEntry != null){
						String type = runnableEntry.getEntryDetails().getType();
						reg1Entry.getEntryDetails().setType(type);
					}
				}
			}
		}
		logger.debug("\n Thread.InitAnalyzer ");
		return null;
	}
}
