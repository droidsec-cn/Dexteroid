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

import handler.BaseHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.log4j.Logger;

import analyzer.Analyzer;
import configuration.Config;
import enums.ApiTypesBySyntax;

public class Instruction extends CFGComponent {

	private Instruction prevInstr;
	private String typeBySyntax;
	private String text;
	private String address;
	private InstructionResponse instResponse;

	private static Logger logger;

	public Instruction() {
		logger = Logger.getLogger(Instruction.class);
	}

	public enum API_TYPES {
		userDefinedCallerAPI, taintPropagater, dumbAPI, source, storeVariable, commonInitializer, sink
	}

	public void accept(Analyzer a) {
		a.analyze(this);
	}

	public Instruction getPrevInstr() {
		return prevInstr;
	}

	public void setPrevInstr(Instruction prevInstr) {
		this.prevInstr = prevInstr;
	}

	@SuppressWarnings("unchecked")
	public InstructionResponse instructionHandler(Instruction prev) {
		InstructionResponse ir = null;
		BaseHandler handler = null;
		Class cls = null;
		setCurrentInstrData();
		String instType = this.getTypeBySyntax();
		Properties instrHandlers = Config.getInstance().getInstructionHandlersMap();
		if (instrHandlers.containsKey(instType)) {

			String handlerName = instrHandlers.getProperty(instType);
			String completeHandlerName = new StringBuilder("handler.").append(handlerName).toString();
			logger.info("text-> " + this.getText());
			logger.info("Instruction.java -> completeHandlerName " + completeHandlerName);
			try {
				cls = Class.forName(completeHandlerName);
				handler = (BaseHandler) cls.getDeclaredConstructor(new Class[] { Instruction.class, Instruction.class }).newInstance(this,
						prev);
				instResponse = handler.execute();
			} catch (ClassNotFoundException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				//				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return instResponse;
	}

	private void setCurrentInstrData() {
		String api = "api";
		String lineSplit[] = this.text.split(" ");
		this.address = lineSplit[0];
		this.typeBySyntax = lineSplit[1]; // move-result, invoke-virtual
	}

	/*
	 * #Landroid/content/Intent;-><init> = commonInitializer
	 * #Landroid/content/BroadcastReceiver;-><init> = commonInitializer
	 * #Landroid/content/IntentFilter;-><init> = commonInitializer
	 * #Landroid/content/ContentValues;-><init> = commonInitializer
	 * #Landroid/os/Handler;-><init> = commonInitializer
	 * #Landroid/os/HandlerThread;-><init> = commonInitializer
	 * #Ljava/lang/Long;-><init> = commonInitializer
	 * #Lorg/apache/http/HttpHost;-><init> = commonInitializer
	 */
	public String getCalledMethodType(String callerAPI) {
		String methodType = "";

		if (callerAPI == null || callerAPI.isEmpty() || !callerAPI.contains("/"))
			return methodType;
		String[] apiNameSplits = callerAPI.split("[/]");
		String apiName = apiNameSplits[0];
		String apiName2 = apiNameSplits[1];

		if (apiName.equalsIgnoreCase("Landroid") || (apiName.equalsIgnoreCase("Lcom") && apiName2.equalsIgnoreCase("android"))) {
			methodType = ApiTypesBySyntax.android.toString();
		} else if (apiName.equalsIgnoreCase("Ldalvik")) {
			methodType = ApiTypesBySyntax.dalvik.toString();
		} else if (apiName.equalsIgnoreCase("Ljava")) {
			methodType = ApiTypesBySyntax.java.toString();
		} else if (apiName.equalsIgnoreCase("Ljavax") || apiName.equalsIgnoreCase("Ljunit")
				|| (apiNameSplits[0].equalsIgnoreCase("Lorg") && apiNameSplits[1].equalsIgnoreCase("apache"))
				|| (apiNameSplits[0].equalsIgnoreCase("Lorg") && apiNameSplits[1].equalsIgnoreCase("json"))
				|| (apiNameSplits[0].equalsIgnoreCase("Lorg") && apiNameSplits[1].equalsIgnoreCase("w3c"))
				|| (apiNameSplits[0].equalsIgnoreCase("Lorg") && apiNameSplits[1].equalsIgnoreCase("xml"))
				|| (apiNameSplits[0].equalsIgnoreCase("Lorg") && apiNameSplits[1].equalsIgnoreCase("xmlpull"))) {
			methodType = ApiTypesBySyntax.otherlibrary.toString();
		} else {
			methodType = ApiTypesBySyntax.userdefined.toString();
		}

		return methodType;
	}

	public String getTypeBySyntax() {
		return typeBySyntax;
	}

	public void setTypeBySyntax(String typeBySyntax) {
		this.typeBySyntax = typeBySyntax;
	}

	public String getText() {
		return text;
	}

	public void setText(String instrText) {
		this.text = instrText;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public InstructionResponse getInstResponse() {
		return instResponse;
	}

	public void setInstResponse(InstructionResponse ir) {
		this.instResponse = ir;
	}

}
