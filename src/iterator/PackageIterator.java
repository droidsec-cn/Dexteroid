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
 
package iterator;

import java.util.ArrayList;

import java.util.Iterator;

import models.cfg.BasicBlock;
import models.cfg.CFGComponent;

public class PackageIterator implements Iterator<Object> {

	private CFGComponent currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currClassObjCollection;

	public PackageIterator(CFGComponent myComp) {
		currComp = myComp;
		currClassObjCollection = myComp.getCompCollection();
		currPosition = 0;
	}

	public boolean hasNext() {
		//		System.out.println("Class Iterator.hasNext() -> position" + currPosition + ", size" + currClassObjCollection.size() );
		if (currPosition < currClassObjCollection.size())
			return true;
		return false;
	}

	public Object next() {
		Object obj = currClassObjCollection.get(currPosition);
		currPosition += 1;
		return obj;
	}

	public boolean remove(Object obj) {
		if (currComp.removeItem((CFGComponent) obj)) {
			currPosition -= 1;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return currComp.getItem(currPosition).toString();
	}

	@Override
	public void remove() {
	}
}
