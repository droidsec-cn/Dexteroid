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

import java.io.File;

import configuration.Config;

public class APKDecompiler {

	private String androguardPath = "H:\\AndroguardOutput\\androguard-master\\androguard-master";
	
	public void decompileAPK(String inputFile){
		//Generate Dalvik Byte Code
		runAndroguard(inputFile, ".txt", "cfgAndroFile.py");
		runAndroguard(inputFile, ".xml", "androaxml.py");

		if(Config.getInstance().isResourceHandling()){
			decompileResourceXMLFiles(inputFile);
		}
	}
	
	public void runAndroguard(String inputFile, String outputFileExtension, String scriptName){
		String outputFile = inputFile.concat(outputFileExtension);
		String scriptPath = androguardPath.concat("\\").concat(scriptName);
		
		if(!new File(outputFile).exists()){
			Process p;
			ProcessBuilder pb=new ProcessBuilder();
			pb.command("python", scriptPath, "-i", inputFile , "-o", outputFile);
	
			try{
				p=pb.start();
				p.waitFor(); // wait for process finishes
			}catch (Exception e){
				e.printStackTrace();
			}
		 }
	}
	
	public void decompileResourceXMLFiles(String inputFile){
		String[] split1 = inputFile.split("[\\\\]"); //   */*/abc.apk
		String dirName = split1[split1.length-1];
		dirName = dirName.substring(0, dirName.length()-3); // get "abc" here and create that folder.
		
		String outputPath = "";
		for(int i=0; i < split1.length-1; i++){
			outputPath += split1[i];
			outputPath += "\\";
		}
		outputPath += dirName;
		File outputDir = new File(outputPath);
		if(!outputDir.exists())
			outputDir.mkdir();
		
		String outputFile = outputPath;
		String scriptPath = androguardPath.concat("androaxmlresrc.py");
		Process p;
		ProcessBuilder pb=new ProcessBuilder();
		pb.command("python", scriptPath, "-i", inputFile , "-o", outputFile); // here output is output directory.
	
		try{
			p=pb.start();
			p.waitFor(); // wait for process finishes
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public String getAndroguardPath() {
		return androguardPath;
	}

	public void setAndroguardPath(String androguardPath) {
		this.androguardPath = androguardPath;
	}

}
