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
 
package models.manifest;

import java.util.ArrayList;

import models.cfg.CFGComponent;

public class Application {

	private ArrayList<ComponentManifest> componentsManifest;
	private ArrayList<String> componentList;

	public Application() {
		setComponentsManifest(new ArrayList<ComponentManifest>());
	}

	public ArrayList<ComponentManifest> getComponentsManifest() {
		return componentsManifest;
	}

	public void setComponentsManifest(ArrayList<ComponentManifest> componentsManifest) {
		this.componentsManifest = componentsManifest;
	}

	public ArrayList<String> getComponentList() {
		return componentList;
	}

	public void setComponentList(ArrayList<String> componentList) {
		this.componentList = componentList;
	}

	public void setComponentList() {
		if (componentsManifest != null && componentsManifest.size() > 0) {
			componentList = new ArrayList<String>();
			for (ComponentManifest comp : componentsManifest) {
				componentList.add(comp.getName());
			}
		}
	}

	public ComponentManifest getComponent(String compName) {
		ComponentManifest returnComp = null;
		for (ComponentManifest comp : componentsManifest) {
			if (comp.getName().equalsIgnoreCase(compName)) {
				returnComp = comp;
				break;
			}
		}
		return returnComp;
	}

}
