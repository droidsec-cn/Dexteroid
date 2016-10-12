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
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

public class InvokeStaticHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private String qualifiedApiName; //com.android.TelephonyManger->getDeviceId

	private static Logger logger;

	public InvokeStaticHandler(Instruction instr, Instruction prev) {
		this.currInstr = instr;
		logger = Logger.getLogger(InvokeStaticHandler.class);
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

	// 0x8e invoke-static v8, v9, v10, v11, Ljava/lang/Math;->pow(D D)D
	//	0x2e invoke-static v5, v6, Ljava/lang/String;->valueOf(J)Ljava/lang/String;
	//0x13e invoke-static Ljava/util/Locale;->getDefault()Ljava/util/Locale;
	public ArrayList<Register> getInvolvedRegisters() {
		ArrayList<Register> invokeRegisters = new ArrayList<Register>();
		String[] totalRegisters;

		String inputLine = this.currInstr.getText();
		String twoSidesOfArrow[] = inputLine.split("->");
		String rhsSplitByLeftParanthesis[] = twoSidesOfArrow[1].split("[(]");
		String leftSideOfArrow[] = twoSidesOfArrow[0].split(" ");
		String calledAPIName = leftSideOfArrow[leftSideOfArrow.length - 1];
		String newCalledName = calledAPIName.substring(1, calledAPIName.length() - 1); // L from start and ; from end are removed.

		String[] paramsString = rhsSplitByLeftParanthesis[1].split("[)]");
		String[] paramsArray = paramsString[0].split(" ");
		this.ir.setReturnType(paramsString[1]);
		logger.trace("retType=> " + paramsString[1] + ", rhs => " + rhsSplitByLeftParanthesis[1]);
		String calledMethodName = rhsSplitByLeftParanthesis[0];

		ir.setLineNumber(leftSideOfArrow[0]);
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(calledMethodName);
		ir.setCalledMethodNature(currInstr.getCalledMethodType(calledAPIName));

		qualifiedApiName = calledAPIName.concat("->").concat(calledMethodName);
		ir.setQualifiedAPIName(qualifiedApiName);

		String instType = leftSideOfArrow[1];
		if (instType.equalsIgnoreCase("invoke-static/range")) {
			if (leftSideOfArrow[3].equalsIgnoreCase("...")) {
				String firstRegNoStr = "";
				String lastRegNoStr = "";
				String firstRegOfRange = leftSideOfArrow[2];
				String lastRegOfRange = leftSideOfArrow[4];

				firstRegNoStr = firstRegOfRange.substring(1);
				lastRegNoStr = lastRegOfRange.substring(1, lastRegOfRange.length() - 1); // last will be v12,
				int firstRegNo = Integer.parseInt(firstRegNoStr);
				int lastRegNo = Integer.parseInt(lastRegNoStr);
				totalRegisters = new String[lastRegNo - firstRegNo + 1];

				int startingRegNo = firstRegNo;
				for (int i = 0; i <= lastRegNo - firstRegNo; i++) {
					String reg = String.valueOf('v').concat(String.valueOf(startingRegNo++));
					totalRegisters[i] = reg;
				}
			} else{
				String reg = leftSideOfArrow[2];
				totalRegisters = new String[1];
				totalRegisters[0] = reg.substring(0, reg.length() - 1);
			}
		}else {
			int apiCallIndex = leftSideOfArrow.length - 1; // index of Ljava call. 
			int regCount = apiCallIndex - 2; // 5-2 = 3
			totalRegisters = new String[regCount];

			int k = 2;
			for (int i = 0; i < regCount; i++) {
				String register = leftSideOfArrow[k++];
				totalRegisters[i] = register.substring(0, register.length() - 1);
			}
		}

		invokeRegisters = getListOfRegisters(totalRegisters,paramsArray, calledAPIName);
		logger.trace("[InvokeStaticHandler.java]");
		for (int i = 0; i < totalRegisters.length; i++) {
			logger.trace(totalRegisters[i] + "   ");
		}
		return invokeRegisters;
	}
	
	public ArrayList<Register> getListOfRegisters(String[] totalRegisters, String[] paramsArray, String calledAPIName) {
		ArrayList<Register> invokeRegisters = new ArrayList<Register>();
		ir.setUsedRegisters(totalRegisters);
		logger.debug("< totalRegLength>" + totalRegisters.length);
		if (paramsArray != null) {
			logger.debug("paramsArrayLength>>> " + paramsArray.length);
			if (paramsArray.length != totalRegisters.length && paramsArray.length > 0) {
				int paramArrayIndex = 0;
				for (int i = 0; i < totalRegisters.length; i++) {
					Register r = new Register();
					String type = paramsArray[paramArrayIndex];
					HashSet<String> sixtyFourBitRegisters = Config.getInstance().getSixtyFourBitRegisters();
					if (sixtyFourBitRegisters.contains(type)) {
						Register reg1 = new Register();

						reg1.setName(totalRegisters[i]);
						reg1.setType(type);
						invokeRegisters.add(reg1);
						i++;
					}
					r.setName(totalRegisters[i]);
					r.setType(type);
					paramArrayIndex++;
					invokeRegisters.add(r);
				}
			} else {
				for (int i = 0; i < totalRegisters.length; i++) {
					Register r = new Register();
					r.setName(totalRegisters[i]);
					r.setType(paramsArray[i]);
					invokeRegisters.add(r);
				}
			}
		}
		return invokeRegisters;
	}
}
