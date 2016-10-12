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
 
package apihandlers.misc;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import symboltable.SymbolSpace;
import symboltable.SymbolTableEntry;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class Constants {
	//Doing lazy binding for this instance object.
	private static Constants instance = null;

	private static Hashtable audioSourceConstants;
	private static Hashtable outputFormatConstants;
	private static Hashtable audioEncoderConstants;
	private static Hashtable videoSourceConstants;
	private static Hashtable sensitiveDbUris;

	private Constants() {
		setAudioSourceConstants(loadAudioSourceConstants());
		setOutputFormatConstants(loadOutputFormatConstants());
		setAudioEncoderConstants(loadAudioEncoderConstants());
		setVideoSourceConstants(loadVideoSourceConstants());
		setSensitiveDbUris(loadSensitiveDbUris());
	}

	public static Constants getInstance() {
		if (null == instance) {
			synchronized (Constants.class) {
				if (instance == null) {
					instance = new Constants();
				}
			}
		}
		return instance;
	}

	//HashSet should have sufficed for this.
	private Hashtable loadSensitiveDbUris() {
		Hashtable ht = new Hashtable();

		//sms
		ht.put("'content://sms/inbox'", "");
		ht.put("content://sms/inbox", "");
		ht.put("'content://sms/conversations'", "");
		ht.put("content://sms/conversations", "");
		ht.put("'content://sms/draft'", "");
		ht.put("content://sms/draft", "");
		ht.put("'content://sms/outbox'", "");
		ht.put("content://sms/outbox", "");
		ht.put("'content://sms/sent'", "");
		ht.put("content://sms/sent", "");

		//contacts
		ht.put("Landroid/provider/ContactsContract$Data;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$StatusUpdates;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$RawContactsEntity;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$Groups;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Nickname;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Organization;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Photo;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Relation;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$Website;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$StructuredName;->CONTENT_URI", "");
		ht.put("Landroid/provider/ContactsContract$CommonDataKinds$StructuredPostal;->CONTENT_URI", "");
		//TODO ContactsContract.Contacts.CONTENT_LOOKUP_URI

		////Browser
		ht.put("content://com.android.browser/bookmarks", "");
		ht.put("content://com.android.browser/accounts", "");
		ht.put("content://com.android.browser/history", "");
		ht.put("content://com.android.browser/images", "");
		ht.put("content://com.android.browser/searches", "");
		ht.put("content://com.android.browser/syncstate", "");
		ht.put("content://com.android.browser/ image_mappings", "");
		ht.put("content://com.android.browser/combined", "");
		ht.put("content://com.android.browser/settings", "");

		////VoicemailContract
		ht.put("content://com.android.voicemail/voicemail", "");
		ht.put("content://com.android.voicemail/status", "");

		////UserDictionary
		ht.put("content://user_dictionary", "");
		ht.put("content://user_dictionary/words", "");

		////ContactsContract
		ht.put("content://com.android.contact", "");
		///directories”

		ht.put("content://com.android.contact", "");

		///syncstate”
		ht.put("content://com.android.contacts/profile/syncstate", "");
		ht.put("content://com.android.contact", "");

		///status_updates”
		ht.put("content://com.android.contacts/data/phones", "");
		ht.put("content://com.android.contacts/data/emails", "");
		ht.put("content://com.android.contacts/data/postals", "");
		ht.put("content://com.android.contacts/data/callables", "");
		ht.put("content://com.android.contacts/data/contactables", "");

		//CalendarContract
		ht.put("content://com.android.calendar", "");
		ht.put("content://com.android.calendar/calendar_entities", "");
		ht.put("content://com.android.calendar/calendars", "");
		ht.put("content://com.android.calendar/attendees", "");
		ht.put("content://com.android.calendar/event_entities", "");
		ht.put("content://com.android.calendar/events", "");
		ht.put("content://com.android.calendar/instances/when", "");
		ht.put("content://com.android.calendar/instances/groupbyday", "");
		ht.put("content://com.android.calendar/reminders", "");
		ht.put("content://com.android.calendar/calendar_alerts", "");
		ht.put("content://com.android.calendar/colors", "");
		ht.put("content://com.android.calendar/extendedproperties", "");
		ht.put("content://com.android.calendar/syncstate", "");

		//CallLog
		ht.put("content://call_log", "");
		ht.put("content://call_log/calls", "");

		//Contacts (deprecated interface but still can be used to access data)
		ht.put("content://contacts", "");
		ht.put("content://contacts/settings", "");
		ht.put("content://contacts/people", "");
		ht.put("content://contacts/groups", "");
		ht.put("content://contacts/phones", "");

		ht.put("content://contacts/groupmembership", "");
		ht.put("content://contacts/contact_methods", "");
		ht.put("content://contacts/presence", "");
		ht.put("content://contacts/organizations", "");
		ht.put("content://contacts/photos", "");
		ht.put("content://contacts/extensions", "");

		//Downloads
		ht.put("content://downloads/my_downloads", "");

		//Sms
		ht.put("content://sms", "");
		ht.put("content://sms/inbox", "");
		ht.put("content://sms/sent", "");
		ht.put("content://sms/draft", "");
		ht.put("content://sms/outbox", "");
		ht.put("content://sms/conversations", "");
		ht.put("content://mms-sms/conversations", "");
		ht.put("content://mms", "");
		ht.put("content://mms/inbox", "");
		ht.put("content://mms/sent", "");
		ht.put("content://mms/drafts", "");
		ht.put("content://mms/outbox", "");
		ht.put("content://mms/rate", "");
		ht.put("content://mms-sms", "");
		ht.put("content://mms-sms/pending", "");
		ht.put("content://telephony/carriers", "");
		ht.put("content://cellbroadcasts", "");

		//Settings
		ht.put("content://settings/system", "");
		ht.put("content://settings/secure", "");
		ht.put("content://settings/global", "");
		ht.put("content://settings/bookmarks", "");

		//contacts
		ht.put("'Landroid/provider/ContactsContract$Data;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$Contacts;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$StatusUpdates;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$RawContactsEntity;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$Groups;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Nickname;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Organization;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Photo;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Relation;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$Website;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$StructuredName;->CONTENT_URI'", "");
		ht.put("'Landroid/provider/ContactsContract$CommonDataKinds$StructuredPostal;->CONTENT_URI'", "");
		//TODO ContactsContract.Contacts.CONTENT_LOOKUP_URI

		////Browser
		ht.put("'content://com.android.browser/bookmarks'", "");
		ht.put("'content://com.android.browser/accounts'", "");
		ht.put("'content://com.android.browser/history'", "");
		ht.put("'content://com.android.browser/images'", "");
		ht.put("'content://com.android.browser/searches'", "");
		ht.put("'content://com.android.browser/syncstate'", "");
		ht.put("'content://com.android.browser/ image_mappings'", "");
		ht.put("'content://com.android.browser/combined'", "");
		ht.put("'content://com.android.browser/settings'", "");

		////VoicemailContract
		ht.put("'content://com.android.voicemail/voicemail'", "");
		ht.put("'content://com.android.voicemail/status'", "");

		////UserDictionary
		ht.put("'content://user_dictionary'", "");
		ht.put("'content://user_dictionary/words'", "");

		////ContactsContract
		ht.put("'content://com.android.contact'", "");
		ht.put("'content://com.android.contacts/profile/syncstate'", "");

		///status_updates”
		ht.put("'content://com.android.contacts/data/phones'", "");
		ht.put("'content://com.android.contacts/data/emails'", "");
		ht.put("'content://com.android.contacts/data/postals'", "");
		ht.put("'content://com.android.contacts/data/callables'", "");
		ht.put("'content://com.android.contacts/data/contactables'", "");

		//CalendarContract
		ht.put("'content://com.android.calendar'", "");
		ht.put("'content://com.android.calendar/calendar_entities'", "");
		ht.put("'content://com.android.calendar/calendars'", "");
		ht.put("'content://com.android.calendar/attendees'", "");
		ht.put("'content://com.android.calendar/event_entities'", "");
		ht.put("'content://com.android.calendar/events'", "");
		ht.put("'content://com.android.calendar/instances/when'", "");
		ht.put("'content://com.android.calendar/instances/groupbyday'", "");
		ht.put("'content://com.android.calendar/reminders'", "");
		ht.put("'content://com.android.calendar/calendar_alerts'", "");
		ht.put("'content://com.android.calendar/colors'", "");
		ht.put("'content://com.android.calendar/extendedproperties'", "");
		ht.put("'content://com.android.calendar/syncstate'", "");

		//CallLog
		ht.put("'content://call_log'", "");
		ht.put("'content://call_log/calls'", "");

		//Contacts (deprecated interface but still can be used to access data)
		ht.put("'content://contacts'", "");
		ht.put("'content://contacts/settings'", "");
		ht.put("'content://contacts/people'", "");
		ht.put("'content://contacts/groups'", "");
		ht.put("'content://contacts/phones'", "");
		ht.put("'content://contacts/groupmembership'", "");
		ht.put("'content://contacts/contact_methods'", "");
		ht.put("'content://contacts/presence'", "");
		ht.put("'content://contacts/organizations'", "");
		ht.put("'content://contacts/photos'", "");
		ht.put("'content://contacts/extensions'", "");

		//Downloads
		ht.put("'content://downloads/my_downloads'", "");

		ht.put("'content://sms'", "");
		ht.put("'content://sms/inbox'", "");
		ht.put("'content://sms/sent'", "");
		ht.put("'content://sms/draft'", "");
		ht.put("'content://sms/outbox'", "");
		ht.put("'content://sms/conversations'", "");

		ht.put("'content://mms-sms/conversations'", "");
		ht.put("'content://mms'", "");
		ht.put("'content://mms/inbox'", "");
		ht.put("'content://mms/sent'", "");
		ht.put("'content://mms/drafts'", "");
		ht.put("'content://mms/outbox'", "");
		ht.put("'content://mms/rate'", "");
		ht.put("'content://mms-sms'", "");
		ht.put("'content://mms-sms/pending'", "");

		ht.put("'content://telephony/carriers'", "");
		ht.put("'content://cellbroadcasts'", "");

		//Settings
		ht.put("'content://settings/system'", "");
		ht.put("'content://settings/secure'", "");
		ht.put("'content://settings/global'", "");
		ht.put("'content://settings/bookmarks'", "");

		return ht;
	}

	private Hashtable loadAudioSourceConstants() {
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();
		ht.put("0", "DEFAULT");
		ht.put("1", "MIC");
		ht.put("2", "VOICE_UPLINK");
		ht.put("3", "VOICE_DOWNLINK");
		ht.put("4", "VOICE_CALL");
		ht.put("5", "CAMCORDER");
		ht.put("6", "VOICE_RECOGNITION");
		ht.put("7", "VOICE_COMMUNICATION");
		ht.put("8", "REMOTE_SUBMIX");

		return ht;
	}

	private Hashtable loadVideoSourceConstants() {
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();
		ht.put("0", "DEFAULT");
		ht.put("1", "CAMERA");
		ht.put("2", "SURFACE");
		return ht;
	}

	private Hashtable loadOutputFormatConstants() {
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();
		ht.put("0", "DEFAULT");
		ht.put("1", "3GPP");
		ht.put("2", "MPEG4");
		ht.put("3", "AMR_NB");
		ht.put("4", "AMR_WB");
		ht.put("6", "AAC_ADTS");

		return ht;
	}

	private Hashtable loadAudioEncoderConstants() {
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();
		ht.put("0", "DEFAULT");
		ht.put("1", "AMR_NB");
		ht.put("2", "AMR_WB");
		ht.put("3", "AAC");
		ht.put("4", "HE_AAC");
		ht.put("5", "AAC_ELD");

		return ht;
	}

	private Hashtable loadContactsContacts() {
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();

		//Most of places, developers don't know the index value, so they first getColumnIndex(), so I won't 
		// handle the case where getString(0) is called with integer-const value given by the developer.

		return ht;
	}

	public Hashtable getAudioSourceConstants() {
		return audioSourceConstants;
	}

	private void setAudioSourceConstants(Hashtable audioSourceConstants) {
		Constants.audioSourceConstants = audioSourceConstants;
	}

	public Hashtable getOutputFormatConstants() {
		return outputFormatConstants;
	}

	private void setOutputFormatConstants(Hashtable outputFormatConstants) {
		Constants.outputFormatConstants = outputFormatConstants;
	}

	public Hashtable getAudioEncoderConstants() {
		return audioEncoderConstants;
	}

	private void setAudioEncoderConstants(Hashtable audioEncoderConstants) {
		Constants.audioEncoderConstants = audioEncoderConstants;
	}

	public Hashtable getSensitiveDbUris() {
		return sensitiveDbUris;
	}

	public static void setSensitiveDbUris(Hashtable sensitiveDbUris) {
		Constants.sensitiveDbUris = sensitiveDbUris;
	}

	public static Hashtable getVideoSourceConstants() {
		return videoSourceConstants;
	}

	public static void setVideoSourceConstants(Hashtable videoSourceConstants) {
		Constants.videoSourceConstants = videoSourceConstants;
	}

}
