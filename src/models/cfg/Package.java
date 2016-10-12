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

import iterator.PackageIterator;
import java.util.Iterator;
//import iterator.Iterator;

import java.util.*;

import analyzer.*;

public class Package extends CFGComponent implements Iterable<CFGComponent> {

	public Package() {
		compCollection = new ArrayList<CFGComponent>();
	}


	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	//	@Override
	public Iterator iterator() {
		return (Iterator) new PackageIterator(this);
	}

	public void accept(Analyzer a) {

		a.analyze(this);

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
