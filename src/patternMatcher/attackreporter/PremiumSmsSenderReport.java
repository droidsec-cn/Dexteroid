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

import java.util.Stack;

import models.cfg.MethodSignature;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import configuration.Config;

public class PremiumSmsSenderReport extends Report {

	private String recpNo = "";
	private String sinkAPI = "";

	private String currComponent = "";
	private String message = "";
	private String permutationStr = "";
	private MultiValueMap functionCallStack;
	private String instrContainerCls = "";
	private String instContainerMthd = "";
	private String compName = "";
	private String compMethdName = "";
	private Logger logger;

	public PremiumSmsSenderReport() {
		logger = Logger.getLogger("");
		//		this.setSourceInfoList(new ArrayList<SourceInfo>());

	}

	@Override
	public boolean equals(Object obj) {
		PremiumSmsSenderReport rep = (PremiumSmsSenderReport) obj;
		if (!rep.getRecpNo().equalsIgnoreCase(this.getRecpNo()))
			return false;
		if (!rep.getSinkAPI().equalsIgnoreCase(this.getSinkAPI()))
			return false;
		if (!rep.getCurrComponent().equalsIgnoreCase(this.getCurrComponent()))
			return false;
		return true;
	}

	public void printReport() {
		Config.getInstance().setAttackReported(true);
		logger.fatal("\n\n[msg] = " + this.getMessage());
		logger.fatal("[recpNo] = " + this.getRecpNo());
		logger.fatal(" [instrInfo]= " + this.getSinkAPI());
		logger.fatal("[comp] = " + this.getCurrComponent());
		logger.fatal(" [CFGPermutation]= " + this.getPermutationStr());

		Stack funcCallStack = Config.getInstance().getFuncCallStack();
		if (funcCallStack.size() > 0) {
			logger.fatal("\n\n [Function call stack] ");
			for (int i = 0; i < funcCallStack.size(); i++) {
				MethodSignature ms = (MethodSignature) funcCallStack.get(i);
				if (ms != null) {
					logger.fatal(" [pkgClassName]= " + ms.getPkgClsName() + ", [methodName]= " + ms.getName() + ", [paramsCount]= "
							+ ms.getParams().size());
				}
			}
		}
		logger.fatal("\n\n report-ends!!\n\n");
		logger.fatal("\n\n");
	}

	public String getRecpNo() {
		return recpNo;
	}

	public void setRecpNo(String recpNo) {
		this.recpNo = recpNo;
	}

	public String getSinkAPI() {
		return sinkAPI;
	}

	public void setSinkAPI(String sinkAPI) {
		this.sinkAPI = sinkAPI;
	}

	public String getCurrComponent() {
		return currComponent;
	}

	public void setCurrComponent(String currComponent) {
		this.currComponent = currComponent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPermutationStr() {
		return permutationStr;
	}

	public void setPermutationStr(String permutationStr) {
		this.permutationStr = permutationStr;
	}

	public MultiValueMap getFunctionCallStack() {
		return functionCallStack;
	}

	public void setFunctionCallStack(MultiValueMap functionCallStack) {
		this.functionCallStack = functionCallStack;
	}

	public String getInstrContainerCls() {
		return instrContainerCls;
	}

	public void setInstrContainerCls(String instrContainerCls) {
		this.instrContainerCls = instrContainerCls;
	}

	public String getInstContainerMthd() {
		return instContainerMthd;
	}

	public void setInstContainerMthd(String instContainerMthd) {
		this.instContainerMthd = instContainerMthd;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getCompMethdName() {
		return compMethdName;
	}

	public void setCompMethdName(String compMethdName) {
		this.compMethdName = compMethdName;
	}

}
