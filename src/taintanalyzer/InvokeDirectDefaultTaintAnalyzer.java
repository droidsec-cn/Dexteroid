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

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.EntryDetails;
import symboltable.SourceInfo;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InvokeDirectDefaultTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	ArrayList<Register> involvedRegisters;
	SymbolTableEntry returnEntry;

	// If method returns something, it returns and stores via move-result, And if the method is <init>, it creates that object
	// and stores it in the first variable.
	public InvokeDirectDefaultTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		involvedRegisters = new ArrayList<Register>();
		returnEntry = new SymbolTableEntry();
		logger = Logger.getLogger(InvokeDirectDefaultTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		boolean tainted = false;
		involvedRegisters = ir.getInvolvedRegisters();
		String returnType = ir.getReturnType();
		String methodOrObjectName = ir.getMethodOrObjectName();
		if (involvedRegisters.size() > 0) {
			Register callereReg = involvedRegisters.get(0);
			SymbolTableEntry callerEntry; // = this.localSymSpace.find(callereReg.getName());

			//Case#1
			if (returnType.trim().equalsIgnoreCase("V")) {
				if (methodOrObjectName.trim().equalsIgnoreCase("<init>")) {
					Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
					callerEntry = this.localSymSpace.find(callereReg.getName());
					SymbolTableEntry destEntry = null;

					if (callerEntry != null) {
						destEntry = (SymbolTableEntry) callerEntry.clone(); //make shallow copy
						EntryDetails destEntryDetails = destEntry.getEntryDetails();
						for (int i = 1; i < involvedRegisters.size(); i++) {
							Register reg = involvedRegisters.get(i);
							SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
							destEntry.addSensitiveDataIfNeeded(entry);
						}
						destEntryDetails.setType(ir.getCallerAPIName());
						destEntry.setEntryDetails(destEntryDetails);
						this.localSymSpace.addEntry(destEntry);

					} else {
						destEntry = new SymbolTableEntry();
						EntryDetails destEntryDetails = destEntry.getEntryDetails();
						destEntry.setName(callereReg.getName());
						destEntry.setInstrInfo(ir.getInstr().getText());
						destEntry.setLineNumber(ir.getLineNumber());

						ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();
						for (int i = 1; i < involvedRegisters.size(); i++) {
							Register reg = involvedRegisters.get(i);
							SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
							destEntry.addSensitiveDataIfNeeded(entry);
						}
						if (!destEntryDetails.isTainted()) // if not tainted already, 
							destEntryDetails.setTainted(tainted);
						destEntryDetails.setSourceInfoList(srcInfoList);
						destEntryDetails.setType(ir.getCallerAPIName());
						destEntry.setEntryDetails(destEntryDetails);
						this.localSymSpace.addEntry(destEntry); //Even if the entry exists, it will be replaced with new one.
					}
				} else {
					// When method name is not <init>, in that case, it just does nothing. Because even if any of the input
					// params is tainted, no varible is affected.
					
					callerEntry = this.localSymSpace.find(callereReg.getName());
					String qualifiedAPIName = ir.getCallerAPIName().concat("->").concat(ir.getMethodOrObjectName());
					String currPkgClassName = ir.getInstr().getCurrPkgClassName();
					if (!currPkgClassName.equalsIgnoreCase(qualifiedAPIName)) {
						if (callerEntry != null) {
							EntryDetails callerEntryDetails = callerEntry.getEntryDetails();
							callerEntry.setName(callereReg.getName());
							callerEntry.setInstrInfo(ir.getInstr().getText());
							callerEntry.setLineNumber(ir.getLineNumber());
							for (int i = 1; i < involvedRegisters.size(); i++) {
								Register reg = involvedRegisters.get(i);
								SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
								callerEntry.addSensitiveDataIfNeeded(entry);
							}
							callerEntryDetails.setType(ir.getCallerAPIName());
							callerEntry.setEntryDetails(callerEntryDetails);
						}
					}
				}
				logger.debug("\n InvokeDirectDefaultTaintAnalyzer");
				localSymSpace.logInfoSymbolSpace();
				return null;
			} else {

				Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
				callerEntry = this.localSymSpace.find(callereReg.getName());
				SymbolTableEntry destEntry = null;
				if (callerEntry != null) {
					if (immutableObjects.containsKey(returnType.trim())) {
						destEntry = new SymbolTableEntry(callerEntry); //make deep copy
					} else {
						destEntry = (SymbolTableEntry) callerEntry.clone(); //make shallow copy
					}
				} else {
					destEntry = new SymbolTableEntry();
				}
				EntryDetails destEntryDetails = destEntry.getEntryDetails();
				for (int i = 0; i < involvedRegisters.size(); i++) {
					Register reg = involvedRegisters.get(i);
					SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
					destEntry.addSensitiveDataIfNeeded(entry);
				}
				destEntry.setName(""); //any name you give here does not matter. It will be set by move instruction.
				destEntry.setLineNumber(ir.getLineNumber());
				destEntry.setInstrInfo(ir.getInstr().getText());

				destEntryDetails.setType(returnType);
				destEntryDetails.setConstant(false);
				destEntryDetails.setField(false);
				destEntryDetails.setRecord(false);
				destEntryDetails.setValue("");
				destEntry.setEntryDetails(destEntryDetails);
				return destEntry;
			}
		}
		return null;
	}
}
