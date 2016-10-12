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

public class Register {

	String name;
	String type;
	boolean tainted;
	String value;
	boolean constant;
	String referenceObject;
	String callerObjectType;
	String fullyQualifiedName;
	
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	public String getCallerObjectType() {
		return callerObjectType;
	}
	public void setCallerObjectType(String callerObjectType) {
		this.callerObjectType = callerObjectType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isTainted() {
		return tainted;
	}
	public void setTainted(boolean tainted) {
		this.tainted = tainted;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isConstant() {
		return constant;
	}
	public void setConstant(boolean constant) {
		this.constant = constant;
	}
	public String getReferenceObject() {
		return referenceObject;
	}
	public void setReferenceObject(String referenceObject) {
		this.referenceObject = referenceObject;
	}

}
