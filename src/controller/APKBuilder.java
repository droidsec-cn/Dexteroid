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
 
package controller;

import java.io.File;

import models.cfg.APK;

import org.apache.log4j.Logger;

import decompilation.APKDecompiler;
import decompilation.ByteCodeParser;

import taintanalyzer.TaintAnalyzer;
import analyzer.Analyzer;

public class APKBuilder {
	
	private static Logger logger = Logger.getLogger("CFGBuilder");
	
	public APK getAPKObject( File inputFile){
		APKDecompiler decompiler = new APKDecompiler();
		APK apk=null;
		String inputFilePath = inputFile.getAbsolutePath();

		if(inputFilePath.endsWith(".apk")){
			decompiler.decompileAPK(inputFilePath); 
			
			logger.fatal("@@@@@  " + inputFilePath);
			ByteCodeParser op = new ByteCodeParser();
			apk = op.getAPKInfo(inputFilePath);
		}
		return apk;
	}

}
