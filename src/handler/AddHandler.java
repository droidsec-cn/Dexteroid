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

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class AddHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;

	public AddHandler(Instruction instr, Instruction prev) {
		this.currInstr = instr;
	}

	// 0xf6 add-int/lit8 v8, v8, 1
	// 0x1c add-int v3, v7, v2
	// ushr-int/lit8 vx, vy, lit8
	// Other types of add operations may involve three registers.
	public InstructionResponse execute() {
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();

		if (splitInstr != null) {
			String[] opcodeSplit = splitInstr[1].split("-");
			String instrType = "";

			if (opcodeSplit != null) {
				if (opcodeSplit.length == 1) {
					instrType = "I";
				} else {
					String opcodeRightSide = opcodeSplit[1];
					if (opcodeRightSide.contains("/")) {
						String[] newVal = opcodeRightSide.split("[//]");
						opcodeRightSide = newVal[0];
					}
					if (opcodeRightSide != null && !opcodeRightSide.isEmpty()) {
						if (opcodeRightSide.equalsIgnoreCase("int"))
							instrType = "I";
						else if (opcodeRightSide.equalsIgnoreCase("boolean"))
							instrType = "Z";
						else if (opcodeRightSide.equalsIgnoreCase("byte"))
							instrType = "B";
						else if (opcodeRightSide.equalsIgnoreCase("char"))
							instrType = "C";
						else if (opcodeRightSide.equalsIgnoreCase("short"))
							instrType = "S";
					}
				}

			}

			Register r1 = new Register();
			String reg1 = splitInstr[2];
			reg1 = reg1.substring(0, reg1.length() - 1);
			r1.setName(reg1);
			r1.setType(instrType);
			ir.setReturnType(instrType);

			Register r2 = new Register();
			String reg2 = splitInstr[3];
			reg2 = reg2.substring(0, reg2.length() - 1);
			r2.setName(reg2);

			Register r3 = new Register();
			String reg3 = splitInstr[4];
			r3.setName(reg3);

			ir.setUsedRegisters(new String[] { reg1, reg2, reg3 }); // v0
			involvedRegisters.add(r1);
			involvedRegisters.add(r2);
			involvedRegisters.add(r3);

		}
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		ir.setLineNumber(splitInstr[0]);
		return ir;
	}

}
