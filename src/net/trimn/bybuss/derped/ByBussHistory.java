package net.trimn.bybuss.derped;

import java.util.ArrayList;

import net.trimn.bybuss.DBHelper;
import net.trimn.bybuss.R;
import net.trimn.bybuss.R.id;
import net.trimn.bybuss.R.layout;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@Deprecated
public class ByBussHistory extends Activity {
	
	private DBHelper db;
	private Cursor cursor;
	private ListView list;
	private ArrayList<String> history_list;
	private ArrayAdapter<String> listAdapter;
	
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	
	public void onCreate(Bundle savedInstanceState) {

        
        // ===
        db 				= new DBHelper(this);
        cursor 				= db.getAllHistoryRows();
        
        
        if (cursor != null) {
        	cursor.moveToFirst();
            history_list = new ArrayList<String>();
            
            for (int i = 0; i < cursor.getCount(); i++) {
            	history_list.add(cursor.getString(1));
            	cursor.moveToNext();
            }
            
            
            list = (ListView) findViewById(R.id.history_list);
            listAdapter = new ArrayAdapter<String>(this, R.layout.history_listview, history_list);
            list.setAdapter(listAdapter);
            
            listAdapter.notifyDataSetChanged();

            list.setTextFilterEnabled(true);
//            list.setOnItemClickListener(new OnItemClickListener() {
//
//    			@Override
//    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//    				searchbar.setText(history_list.get(arg2));
//    				
//    				// utfør søk
//    				historyUsed = true;
//    				doSearch();
//    				
//    				tabs.setCurrentTab(0);
//    			}
//            	
//            });
            
            list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
					menu.setHeaderTitle("Redigér");
					menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Slett");					
				}
			});
            
            
            
        }
        
        
    }
}
