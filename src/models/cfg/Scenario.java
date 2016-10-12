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
import java.util.List;

public class Scenario {

	private ArrayList<String> sequenceItems;

	public Scenario() {
		sequenceItems = new ArrayList<String>();
	}

	public ArrayList<String> getSequenceItems() {
		return sequenceItems;
	}

	public void setSequenceItems(ArrayList<String> sequenceItems) {
		this.sequenceItems = sequenceItems;
	}

}
