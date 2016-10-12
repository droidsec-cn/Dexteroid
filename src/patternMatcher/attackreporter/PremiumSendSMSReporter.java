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

import models.cfg.Instruction;
import models.manifest.AndroidManifest;
import symboltable.EntryDetails;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class PremiumSendSMSReporter {

	public static void repotAttack(Instruction instr, SymbolTableEntry recipientNoEntry, TaintAnalyzer ta) {
		AndroidManifest am = Config.getInstance().getAndroidManifest();
		String currPkgClsInfo = instr.getCurrPkgClassName();

		if (recipientNoEntry != null) {
			EntryDetails userED = recipientNoEntry.getEntryDetails();
			String entryValue = userED.getValue().trim();
			if ((userED.isConstant() && !entryValue.equalsIgnoreCase("0"))) {

				String info1 = recipientNoEntry.getInstrInfo() + ", \n [instr]" + instr.getText();
				String permStr = Config.getInstance().getCurrCFGPermutationString();

				PremiumSmsSenderReport rep = new PremiumSmsSenderReport();
				rep.setInstrContainerCls(instr.getCurrPkgClassName());
				rep.setInstContainerMthd(instr.getCurrMethodName());
				rep.setCompName(ta.getCurrComponentName());
				rep.setCompMethdName(ta.getCurrComponentCallback());
				rep.setCurrComponent(ta.getCurrComponentPkgName());

				rep.setCurrComponent(ta.getCurrComponentName());
				rep.setSinkAPI(info1);
				rep.setRecpNo(entryValue);
				rep.setPermutationStr(permStr);
				rep.setMessage(" ##### This API can send SMS to premium numbers::");

				if (!AttackReporter.getInstance().checkIfPremiumSmsSenderExists(rep)) {
					AttackReporter.getInstance().getPremiumSmsReportList().add(rep);
					rep.printReport();
				}
			}
		}
	}
}
