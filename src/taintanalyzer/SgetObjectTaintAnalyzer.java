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
 
package taintanalyzer.instranalyzers;

import handler.InvokeHandler;
import handler.InvokeStaticHandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ApiTypesBySyntax;

public class SgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private TaintAnalyzer ta;

	public SgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		logger = Logger.getLogger(SgetObjectTaintAnalyzer.class);
		this.ta = ta;
	}

	//	0x22 sget-object v2, Lcom/geinimi/c/j;->a Ljava/lang/String;
	public Object analyzeInstruction() {
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		SymbolTableEntry destEntry;
		SymbolTableEntry destGlobalEntry;
		Register destReg = ir.getInvolvedRegisters().get(0); //v2
		String instrText = ir.getInstr().getText();
		
		String srcDestType = "";
		String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());
		Properties sourceSinkAPIMap = Config.getInstance().getSourceSinkAPIMap();

		Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
		String apiInfo = "";
		Instruction instr = ir.getInstr();
		apiInfo = String.valueOf(" [PkgClass] = ").concat(
				instr.getCurrPkgClassName().concat(" , [method] = ").concat(instr.getCurrMethodName()));

		if (sourceSinkAPIMap.containsKey(qualifiedAPIName)) {
			destEntry = new SymbolTableEntry();
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Mostly it is String.
			destEntry.getEntryDetails().setTainted(true);

			SourceInfo srcInfo = new SourceInfo();
			srcInfo.setSrcAPI(qualifiedAPIName);
			srcInfo.setSrcInstr(ir.getInstr().getText());
			ArrayList<SourceInfo> siList = destEntry.getEntryDetails().getSourceInfoList();
			if (siList == null)
				siList = new ArrayList<SourceInfo>();

			if (!siList.contains(srcInfo))
				siList.add(srcInfo);
			destEntry.getEntryDetails().setSourceInfoList(siList);
			localSymSpace.addEntry(destEntry);
		} else {
			SymbolTableEntry srcEntry = globalSymSpace.find(qualifiedAPIName);
			if (srcEntry != null) {
				String returnType = ir.getReturnType();
				String srcEntryType = srcEntry.getEntryDetails().getType();
				if (immutableObjects.containsKey(returnType)) {
					destEntry = new SymbolTableEntry(srcEntry); // deep copy
					destEntry.setInstrInfo(ir.getInstr().getText());
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setName(destReg.getName());
					destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
					localSymSpace.addEntry(destEntry);

				} else {
					//shallow copy.
					destEntry = (SymbolTableEntry) srcEntry.clone();
					destEntry.setInstrInfo(ir.getInstr().getText());
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setName(destReg.getName());
					destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

					if (ir.getReturnType().equalsIgnoreCase("Landroid/os/Handler;") && !srcEntryType.isEmpty()) {
						destEntry.getEntryDetails().setType(srcEntryType);
					}
					localSymSpace.addEntry(destEntry);
				}
			} else {
				String text = "		0xcc invoke-static " + ir.getCallerAPIName() + "-><clinit>()V";
				Instruction newInstr = new Instruction();
				newInstr.setText(text);
				InvokeStaticHandler invokeHandler = new InvokeStaticHandler(newInstr, null);
				InstructionResponse newIR = invokeHandler.execute();

				logger.debug("<isntr> = " + text);
				MethodHandler mHandler = new MethodHandler(ta);
				mHandler.setIr(newIR);
				MethodSignature ms = MethodSignature.getMethodSignatureFromCurrInstruction(newIR);
				APK apk = ta.getApk();
				if (ms != null) {
					CFG cfg = apk.findMethodBySignature(ms);
					if (cfg != null) {
						logger.debug("cfg key -> " + cfg.getKey());
						logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
						logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

						boolean result = mHandler.handleMethodCall(cfg);
						srcEntry = globalSymSpace.find(qualifiedAPIName);

						if (srcEntry != null) {
							String returnType = ir.getReturnType();
							String srcEntryType = srcEntry.getEntryDetails().getType();
							if (immutableObjects.containsKey(returnType)) {
								destEntry = new SymbolTableEntry(srcEntry); // deep copy
								destEntry.setInstrInfo(ir.getInstr().getText());
								destEntry.setLineNumber(ir.getLineNumber());
								destEntry.setName(destReg.getName());
								destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

								localSymSpace.addEntry(destEntry);
							} else {
								//shallow copy.
								destEntry = (SymbolTableEntry) srcEntry.clone();
								destEntry.setInstrInfo(ir.getInstr().getText());
								destEntry.setLineNumber(ir.getLineNumber());
								destEntry.setName(destReg.getName());
								if (ir.getReturnType().equalsIgnoreCase("Landroid/os/Handler;") && !srcEntryType.isEmpty()) {
									destEntry.getEntryDetails().setType(srcEntryType);
								} else {
									destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
								}
								localSymSpace.addEntry(destEntry);
							}
						} else {
							destEntry = new SymbolTableEntry();
							destEntry.setInstrInfo(ir.getInstr().getText());
							destEntry.setLineNumber(ir.getLineNumber());
							destEntry.setName(destReg.getName());
							destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
							localSymSpace.addEntry(destEntry);
						}
					}
				}
			}
		}

		logger.debug("\n SgetObjectTaintAnalyzer");
		logger.debug("\n Printing Global SymSpace");
		globalSymSpace.logInfoSymbolSpace();
		return null;
	}

}
