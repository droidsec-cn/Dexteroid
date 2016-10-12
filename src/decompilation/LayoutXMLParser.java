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
 
package decompilation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.APK;
import models.cfg.BasicBlock;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.MethodSignature;
import models.cfg.Package;
import models.cfg.Parameter;

import org.apache.log4j.Logger;

public class LayoutXMLParser {

	private static Logger logger;
	private Hashtable layoutComponents;
	private Hashtable layoutComponentsByName;

	public LayoutXMLParser() {
		layoutComponents = new Hashtable();
		layoutComponentsByName = new Hashtable();
	}

	public void identifyLayoutComponents(String inputDirPath) {
		String layoutFile = inputDirPath.concat("\\layout.txt");
		getLayoutComponents(layoutFile);

		getCallbacksOfXMLFiles(inputDirPath);

		return;
	}

	//This method takes input file path and extracts layout-file components.
	public void getLayoutComponents(String layoutFilePath) {
		BufferedReader br;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(new File(layoutFilePath).getAbsolutePath()));
			try {

				line = br.readLine();
				while (line != null && !line.isEmpty()) {
					// <public type="layout" name="main" id="0x7f030000" />
					if (line.startsWith("<public")) {
						String[] split1 = line.split(" ");

						String[] nameSplit = split1[2].split("[=]");
						String[] idSplit = split1[3].split("[=]");
						String name = nameSplit[1];
						name = name.substring(1, name.length() - 1);
						String id = idSplit[1];
						id = id.substring(3, id.length() - 1);
						Integer decimalID = Integer.parseInt(id.trim(), 16);
						String decimdIDStr = String.valueOf(decimalID);

						Layout layout = new Layout();
						layout.setName(name);
						layout.setId(decimdIDStr);

						layoutComponents.put(decimdIDStr, layout);
						layoutComponentsByName.put(name, layout);
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// This automatically sets layoutComponents by IDs by setting layouts
	// obtained by name.
	public void getCallbacksOfXMLFiles(String xmlDirPath) {
		File xmlDirectory = new File(xmlDirPath);
		BufferedReader br;
		String line = "";

		for (File file : xmlDirectory.listFiles()) {
			try {
				br = new BufferedReader(new FileReader(file.getAbsolutePath()));
				try {
					line = br.readLine();
					while (line != null) {
						// android:onClick="sendMessage">
						if (line.contains("android:onClick")) {
							String[] preSplit = line.split(" ");
							for (int i = 0; i < preSplit.length; i++) {
								if (preSplit[i].trim().contains("android:onClick")) {
									line = preSplit[i];
									break;
								}
							}
							String[] split1 = line.split("[=]");

							String methodName = split1[1].trim();
							methodName = methodName.substring(1, methodName.length() - 1);
							String fileName = file.getName();
							fileName = fileName.substring(0, fileName.length() - 4);

							Layout layout = (Layout) layoutComponentsByName.get(fileName);
							layout.getMethods().add(methodName);
						} else if (line.contains("<include layout=")) {

							String[] split1 = line.split("[=]");
							String[] split2 = split1[1].split("[/]");

							if (split2.length == 2) {
								String layoutName = split2[1];
								String layoutFilePath = xmlDirPath.concat("\\").concat(layoutName);

								String fileName = file.getName();

								Layout layout = (Layout) layoutComponentsByName.get(fileName);

								try {
									BufferedReader br2 = new BufferedReader(new FileReader(new File(layoutFilePath).getAbsolutePath()));
									try {
										line = br2.readLine();
										while (line != null && !line.isEmpty()) {
											// android:onClick="sendMessage">
											if (line.contains("android:onClick")) {
												String[] preSplit = line.split(" ");
												for (int i = 0; i < preSplit.length; i++) {
													if (preSplit[i].trim().contains("android:onClick")) {
														line = preSplit[i];
														break;
													}
												}
												split1 = line.split("[=]");

												String methodName = split1[1].trim();
												methodName = methodName.substring(1, methodName.length());
												layout.getMethods().add(methodName);
											}
											line = br.readLine();
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
						line = br.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Hashtable getLayoutComponents() {
		return layoutComponents;
	}

	public void setLayoutComponents(Hashtable layoutComponents) {
		this.layoutComponents = layoutComponents;
	}

	public Hashtable getLayoutComponentsByName() {
		return layoutComponentsByName;
	}

	public void setLayoutComponentsByName(Hashtable layoutComponentsByName) {
		this.layoutComponentsByName = layoutComponentsByName;
	}

}
