package net.everythingandroid.smspopup.filter;

import java.util.List;

import net.everythingandroid.smspopup.R;
import net.everythingandroid.smspopup.SmsMmsMessage;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FilterService {

	private static boolean filterMessageByKeywords(SmsMmsMessage message, List<String> keywordList) {

		String body = message.getMessageBody();

		for (String word : keywordList) {
			if (body.indexOf(word) >= 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean filterMessage(SmsMmsMessage message, Context context) {
		if (message.getMessageType() != SmsMmsMessage.MESSAGE_TYPE_SMS) return false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		boolean enableFiltering = prefs.getBoolean(context.getString(R.string.pref_enable_filtering_key), true);
		if (!enableFiltering) return false;

		boolean ignoreContacts = prefs.getBoolean(context.getString(R.string.pref_bypass_contacts_key), true);
		if (ignoreContacts && (message.getContactId() != null)) return false;

		FilterDbAdapter dbAdapter = new FilterDbAdapter(context);
		dbAdapter.open();
		try {
			List<String> keywordList = dbAdapter.fetchKeywordsAsList();
			if (filterMessageByKeywords(message, keywordList)) {
				dbAdapter.createFilteredMessage(message);
				message.delete();
				return true;
			}
		} finally {
			dbAdapter.close();
		}

		return false;
	}


}
