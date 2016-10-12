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

import models.cfg.MethodSignature;
import models.cfg.Parameter;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import symboltable.SourceInfo;
import configuration.Config;

public class InformationLeakerReport extends Report {

	private String sinkAPI = "";
	private String sourceAPI = "";
	private ArrayList<SourceInfo> sourceInfoList;

	private String currComponent = "";
	private String currComponentType = "";
	private String message = "";
	private String permutationStr = "";
	private Stack functionCallStack;
	private String instrContainerCls = "";
	private String instContainerMthd = "";
	private String compName = "";
	private String compMethdName = "";

	private Logger logger;

	public InformationLeakerReport() {
		logger = Logger.getLogger("");
		this.setSourceInfoList(new ArrayList<SourceInfo>());
	}

	@Override
	public boolean equals(Object obj) {
		InformationLeakerReport rep = (InformationLeakerReport) obj;
		ArrayList<SourceInfo> repSourceInfoList = rep.getSourceInfoList();

		//Two lists have same sizes but may have different source items. Similarly, if all the items in the larger list are same as given
		// in smaller list, still they are same.
		for (SourceInfo si : repSourceInfoList) {
			if (!this.getSourceInfoList().contains(si))
				return false;
		}
		for (SourceInfo si : this.getSourceInfoList()) {
			if (!repSourceInfoList.contains(si))
				return false;
		}
		if (!rep.getSinkAPI().trim().equalsIgnoreCase(this.getSinkAPI().trim()))
			return false;

		return true;
	}

	public void printReport() {
		Config.getInstance().setAttackReported(true);
		logger.fatal("\n\n[msg] = " + this.getMessage());
		for (SourceInfo si : this.getSourceInfoList()) {
			logger.fatal("[srcInfo]= " + si.getSrcAPI());
			logger.fatal("[srcInstrInfo]= " + si.getSrcInstr());
		}

		logger.fatal("[sink] = " + this.getSinkAPI());
		logger.fatal("\n\n[sinkContainerClsMthd] = " + this.getInstrContainerCls() + " " + this.getInstContainerMthd());
		logger.fatal("[ComponentInfo] = " + this.getCompName() + "/" + this.getCurrComponent() + "; " + this.getCompMethdName()
				+ ", compType=" + this.getCurrComponentType());
		logger.fatal(" [CFGPermutation] = " + this.getPermutationStr());

		Stack funcCallStack = this.getFunctionCallStack();
		if (funcCallStack.size() > 0) {
			logger.fatal("\n\n [Function call stack] from first-method to last-invoked-method order");
			for (int i = 0; i < funcCallStack.size(); i++) {
				MethodSignature ms = (MethodSignature) funcCallStack.get(i);
				if (ms != null) {
					String paramTypes = "";
					for (Parameter param : ms.getParams()) {
						paramTypes += param.getType() + " , ";
					}
					logger.fatal(" [pkgClassName]= " + ms.getPkgClsName() + ", [methodName]= " + ms.getName() + ", [paramTypes]= "
							+ paramTypes);
				}
			}
		}

		logger.fatal("\n\n report-ends!!\n\n");
	}

	public String getSinkAPI() {
		return sinkAPI;
	}

	public void setSinkAPI(String sinkAPI) {
		this.sinkAPI = sinkAPI;
	}

	public String getSourceAPI() {
		return sourceAPI;
	}

	public void setSourceAPI(String sourceAPI) {
		this.sourceAPI = sourceAPI;
	}

	public ArrayList<SourceInfo> getSourceInfoList() {
		return sourceInfoList;
	}

	public void setSourceInfoList(ArrayList<SourceInfo> sourceInfoList) {
		this.sourceInfoList = sourceInfoList;
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

	public Stack getFunctionCallStack() {
		return functionCallStack;
	}

	public void setFunctionCallStack(Stack functionCallStack) {
		this.functionCallStack = functionCallStack;
	}

	public String getCurrComponentType() {
		return currComponentType;
	}

	public void setCurrComponentType(String currComponentType) {
		this.currComponentType = currComponentType;
	}

}
