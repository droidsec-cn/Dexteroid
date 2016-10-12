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

public class AndroidManifest {

	private ArrayList<Permission> usedPermList;
	private ArrayList<String> usedPermStrList;
	private Application application;
	private String packageName = "";

	public AndroidManifest() {
		usedPermList = new ArrayList<Permission>();
		usedPermStrList = new ArrayList<String>();
		application = new Application();
	}

	public void setUsedPermAsStrings() {
		if (usedPermList.size() > 0) {
			for (Permission p : usedPermList) {
				String pName = p.getName();
				usedPermStrList.add(pName);
			}
		}
	}

	public void printAllComponents() {
		ComponentManifest resultComponent = null;
		ArrayList<ComponentManifest> componentsManifestList = application.getComponentsManifest();

		if (componentsManifestList != null) {
			for (ComponentManifest comp : componentsManifestList) {
				System.out.println(comp.getName() + " <==> ");
			}
		}
	}

	//Ideally, this should be in Application.java class.
	public ComponentManifest findComponentManifest(String qualifiedCompName) {
		ComponentManifest resultComponent = null;
		ArrayList<ComponentManifest> componentsManifest = application.getComponentsManifest();

		if (qualifiedCompName.endsWith(";"))
			qualifiedCompName = qualifiedCompName.substring(0, qualifiedCompName.length() - 1);

		for (ComponentManifest comp : componentsManifest) {
			if (comp.getName().equalsIgnoreCase(qualifiedCompName)) {
				resultComponent = comp;
				break;
			}
		}
		return resultComponent;
	}

	public ArrayList<Permission> getUsedPermList() {
		return usedPermList;
	}

	public void setUsedPermList(ArrayList<Permission> usedPermList) {
		this.usedPermList = usedPermList;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ArrayList<String> getUsedPermStrList() {
		return usedPermStrList;
	}

	public void setUsedPermStrList(ArrayList<String> usedPermStrList) {
		this.usedPermStrList = usedPermStrList;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
