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
import java.util.Collections;
import java.util.Comparator;

import symboltable.SourceInfo;

public class AttackReporter {

	private ArrayList<InformationLeakerReport> infoLeakReportList;
	private ArrayList<PremiumSmsSenderReport> premiumSmsReportList;
	private static AttackReporter instance = null;

	private AttackReporter() {
		infoLeakReportList = new ArrayList<InformationLeakerReport>();
		premiumSmsReportList = new ArrayList<PremiumSmsSenderReport>();
	}

	public void resetAllExistingReports() {
		infoLeakReportList = new ArrayList<InformationLeakerReport>();
		premiumSmsReportList = new ArrayList<PremiumSmsSenderReport>();
	}

	public static AttackReporter getInstance() {
		if (instance == null) {
			synchronized (AttackReporter.class) {
				if (instance == null) {
					return instance = new AttackReporter();
				}
			}
		}
		return instance;
	}

	public void reportAllUniqueInfoLeakWarnings() {
		if (infoLeakReportList != null && infoLeakReportList.size() > 0) {
			Collections.sort(infoLeakReportList, new Comparator<InformationLeakerReport>() {
				@Override
				public int compare(InformationLeakerReport r1, InformationLeakerReport r2) {
					return r1.getSourceInfoList().size() - r2.getSourceInfoList().size();
				}
			});
			int size = infoLeakReportList.size();
			for (int i = 0; i < size; i++) {
				boolean subsumable = false;
				for (int j = i + 1; j < size; j++) {
					if (isOneListSubsumableByOther(infoLeakReportList.get(i).getSourceInfoList(), infoLeakReportList.get(j)
							.getSourceInfoList())
							&& infoLeakReportList.get(i).getSinkAPI().equalsIgnoreCase(infoLeakReportList.get(j).getSinkAPI())) {
						subsumable = true;
						break;
					}
				}
				if (!subsumable) {
					infoLeakReportList.get(i).printReport();
				}
			}
		}
	}

	private boolean isOneListSubsumableByOther(ArrayList<SourceInfo> list1, ArrayList<SourceInfo> list2) {
		boolean subsumable = true;
		for (SourceInfo si : list1) {
			if (!list2.contains(si)) {
				return false;
			}
		}
		return subsumable;
	}

	public boolean checkIfInfoLeakExists(InformationLeakerReport report) {
		if (infoLeakReportList.contains(report))
			return true;
		return false;
	}
	
	public boolean checkIfPremiumSmsSenderExists(PremiumSmsSenderReport report) {
		if (premiumSmsReportList.contains(report))
			return true;
		return false;
	}

	public ArrayList<InformationLeakerReport> getInfoLeakReportList() {
		return infoLeakReportList;
	}

	public void setInfoLeakReportList(ArrayList<InformationLeakerReport> infoLeakReportList) {
		this.infoLeakReportList = infoLeakReportList;
	}

	public ArrayList<PremiumSmsSenderReport> getPremiumSmsReportList() {
		return premiumSmsReportList;
	}

	public void setPremiumSmsReportList(ArrayList<PremiumSmsSenderReport> premiumSmsReportList) {
		this.premiumSmsReportList = premiumSmsReportList;
	}

}
