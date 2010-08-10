package net.everythingandroid.smspopup.filter;

import net.everythingandroid.smspopup.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FilteredManagementActivity extends Activity
{

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_filtered);

		ListView lv = (ListView) findViewById(R.id.FilteredListView);
		String lv_arr[] = { "Android", "iPhone", "BlackBerry", "AndroidPeople" };
		
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr));
	}
}
