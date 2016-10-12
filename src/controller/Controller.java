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
import java.util.ArrayList;

import models.cfg.APK;

import org.apache.log4j.Logger;

import patternMatcher.attackreporter.AttackReporter;
import taintanalyzer.TaintAnalyzer;
import analyzer.Analyzer;
import configuration.Config;
import decompilation.APKDecompiler;

public class Controller {

	private static Logger logger = Logger.getLogger("TaintAnalyzer");
	
	public static void main(String args[]) {
		
		 File inputFile = new File(args[0]);
		 dirWalker(inputFile);
	}
	
	public static void dirWalker(File inputFile) {
		if (inputFile.isDirectory()) {
			for (File file : inputFile.listFiles()) {
				dirWalker(file);
			}
		} else {
			if (inputFile.getAbsolutePath().endsWith(".apk")) {
				APKBuilder apkBlrdr = new APKBuilder();
				APK apk = apkBlrdr.getAPKObject(inputFile);

				if (apk != null) {
					Config.getInstance().setApk(apk);
					Analyzer a = new TaintAnalyzer();
					apk.accept(a);
					reportAllUniqueWarnings();
				}
				logger.fatal("Analysis Finished!! ");
			}
		}
	}
	
	public static void reportAllUniqueWarnings(){
		Config.getInstance().resetDataForNewApp();
		AttackReporter.getInstance().resetAllExistingReports();
	}

}
