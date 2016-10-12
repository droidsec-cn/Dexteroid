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
 
package patternMatcher.attackreporter;

import java.util.ArrayList;
import java.util.Stack;

import models.cfg.Instruction;
import models.cfg.MethodSignature;
import symboltable.SourceInfo;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InfoLeakReporter{

	public static void reportAttack(Instruction instr, ArrayList<SourceInfo> srcList, TaintAnalyzer ta) {
		String instrTxt = instr.getText();
		ArrayList<SourceInfo> reportedSources = new ArrayList<SourceInfo>();
		for (SourceInfo si : srcList) {
			if (!reportedSources.contains(si)) {
				reportedSources.add(si);
			}
		}
		String info1 = instrTxt;
		String permStr = Config.getInstance().getCurrCFGPermutationString();
		String compType = ta.getCurrCls().getType();

		InformationLeakerReport rep = new InformationLeakerReport();
		rep.setInstrContainerCls(instr.getCurrPkgClassName());
		rep.setInstContainerMthd(instr.getCurrMethodName());
		rep.setCompName(ta.getCurrComponentName());
		rep.setCompMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentType(compType);
		rep.setCurrComponent(ta.getCurrComponentPkgName());

		rep.setSourceInfoList(reportedSources);
		rep.setSinkAPI(info1);
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This API can leak Information::");

		Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
		Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();
		for (int i = 0; i < funcCallStack.size(); i++) {
			MethodSignature oldMS = funcCallStack.get(i);
			MethodSignature newMS = new MethodSignature(oldMS);
			funcCallStackCopy.add(newMS);
		}
		rep.setFunctionCallStack(funcCallStackCopy);

		if (Config.getInstance().isUniqueWarningEnabled()) {
			if (!AttackReporter.getInstance().checkIfInfoLeakExists(rep)) {
				AttackReporter.getInstance().getInfoLeakReportList().add(rep);
				rep.printReport();
			}
		} else {
			AttackReporter.getInstance().getInfoLeakReportList().add(rep);
			rep.printReport();
		}
	}
}
