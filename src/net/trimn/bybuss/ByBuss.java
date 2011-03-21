package net.trimn.bybuss;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity of the ByBuss application for Android
 * @author tmn
 */
public class ByBuss extends TabActivity {
	
	private AtbBussorakel buss;
	private EditText searchbar;
	private TextView answerField;
	private Button askButton;
	private InputMethodManager imm;
	private DBHelper db;
	private Cursor cursor;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> history_list;
	
	private ListView list;
	
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestCustomTitleBar();
		setContentView(R.layout.mainview);
		setCustomTitle("ByBuss - busstider i Trondheim");

		Resources res = getResources();
		TabHost tabHost = getTabHost();

		Tabs tabs = new Tabs(tabHost, res);
		tabs.addTab(getString(R.string.tab_bussorakel), R.drawable.ic_tab_bussorakel, R.id.tab_bussorakel);
		tabs.addTab(getString(R.string.tab_history), R.drawable.ic_tab_history, R.id.tab_history);
//		tabs.addTab(getString(R.string.tab_map), R.drawable.ic_tab_history, R.id.tab_map);
		
		tabHost.setCurrentTab(0);
		
		
		
		
		
		// Tab #1
		buss 				= new AtbBussorakel();
        imm 				= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        searchbar 			= (EditText)	findViewById(R.id.entry);
        askButton 			= (Button)		findViewById(R.id.send);
        answerField 		= (TextView)	findViewById(R.id.answer);
        
        searchbar.setMaxWidth(searchbar.getWidth());						// set max width of searchbar (dunno why itz hair)

        searchbar.setOnClickListener(new SearchbarClickListener());			// reset the search box if placeholder-text
        searchbar.setOnKeyListener(new SearchbarKeyListener());        		// when user submits with focus in searchbar
        askButton.setOnClickListener(new AskButtonClickListener());			// trigger search when search button is clicked
        searchbar.setOnFocusChangeListener(new OnChangeTab());				// hide keyboard on tab switch
		
        
        
        
        
        // Tab #2
        db 					= new DBHelper(this);
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
            
            list.setOnItemClickListener(new OnItemClicked(tabHost));
            list.setOnCreateContextMenuListener(new OnItemLongHold());
        }
        
		
		
	} // End onCreate
	
	
	

	
	/**
	 * 
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
    	case CONTEXTMENU_DELETEITEM:
    		if (db.deleteHistoryRow(db.getHistoryItemId(list.getItemAtPosition((int)info.id).toString())) > 0) {
    			Toast.makeText(getApplicationContext(), "Slettet", Toast.LENGTH_SHORT).show();    	
    			new ListUpdateThread(2, (int)info.id).execute();
    		} else {
    			Toast.makeText(getApplicationContext(), "En feil oppstod. Du kan rapportere feilen ved å sende mail til mail@trimn.net. Takk!", Toast.LENGTH_LONG).show(); 
    		}
    		
    		return true;
//    		case R.id.edit:
//    			editNote(info.id);
//    			return true;
//    		case R.id.delete:
//    			deleteNote(info.id);
//    			return true;
    		default:
    			return onContextItemSelected(item);
      }
    }
	

	
	protected void requestCustomTitleBar() {
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}
	
    protected void setCustomTitle(String msg) {
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
	    TextView tv = (TextView) getWindow().findViewById(R.id.headerTitleTextVw);
	    tv.setText(msg);
    }
	

	
	
	
	private void doSearch() {
    	answerField.setText("Venter på svar fra bussorakelet...");
    	
    	if (searchbar.getText().length() <= 0) {
    		answerField.setText("Søkefeltet er tomt.");
    	} else {
    		imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
    		buss.setQuestion(searchbar.getText().toString().trim());

    		new AtbThreadTest(this).execute();
    	}
    }
	
	
	
	/**
	 * Search the Database for String-match (search query vs database posts)
	 * @param context
	 * @param search
	 * @return boolean
	 */
	public static boolean isInDatabase(Context context, String search) {
		DBHelper db 		= new DBHelper(context);
		Cursor c 			= db.getAllHistoryRows();
		if (c != null) {
			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				if (c.getString(1).equals(search)) {
					return true;
				} 
				c.moveToNext();
			}
		}
		return false;
	}
	
	/**
	 * Insert the search query into the Database
	 * @param context
	 * @param search
	 * @param answer
	 */
	public static void insertIntoDb(Context context, String search, String answer) {
		DBHelper db = new DBHelper(context);
		db.createHistoryItem(search.trim(), answer);
	}
	
	
	
	
	private final class OnItemLongHold implements OnCreateContextMenuListener {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			menu.setHeaderTitle("Redigér");
			menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Slett");					
		}
	}

	private final class OnItemClicked implements OnItemClickListener {
		private final TabHost tabHost;

		private OnItemClicked(TabHost tabHost) {
			this.tabHost = tabHost;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			searchbar.setText(history_list.get(arg2));
			
			// utfør søk
			doSearch();
			
			tabHost.setCurrentTab(0);
		}
	}
	
	private final class AskButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			doSearch();
		}
	}

	private final class OnChangeTab implements View.OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
		    if (!hasFocus) {
		    	imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
		    }
		}
	}


	/**
	 * 
	 * @author tmn
	 *
	 */
	private final class SearchbarKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
				doSearch();
			}
			return false;
		}
	}

	/**
	 * 
	 * @author tmn
	 *
	 */
	private final class SearchbarClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (searchbar.getText().toString().equals(getString(R.string.search_field))) {
				searchbar.setText("");
			}
		}
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 * Playing with this Thread alike Async thingy
	 * 
	 */
	
	
	

	
	/**
     * Threading for search query
     * @author tmn
     */
    class AtbThreadTest extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public AtbThreadTest(Context context) {
			this.context = context;
		}
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		buss.ask();
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void unused) {    		
    		if (buss.getAnswer().trim().equals("No question supplied.")) {
    			answerField.setText(getString(R.string.answer_field));
    		} else {
    			if (!isInDatabase(context, searchbar.getText().toString().trim())) {
    				new ListUpdateThread(1, -1).execute();    				
    			}
    			answerField.setText(buss.getAnswer());
    		}
    	}
    	
    }
    
    /**
     * Thread for updating list
     * @author tmn
     */
    class ListUpdateThread extends AsyncTask<Void, Void, Void> {
    	
    	private int mode = -1;
    	private int item = -1;
    	
    	public ListUpdateThread(int mode, int item) {
			this.mode = mode;
			this.item = item;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mode == 1) {
				db.createHistoryItem(searchbar.getText().toString().trim(), buss.getAnswer());				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			if (mode == 1) {
				((ArrayAdapter<String>)listAdapter).add(searchbar.getText().toString().trim());				
			} else if (mode == 2) {
				((ArrayAdapter<String>)listAdapter).remove(history_list.get(item));
			}
		}
		
    }
    
    
    
    
    
    // not in use
    
//    /**
//	 * Thread for filling up map with bussholdeplasser
//	 */
//    class mapFillLoadThread extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			InputStream io 			= getResources().openRawResource(R.raw.buss);
//			parseHoldeplasser 		= new XmlParser(io);
//	    	holdeplass 				= parseHoldeplasser.getHoldeplasser();
//	    	
//	    	GeoPoint point = null;
//	    	
//	    	for (int i = 0; i < holdeplass.size(); i++) {
//	    		point = new GeoPoint((int)(holdeplass.get(i).getLat() * 1000000), (int)(holdeplass.get(i).getLon() * 1000000));
//	    		itemOverlay.addOverlayItem(new OverlayItem(point, holdeplass.get(i).getName() != null ? holdeplass.get(i).getName() : "...", "Test"));
//	    	}
//	    	
//	    	return null;
//		}
//		
//		@Override
//    	protected void onPostExecute(Void unused) {
//			mapOverlays.add(itemOverlay);
//    	}
			
		
    	
//    }
    
    
    
    
    
	
}
