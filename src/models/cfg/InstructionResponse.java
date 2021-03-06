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
 
package models.cfg;


import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class InstructionResponse {

	private String[] usedRegisters;  		// All of the registers which are used to execute instruction.
	private String changedRegister="";			// Destination register which gets changed after execution of instruction.
	private boolean isSourceAPI=false;			// Set it to true if current instruction is a source API.
	private boolean isSinkAPI=false;				// Set it to true if current instruction is a sink API.
	private Instruction instr;				//current instruction.
	private String returnType="";              //return type of current instruction
	private boolean isPrevInstrSource=false;
	private String lineNumber="";
	private String methodOrObjectName="";   // ->myUser or ->getDeviceId() 
	private String calledMethodNature="";	 // user-defined? android? java? etc.	 
	private String callerAPIName="";		 // Ljava/lang/StringBuilder;-> ...
	private String qualifiedAPIName="";      // "Ljava/lang/StringBuilder;->concat"

	private ArrayList<Register> involvedRegisters;    // Each register contains extra information. See its usage in MoveResultTaintAnalyzer and MoveResultHandler
	
	public String[] getUsedRegisters() {
		return usedRegisters;
	}
	public void setUsedRegisters(String[] usedRegisters) {
		this.usedRegisters = usedRegisters;
	}
	public String getChangedRegister() {
		return changedRegister;
	}
	public void setChangedRegister(String changdRegister) {
		this.changedRegister = changdRegister;
	}
	public boolean isSourceAPI() {
		return isSourceAPI;
	}
	public void setSourceAPI(boolean isSourceAPI) {
		this.isSourceAPI = isSourceAPI;
	}
	public boolean isSinkAPI() {
		return isSinkAPI;
	}
	public void setSinkAPI(boolean isSinkAPI) {
		this.isSinkAPI = isSinkAPI;
	}
	public Instruction getInstr() {
		return instr;
	}
	public void setInstr(Instruction instr) {
		this.instr = instr;
	}
	public ArrayList<Register> getInvolvedRegisters() {
		return involvedRegisters;
	}
	public void setInvolvedRegisters(ArrayList<Register> involvedRegisters) {
		this.involvedRegisters = involvedRegisters;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public boolean isPrevInstrSource() {
		return isPrevInstrSource;
	}
	public void setPrevInstrSource(boolean isPrevInstrSource) {
		this.isPrevInstrSource = isPrevInstrSource;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}


	public String getCallerAPIName() {
		return callerAPIName;
	}
	public void setCallerAPIName(String callerAPIName) {
		this.callerAPIName = callerAPIName;
	}
	public String getMethodOrObjectName() {
		return methodOrObjectName;
	}
	public void setMethodOrObjectName(String methodOrObjectName) {
		this.methodOrObjectName = methodOrObjectName;
	}
	public String getCalledMethodNature() {
		return calledMethodNature;
	}
	public void setCalledMethodNature(String calledMethodNature) {
		this.calledMethodNature = calledMethodNature;
	}
	public String getQualifiedAPIName() {
		return qualifiedAPIName;
	}
	public void setQualifiedAPIName(String qualifiedAPIName) {
		this.qualifiedAPIName = qualifiedAPIName;
	}

}
