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

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

import symboltable.Context;
import symboltable.EntryDetails;
import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;

public class IputObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;

	public IputObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IputObjectTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		String instrText = ir.getInstr().getText();
		logger.debug(ir.getInstr().getText());
		Register srcReg = ir.getInvolvedRegisters().get(0);
		Register destReg = ir.getInvolvedRegisters().get(1);

		SymbolTableEntry srcLocalEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry destLocalEntry = localSymSpace.find(destReg.getName());
		logger.debug("\n IputTaintAnalyzer--Before performing analysis");
		localSymSpace.logInfoSymbolSpace();
		boolean isDeepCopyRequested = false; //By default, we will go with shallow copy.
		String objectName = ir.getMethodOrObjectName();
		Hashtable immutableObjects = Config.getInstance().getImmutableObjects();

		String returnType = ir.getReturnType();
		SymbolTableEntry field = null; //= new SymbolTableEntry();
		if (srcLocalEntry != null) {
			EntryDetails srcEntryDetails = srcLocalEntry.getEntryDetails();
			String srcEntryValue = srcEntryDetails.getValue();
			String srcType = srcEntryDetails.getType();
			if (srcEntryValue != null && srcEntryValue.trim().equalsIgnoreCase("0") && srcEntryDetails.getType().equalsIgnoreCase("I")) {
				if (destLocalEntry != null) {
					field = new SymbolTableEntry();
					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());
					
					//srcLocalEntry may not have defined type, so we also set it explicitly.
					EntryDetails fieldEntryDetails = field.getEntryDetails();

					// fieldEntryDetails.setType(ir.getReturnType()); This is not true, sometimes it sets parent class but we need to
					// set child (original) class name.
					fieldEntryDetails.setValue(srcEntryValue);
					fieldEntryDetails.setConstant(true);
					fieldEntryDetails.setType(returnType);
					field.setEntryDetails(fieldEntryDetails);
					EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();
					destEntryDetails.setRecord(true);
					destEntryDetails.setType(ir.getCallerAPIName());

					destLocalEntry.addFieldDirectlyToRecordEntryByKey(field.getName(), field);
					destLocalEntry.setEntryDetails(destEntryDetails);
				} else {
					//Ideally this case should never arise, but just in case.
					destLocalEntry = new SymbolTableEntry();
					EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

					destLocalEntry.setName(destReg.getName());
					destLocalEntry.setLineNumber(ir.getLineNumber());
					destEntryDetails.setRecord(true);
					destEntryDetails.setType(ir.getCallerAPIName());
					destEntryDetails.setValue("");

					field = new SymbolTableEntry();
					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());
					
					EntryDetails fieldEntryDetails = field.getEntryDetails();
					fieldEntryDetails.setType(ir.getReturnType());
					fieldEntryDetails.setValue("0");
					fieldEntryDetails.setConstant(true);
					fieldEntryDetails.setType(srcEntryDetails.getType());

					destLocalEntry.addFieldDirectlyToRecordEntryByKey(field.getName(), field);
					destLocalEntry.setEntryDetails(destEntryDetails);
					this.localSymSpace.addEntry(destLocalEntry);
				}
			}

			//CASE#2 
			// If we need deep copy.
			// http://stackoverflow.com/questions/5124012/examples-of-immutable-classes
			// Other options are there but not so important.
			else if (immutableObjects.containsKey(srcType) || immutableObjects.containsKey(returnType)) {
				boolean isNewEntry = false;
				EntryDetails destEntryDetails;
				if (destLocalEntry == null) {
					destLocalEntry = new SymbolTableEntry();
					destLocalEntry.setName(destReg.getName());
					destLocalEntry.setLineNumber(ir.getLineNumber());
					isNewEntry = true;
				}
				destEntryDetails = destLocalEntry.getEntryDetails();
				field = new SymbolTableEntry(srcLocalEntry); //deep copy
				field.setName(objectName);
				field.setInstrInfo(this.ir.getInstr().getText());
				field.setLineNumber(ir.getLineNumber());
				field.getEntryDetails().setType(ir.getReturnType());

				destEntryDetails.setRecord(true);
				destEntryDetails.setType(ir.getCallerAPIName());
				destLocalEntry.addFieldDirectlyToRecordEntryByKey(field.getName(), field);
				destLocalEntry.setEntryDetails(destEntryDetails);
				
				if(isNewEntry){
					this.localSymSpace.addEntry(destLocalEntry);
				}
			} else {
				boolean isNewEntry = false;
				EntryDetails destEntryDetails;
				if (destLocalEntry == null) {
					destLocalEntry = new SymbolTableEntry();
					destLocalEntry.setName(destReg.getName());
					destLocalEntry.setLineNumber(ir.getLineNumber());
					isNewEntry = true;
				}
				field = (SymbolTableEntry) srcLocalEntry.clone();
				field.setName(objectName);
				field.setInstrInfo(this.ir.getInstr().getText());
				field.setLineNumber(ir.getLineNumber());
				if (srcType != null && !srcType.isEmpty())
					field.getEntryDetails().setType(srcType);
				else
					field.getEntryDetails().setType(ir.getReturnType());
				destEntryDetails = destLocalEntry.getEntryDetails();
				destEntryDetails.setRecord(true);
				String callerAPIName = ir.getCallerAPIName();
				destEntryDetails.setType(callerAPIName);

				destLocalEntry.addFieldDirectlyToRecordEntryByKey(field.getName(), field);
				destLocalEntry.setEntryDetails(destEntryDetails);
				if(isNewEntry){
					this.localSymSpace.addEntry(destLocalEntry);
				}
			}
		}
		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		logger.debug("\n IputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
