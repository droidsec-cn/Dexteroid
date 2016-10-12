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
 
package models.cfg;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import analyzer.Analyzer;

public class CFGComponent {

	protected String key = "";
	protected String text = "";
	protected String qualifiedKey = "";

	protected String currMethodName = "";
	protected String currClassName = "";
	protected String currPkgName = "";
	protected String currPkgClassName = "";
	protected String currClassType = "";
	private String currBBKey = "";
	private boolean isAnalyzedAtLeaseOnce = false;

	protected ArrayList<CFGComponent> compCollection;
	protected static Logger logger;

	public void accept(Analyzer a) {
	}

	public java.util.Iterator iterator() {
		return null;
	}

	public void addItem(CFGComponent c) {
	}

	public boolean removeItem(CFGComponent c) {
		return false;
	}

	public void setItem(int index, CFGComponent comp) {
	}

	public CFGComponent getItem(int index) {
		return null;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCurrMethodName() {
		return currMethodName;
	}

	public void setCurrMethodName(String currMethodName) {
		this.currMethodName = currMethodName;
	}

	public String getCurrClassName() {
		return currClassName;
	}

	public void setCurrClassName(String currClassName) {
		this.currClassName = currClassName;
	}

	public String getCurrPkgName() {
		return currPkgName;
	}

	public void setCurrPkgName(String currPkgName) {
		this.currPkgName = currPkgName;
	}

	public String getCurrPkgClassName() {
		return currPkgClassName;
	}

	public void setCurrPkgClassName(String currPkgClassName) {
		this.currPkgClassName = currPkgClassName;
	}

	public ArrayList<CFGComponent> getCompCollection() {
		return compCollection;
	}

	public void setCompCollection(ArrayList<CFGComponent> compCollection) {
		this.compCollection = compCollection;
	}

	public String getCurrClassType() {
		return currClassType;
	}

	public void setCurrClassType(String currClassType) {
		this.currClassType = currClassType;
	}

	public String getCurrBBKey() {
		return currBBKey;
	}

	public void setCurrBBKey(String currBBKey) {
		this.currBBKey = currBBKey;
	}

	public boolean isAnalyzedAtLeaseOnce() {
		return isAnalyzedAtLeaseOnce;
	}

	public void setAnalyzedAtLeaseOnce(boolean isAnalyzedAtLeaseOnce) {
		this.isAnalyzedAtLeaseOnce = isAnalyzedAtLeaseOnce;
	}

	public String getQualifiedKey() {
		return qualifiedKey;
	}

	public void setQualifiedKey(String qualifiedKey) {
		this.qualifiedKey = qualifiedKey;
	}

}
