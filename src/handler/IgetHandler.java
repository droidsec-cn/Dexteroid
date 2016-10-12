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
 
package handler;

import java.util.ArrayList;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;

public class IgetHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	public IgetHandler(Instruction instr, Instruction prev) {
		this.currInstr = instr;
	}

	public InstructionResponse execute() {
		instrText = currInstr.getText();
		ir = new InstructionResponse();
		ir = handleInstruction();
		return ir;
	}

	private InstructionResponse handleInstruction() {
		involvedRegisters = getInvolvedRegisters();
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		return ir;
	}

	// 	0x2a iget-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;	
	public ArrayList<Register> getInvolvedRegisters() {
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		String[] usedRegisters;

		String inputLine = this.currInstr.getText();
		String splitByArrow[] = inputLine.split("->");
		String leftSideSplitBySpace[] = splitByArrow[0].split(" ");
		String rightSideSplitBySpace[] = splitByArrow[1].split(" ");
		String calledAPIName = leftSideSplitBySpace[leftSideSplitBySpace.length - 1];

		String reg1 = leftSideSplitBySpace[2];
		String reg2 = leftSideSplitBySpace[3];
		reg1 = reg1.substring(0, reg1.length() - 1);
		reg2 = reg2.substring(0, reg2.length() - 1);

		usedRegisters = new String[1];
		usedRegisters[0] = reg2;
		ir.setUsedRegisters(usedRegisters);
		ir.setChangedRegister(reg1);
		ir.setLineNumber(leftSideSplitBySpace[0]);

		//	 	0x2a iget-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;			
		Register r1 = new Register();
		r1.setName(reg1); // v0
		r1.setType(rightSideSplitBySpace[1]);

		Register r2 = new Register();
		r2.setName(reg2); // v3
		r2.setReferenceObject(rightSideSplitBySpace[0]); //myUser. May be, it should be called object instead of reference one.
		r2.setCallerObjectType(calledAPIName); //Lcom/test/maliciousactivity/MainActivity;

		String obectReturnType = rightSideSplitBySpace[1];
		r2.setType(obectReturnType); // Lcom/test/maliciousactivity/User;
		ir.setReturnType(obectReturnType);

		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(rightSideSplitBySpace[0]);

		String completeAPIName = calledAPIName.concat("->").concat(rightSideSplitBySpace[0]);
		ir.setQualifiedAPIName(completeAPIName);
		involvedRegisters.add(r1);
		involvedRegisters.add(r2);

		return involvedRegisters;
	}
}
