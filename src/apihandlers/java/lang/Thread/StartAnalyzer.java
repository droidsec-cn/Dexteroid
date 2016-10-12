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

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;

public class StartAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private APK apk;

	public StartAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(StartAnalyzer.class);
		apk = ta.getApk();
		this.ta = ta;
	}

	public Object analyzeInstruction() {
		logger.debug("MethodHandler in StartAnalyzer is called.");
		MethodHandler mHandler = new MethodHandler(ta);
		Register runnableReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry runnableEntry = localSymSpace.find(runnableReg.getName());
		boolean isAsyncComponentsAnalyzedWithinCode = Config.getInstance().isAsyncComponentsAnalyzedWithinCode();

		if (isAsyncComponentsAnalyzedWithinCode) {
			if (runnableEntry != null) {
				APK apk = ta.getApk();
				logger.debug("inside InitAnalzer of java.lang.Thread");
				String threadType = runnableEntry.getEntryDetails().getType();
				ClassObj thread = apk.findClassByKey(threadType);
				if (thread != null) {
					CFG runMethod = (CFG) thread.findCFGByKey("run");
					mHandler = new MethodHandler(ta);
					if (runMethod != null) {
						logger.debug("cfg key -> " + runMethod.getKey());
						thread.setAnalyzedAtLeaseOnce(true);
						boolean result = mHandler.handleMethodCall(runMethod);
						runMethod.nullifyBBOutSets();
					}
				}
			} else {
				String currPkgClssName = instr.getCurrPkgClassName();
				if (currPkgClssName != null && !currPkgClssName.isEmpty()) {
					ClassObj cls = apk.findClassByKey(currPkgClssName);
					if (cls != null) {
						CFG cfg = (CFG) cls.findCFGByKey("run");
						if (cfg != null) {
							logger.debug("cfg key -> " + cfg.getKey());
							cls.setAnalyzedAtLeaseOnce(true);
							boolean result = mHandler.handleMethodCall(cfg);
							if (result) {
								Object obj = ta.getInstrReturnedObject();
								if (null != obj) {
									SymbolTableEntry entry = (SymbolTableEntry) obj;
									logger.debug("\n StartAnalyzer");
									localSymSpace.logInfoSymbolSpace();
									return entry;
								}
							}
							return null;
						}
					}
				}
			}
		}
		this.localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
