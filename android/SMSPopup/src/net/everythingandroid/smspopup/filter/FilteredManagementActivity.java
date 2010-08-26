package net.everythingandroid.smspopup.filter;

import net.everythingandroid.smspopup.Log;
import net.everythingandroid.smspopup.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FilteredManagementActivity extends Activity implements OnItemClickListener
{

	private static final int MENU_REFRESH_ID = 1;
	private static final int MENU_CLEARALL_ID = 2;
	private static final int MENU_RESTORE_ID = 3;
	private static final int MENU_DELETE_ID = 4;

	private FilterDbAdapter dbAdapter;
	private Cursor cursor;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_filtered);

		dbAdapter = new FilterDbAdapter(this);
		dbAdapter.open(true);

		cursor = dbAdapter.fetchAllFilteredMessage();
		startManagingCursor(cursor);

		ListAdapter adapter = new SimpleCursorAdapter(
			this,
			android.R.layout.simple_list_item_2,
			cursor,
			new String[] { FilterDbAdapter.KEY_NUMBER, FilterDbAdapter.KEY_BODY },
			new int[] { android.R.id.text1, android.R.id.text2 });

		ListView lv = (ListView) findViewById(R.id.FilteredListView);
		registerForContextMenu(lv);

		lv.setOnItemClickListener(this);

		lv.setAdapter(adapter);
	}

	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
	{
		FilteredMessage msg = dbAdapter.findFilteredMessageById(id);
		if (Log.DEBUG)
		{
			Log.v(msg.getNumber());
			Log.v(msg.getFormatedReceived());
			Log.v(msg.getBody());
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Message Detail");
		alertDialog.setMessage("From: " + msg.getNumber() + "\n" + 
							   "Date: " + msg.getFormatedReceived() + "\n" + 
							   "\n" + msg.getBody());
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		alertDialog.show();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, MENU_REFRESH_ID, 0, "Refresh").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, MENU_CLEARALL_ID, 0, "Clear all").setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case MENU_REFRESH_ID:
				refreshData();
				return true;
			case MENU_CLEARALL_ID:
				clearAll();
				return true;
		}
		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.FilteredListView) {
			menu.setHeaderTitle("Filtered SMS");
			menu.add(0, MENU_DELETE_ID, 0, "Delete");
//			menu.add(0, MENU_RESTORE_ID, 0, "Restore");
		}
	}

	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case MENU_DELETE_ID:
				deleteMessage(info.id);
				return true;
			case MENU_RESTORE_ID:
				restoreMessage(info.id);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void deleteMessage(long id)
	{
		if (Log.DEBUG)
			Log.v("Deleting message id " + id);
		dbAdapter.deleteFilteredMessage(id);
		cursor.requery();
	}

	private void restoreMessage(long id)
	{
		if (Log.DEBUG)
			Log.v("Restoring message id " + id);		
	}

	private void clearAll()
	{
		dbAdapter.deleteAllFilteredMessage();
		cursor.requery();
	}

	private void refreshData()
	{
		cursor.requery();
	}

	public void onDestroy()
	{
		dbAdapter.close();
		super.onDestroy();
	}
}
