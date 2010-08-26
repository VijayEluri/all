package net.everythingandroid.smspopup.filter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.everythingandroid.smspopup.R;
import net.everythingandroid.smspopup.SmsMmsMessage;

public class FilterService {

	private static boolean filterMessageByKeywords(SmsMmsMessage message, String keywords) {
		ArrayList<String> words = new ArrayList<String>();
		for (String line : keywords.split("\n")) {
			for (String word : line.split(" |,|;")) {
				word = word.trim();
				if (word.length() > 0)
					words.add(word);
			}
		}

		String body = message.getMessageBody();

		for (String word : words) {
			if (body.indexOf(word) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean filterMessage(SmsMmsMessage message, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		String keywords = prefs.getString(context.getString(R.string.pref_keywords_key), "");
		boolean ignoreContacts = prefs.getBoolean(context.getString(R.string.pref_bypass_contacts_key), true);
		boolean enableFiltering = prefs.getBoolean(context.getString(R.string.pref_enable_filtering_key), true);
		
		if (enableFiltering) {
			if (message.getMessageType() == SmsMmsMessage.MESSAGE_TYPE_SMS) {
				if (ignoreContacts && (message.getContactId() != null)) {
					// ignore if we can find contact id
				} else {
					if (filterMessageByKeywords(message, keywords)) {
						FilterDbAdapter dbAdapter = new FilterDbAdapter(context);
						dbAdapter.open();
						dbAdapter.createFilteredMessage(message);
						dbAdapter.close();
						message.delete();
						return true;
					}
				}
			}
		}
		return false;
	}

}
