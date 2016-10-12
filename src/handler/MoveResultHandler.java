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
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class MoveResultHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private Instruction prevInstr;

	public MoveResultHandler(Instruction instr, Instruction prev) {
		this.prevInstr = prev;
		this.currInstr = instr;
	}

	public InstructionResponse execute() {
		ir = new InstructionResponse();
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();

		//BaseHandler invokeHandler = new InvokeHandler(prevInstr, null);
		InstructionResponse prevInstrIR = this.prevInstr.getInstResponse();
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		String reg = splitInstr[2];

		//It could be '0x24 move-result vAA' or 'move-result-wide vAA' or 'move-result-object vAA' 
		Register r = new Register();
		if (splitInstr != null) {
			ir.setChangedRegister(reg); //vAA
			r.setTainted(false);
			ir.setPrevInstrSource(false);
			r.setType(" ");
			r.setValue(" ");
			if (prevInstrIR != null) {
				if (prevInstrIR.isSourceAPI()) {
					ir.setPrevInstrSource(true);
					r.setTainted(true);
				}
				String prevInstrTypeBySyntax = prevInstr.getTypeBySyntax();
				if (prevInstrTypeBySyntax.startsWith("invoke")) {
					r.setType(prevInstrIR.getReturnType()); 
					String prevInstrText = prevInstr.getText();
					String[] split1 = prevInstrText.split("->");
					String[] split2 = split1[1].split("[(]");

					r.setValue(split2[0]);
				} else if (prevInstrTypeBySyntax.startsWith("filled")) {
					r.setType(prevInstrIR.getReturnType());
				}
			}
			r.setName(reg);
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		return ir;
	}
}
