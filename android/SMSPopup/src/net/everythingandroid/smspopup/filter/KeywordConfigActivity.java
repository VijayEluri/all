package net.everythingandroid.smspopup.filter;

import net.everythingandroid.smspopup.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class KeywordConfigActivity extends Activity implements OnItemClickListener {

	private static final int MENU_ADD_ID = 1;
	private static final int MENU_DELETE_ID = 2;
	private FilterDbAdapter dbAdapter;
	private Cursor cursor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String keywords = prefs.getString(this.getString(R.string.pref_keywords_key), "");
		migrateKeywordsToDb(this, prefs, keywords);

		setContentView(R.layout.manage_filtered);

		dbAdapter = new FilterDbAdapter(this);
		dbAdapter.open(true);

		cursor = dbAdapter.fetchKeywords();
		startManagingCursor(cursor);

		ListAdapter adapter = new SimpleCursorAdapter(
			this,
			android.R.layout.simple_list_item_1,
			cursor,
			new String[] { FilterDbAdapter.KEY_WORD },
			new int[] { android.R.id.text1 });

		ListView lv = (ListView) findViewById(R.id.FilteredListView);
		registerForContextMenu(lv);

		lv.setOnItemClickListener(this);

		lv.setAdapter(adapter);
	}

	public void onItemClick(AdapterView<?> arg0, View lv, int arg2, long arg3) {
		lv.showContextMenu();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ADD_ID, 0, "Add").setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ADD_ID:
				addKeywords();
				return true;
		}
		return false;
	}

	private void addKeywords() {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		final EditText input = new EditText(this);
		alert.setTitle("Add Keywords");
		alert.setView(input);

		alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				String value = input.getText().toString().trim();
				dbAdapter.insertKeywords(value);
				cursor.requery();
				dialog.dismiss();
			}
		});

		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		alert.show();

	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.FilteredListView) {
			menu.setHeaderTitle("Keyword");
			menu.add(0, MENU_DELETE_ID, 0, "Delete");
//			menu.add(0, MENU_RESTORE_ID, 0, "Restore");
		}
	}

	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case MENU_DELETE_ID:
				deleteKeyword(info.id);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void deleteKeyword(long id) {
		dbAdapter.deleteKeyword(id);
		cursor.requery();
	}

	public void onDestroy() {
		dbAdapter.close();
		super.onDestroy();
	}

	private void migrateKeywordsToDb(Context context, SharedPreferences prefs, String keywords) {
		if (keywords.length() > 0) {
			FilterDbAdapter dbAdapter = new FilterDbAdapter(context);
			dbAdapter.open();
			dbAdapter.insertKeywords(keywords);
			dbAdapter.close();

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(context.getString(R.string.pref_keywords_key), "");
			editor.commit();
		}
	}
}
