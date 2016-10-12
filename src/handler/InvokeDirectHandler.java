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
import java.util.HashSet;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import configuration.Config;

public class InvokeDirectHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private String qualifiedAPIName; //com.android.TelephonyManger->getDeviceId

	public InvokeDirectHandler(Instruction instr, Instruction prev) {
		this.currInstr = instr;
	}

	public InstructionResponse execute() {
		instrText = currInstr.getText();
		ir = new InstructionResponse();
		ir = handleInstruction();
		return ir;
	}

	private InstructionResponse handleInstruction() {
		ArrayList<Register> involvedRegisters = getInvolvedRegisters();
		ir.setInstr(currInstr);
		ir.setInvolvedRegisters(involvedRegisters);
		return ir;
	}

	// 0xc invoke-direct v3, v7, v8, Lcom/test/maliciousactivity/User;-><init>(Ljava/lang/String; Ljava/lang/String;)V
	public ArrayList<Register> getInvolvedRegisters() {
		ArrayList<Register> invokeRegisters = new ArrayList<Register>();
		String[] totalRegisters;

		String inputLine = this.currInstr.getText();
		String splitByArrow[] = inputLine.split("->");
		String rhsSplitByLeftParanthesis[] = splitByArrow[1].split("[(]");
		String leftSideOfArrow[] = splitByArrow[0].split(" ");
		String calledAPIName = leftSideOfArrow[leftSideOfArrow.length - 1];

		String[] paramsString = rhsSplitByLeftParanthesis[1].split("[)]");
		String[] paramsArray = paramsString[0].split(" ");
		this.ir.setReturnType(paramsString[1]);

		String calledMethodName = rhsSplitByLeftParanthesis[0];
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(calledMethodName);
		ir.setCalledMethodNature(currInstr.getCalledMethodType(calledAPIName));
		ir.setLineNumber(leftSideOfArrow[0]);

		qualifiedAPIName = calledAPIName.concat("->").concat(calledMethodName);
		ir.setQualifiedAPIName(qualifiedAPIName);

		String instType = leftSideOfArrow[1];
		if (instType.equalsIgnoreCase("invoke-virtual/range") || instType.equalsIgnoreCase("invoke-direct/range")
				|| instType.equalsIgnoreCase("invoke-static/range") || instType.equalsIgnoreCase("invoke-interface/range")
				|| instType.equalsIgnoreCase("invoke-super/range")) {

			// 0xc invoke-direct v3, v7, v8, Lcom/test/maliciousactivity/User;-><init>(Ljava/lang/String; Ljava/lang/String;)V
			if (leftSideOfArrow[3].equalsIgnoreCase("...")) {

				String firstRegNoStr = "";
				String lastRegNoStr = "";
				String firstRegOfRange = leftSideOfArrow[2];
				String lastRegOfRange = leftSideOfArrow[4];

				// getting register number. It could be v0 or v12 or may be more v123 up to three digit. Maximum is 255, I guess.
				firstRegNoStr = firstRegOfRange.substring(1);
				lastRegNoStr = lastRegOfRange.substring(1, lastRegOfRange.length() - 1); // last will be v12,

				//	System.out.println("Starting register No= " + firstRegNoStr + ", Last Reg No = " + lastRegNoStr  );
				int firstRegNo = Integer.parseInt(firstRegNoStr);
				int lastRegNo = Integer.parseInt(lastRegNoStr);

				totalRegisters = new String[lastRegNo - firstRegNo + 1];
				int startingRegNo = firstRegNo;

				//creating range of registers now. v5 ... v11 for example.
				for (int i = 0; i <= lastRegNo - firstRegNo; i++) {
					String reg = String.valueOf('v').concat(String.valueOf(startingRegNo++));
					totalRegisters[i] = reg;
				}
			} else{
				 // 0x8 invoke-virtual/range v21, Lcom/allen/flashcardsfree/DashboardLayout;->getChildCount()I
				String reg = leftSideOfArrow[2];
				totalRegisters = new String[1];
				totalRegisters[0] = reg.substring(0, reg.length() - 1);
			}			
		} else{
			 // 0x28 invoke-interface v0, v1, v2, Ljava/util/Map;->put(Ljava/lang/Object; Ljava/lang/Object;)Ljava/lang/Object;
			int apiCallIndex = leftSideOfArrow.length - 1; // index of Ljava call. 
			int regCount = apiCallIndex - 2; // 5-2 = 3
			totalRegisters = new String[regCount];
			int k = 2;
			for (int i = 0; i < regCount; i++) {
				String register = leftSideOfArrow[k++];
				totalRegisters[i] = register.substring(0, register.length() - 1);
			}
		}
		invokeRegisters = getListOfRegisters(totalRegisters, paramsArray, calledAPIName);
		return invokeRegisters;
	}
	
	// Now we extract parameter types. Since first register will always be the caller object, we will start from next to it.
	// 0x10 invoke-virtual/range v0 ... v5, Landroid/support/v4/app/ListFragment;->onListItemClick(Landroid/widget/ListView; Landroid/view/View; I J)V	
	// 		0x33c invoke-virtual/range v3 ... v8, Ljava/util/Timer;->schedule(Ljava/util/TimerTask; J J)V
	// 		J takes two registers, instead of one. So we take it as special cases.

	public ArrayList<Register> getListOfRegisters(String[] totalRegisters, String[] paramsArray, String calledAPIName){
		ArrayList<Register> invokeRegisters = new ArrayList<Register>();
		ir.setUsedRegisters(totalRegisters);
		if (paramsArray.length != totalRegisters.length - 1) {
			int paramArrayIndex = 0;
			for (int i = 0; i < totalRegisters.length; i++) {
				Register r = new Register();
				if (i == 0) {
					r.setName(totalRegisters[i]); // name is v0 or v1.
					r.setType(calledAPIName); // Ljava/lang/ etc.
				} else {
					HashSet<String> sixtyFourBitRegisters = Config.getInstance().getSixtyFourBitRegisters();
					String str = paramsArray[paramArrayIndex].trim();
					if (sixtyFourBitRegisters.contains(str)) {
						Register reg1 = new Register();

						reg1.setName(totalRegisters[i]);
						reg1.setType(paramsArray[paramArrayIndex]);
						invokeRegisters.add(reg1);
						i++;
					}
					r.setName(totalRegisters[i]);
					r.setType(paramsArray[paramArrayIndex]);
					paramArrayIndex++;
				}
				invokeRegisters.add(r);
			}
		} else {
			for (int i = 0; i < totalRegisters.length; i++) {
				Register r = new Register();
				if (i == 0) {
					r.setName(totalRegisters[i]); // name is v0 or v1.
					r.setType(calledAPIName); // Ljava/lang/ etc.
				} else {
					r.setName(totalRegisters[i]);
					r.setType(paramsArray[i - 1]);
				}
				invokeRegisters.add(r);
			}
		}
		return invokeRegisters;
	}
}
