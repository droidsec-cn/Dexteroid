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

import iterator.BBRPOIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import configuration.Config;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;

import analyzer.Analyzer;

public class BasicBlock extends CFGComponent implements Iterable<CFGComponent> {

	private ArrayList<String> predecessors; //store predecessor and successor keys. So these keys have to be unique.
	private ArrayList<String> successors;
	private String bbPosition = "";
	private boolean visited = false;
	private ArrayList<String> dominators;

	private Hashtable OUT;
	private Hashtable shadowCopyOfOut;
	private Hashtable shadowCopyOfGlobalSymTable;

	public BasicBlock() {
		compCollection = new ArrayList<CFGComponent>();
		predecessors = new ArrayList<String>();
		successors = new ArrayList<String>();
		setDominators(new ArrayList<String>());
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public CFGComponent getItem(int index) {
		return compCollection.get(index);
	}

	//	@Override
	public Iterator iterator() {
		//		Iterator iterator = instrList.iterator();
		return (Iterator) new BBRPOIterator(this);
	}

	public void accept(Analyzer a) {
		a.analyze(this);
	}

	public void setAnalayzeType() {

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Hashtable getOUT() {
		return OUT;
	}

	public void setOUT(Hashtable oUT) {
		OUT = oUT;
	}

	public ArrayList<String> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(ArrayList<String> predecessors) {
		this.predecessors = predecessors;
	}

	public ArrayList<String> getSuccessors() {
		return successors;
	}

	public void setSuccessors(ArrayList<String> successors) {
		this.successors = successors;
	}

	public String getBbPosition() {
		return bbPosition;
	}

	public void setBbPosition(String bbPosition) {
		this.bbPosition = bbPosition;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public ArrayList<String> getDominators() {
		return dominators;
	}

	public void setDominators(ArrayList<String> dominators) {
		this.dominators = dominators;
	}

	public Hashtable getShadowCopyOfOut() {
		return shadowCopyOfOut;
	}

	//This handles shadow-copy for symbolTables. This is useful in case when output of two basic block goes to one child and the child has to decide which
	// variables it wants to use.
	public void setShadowCopyOfOut(Hashtable originalOut) {
		if (originalOut != null) {
			Hashtable shadowCopy = new Hashtable();
			Enumeration<String> firstLevelKeys = originalOut.keys();

			while (firstLevelKeys.hasMoreElements()) {
				String fKey = firstLevelKeys.nextElement();
				SymbolTableEntry ent = (SymbolTableEntry) originalOut.get(fKey);

				if (!ent.getEntryDetails().isRecord()) {
					SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
					shadowCopy.put(newEnt.getName(), newEnt);
				} else {
					Hashtable myRecordFieldList = ent.getEntryDetails().getRecordFieldList();

					if (myRecordFieldList != null) {
						Enumeration<String> secondLevelKeys = myRecordFieldList.keys();
						SymbolTableEntry secondLevelEnt = new SymbolTableEntry();
						secondLevelEnt.setName(ent.getName());
						Hashtable recordFieldList = new Hashtable();
						while (secondLevelKeys.hasMoreElements()) {
							String sKey = secondLevelKeys.nextElement();
							SymbolTableEntry sEnt = (SymbolTableEntry) ent.getEntryDetails().getRecordFieldList().get(sKey);

							SymbolTableEntry newSEnt = new SymbolTableEntry(sEnt); //it makes first level deep copy
							recordFieldList.put(newSEnt.getName(), newSEnt);
						}
						secondLevelEnt.getEntryDetails().setRecord(true);
						secondLevelEnt.getEntryDetails().setRecordFieldList(recordFieldList);
						shadowCopy.put(secondLevelEnt.getName(), secondLevelEnt);
					} else {
						//rare case
						SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
						shadowCopy.put(newEnt.getName(), newEnt);
					}
				}
			}
			this.shadowCopyOfOut = shadowCopy;
		} else
			this.shadowCopyOfOut = originalOut;

	}

	public Hashtable getShadowCopyOfGlobalSymTable() {
		return shadowCopyOfGlobalSymTable;
	}

	public void setShadowCopyOfGlobalSymTable() {
		SymbolSpace globalSymCopy = Config.getInstance().getGlobalSymbolSpace();
		Hashtable originalSymTable = (Hashtable) globalSymCopy.getEntries().get(0);

		Hashtable deepCopy = new Hashtable();
		Enumeration<String> firstLevelKeys = originalSymTable.keys();
		while (firstLevelKeys.hasMoreElements()) {
			String fKey = firstLevelKeys.nextElement();
			SymbolTableEntry ent = (SymbolTableEntry) originalSymTable.get(fKey);

			SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
			deepCopy.put(newEnt.getName(), newEnt);
		}
		this.shadowCopyOfGlobalSymTable = deepCopy;
	}

	public void setNulltoShadowCopyOfGlobalSymTable() {
		//Just make deep copy of objects
		this.shadowCopyOfGlobalSymTable = null;
	}

}
