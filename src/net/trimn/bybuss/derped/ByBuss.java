package net.trimn.bybuss.derped;
///**
// * 
// * ByBuss
// * Android-applikasjon for spørring mot Bussorakelet i Trondheim
// * 
// * Bussorakelet er en tjeneste hentet hos AtB ( http://atb.no/ )
// * 
// * @author Tri M. Nguyen
// */
//
//package net.trimn.bybuss.deprped;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.trimn.bybuss.AtbBussorakel;
//import net.trimn.bybuss.ByBussOverlay;
//import net.trimn.bybuss.ByBussTabs;
//import net.trimn.bybuss.DBHelper;
//import net.trimn.bybuss.Holdeplass;
//import net.trimn.bybuss.R;
//import net.trimn.bybuss.XmlParser;
//import net.trimn.bybuss.R.drawable;
//import net.trimn.bybuss.R.id;
//import net.trimn.bybuss.R.layout;
//import net.trimn.bybuss.R.menu;
//import net.trimn.bybuss.R.raw;
//import net.trimn.bybuss.R.string;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.drawable.Drawable;
//import android.net.ConnectivityManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.ContextMenu;
//import android.view.ContextMenu.ContextMenuInfo;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnCreateContextMenuListener;
//import android.view.View.OnKeyListener;
//import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.AdapterContextMenuInfo;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TabHost;
//import android.widget.TabHost.OnTabChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;
//
//
//public class ByBuss extends MapActivity {
//    /** Called when the activity is first created. */
//	private AtbBussorakel buss;
//	private EditText searchbar;
//	private TextView answerField;
//	private Button askButton;
//	private InputMethodManager imm;
//	private DBHelper db;
//	private Cursor c;
//	private ListView list;
//	private ArrayAdapter<String> listAdapter;
//	private ArrayList<String> history_list;
//	private boolean historyUsed;
//	
//	protected static final int CONTEXTMENU_DELETEITEM = 0;
//	
//	private MapView mapView;
//	private List<Overlay> mapOverlays;
//	private ByBussOverlay itemOverlay;
//	private Drawable drawable;
//	
////	private BasicMapComponent mapComponent;
//	
//	private XmlParser parseHoldeplasser;
//	private ArrayList<Holdeplass> holdeplass;
//	
//	private String reiseFra;
//	private String reiseTil;
//	
////	private Place[] marker;
//	
//	
//	
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        // request Custom title bar
//        requestCustomTitleBar();
//        setContentView(R.layout.tabhost);
//        
//        setCustomTitle("ByBuss - busstider i Trondheim");
//        
//        // Resources res = getResources();
//        
//        
//        
//        
//        
//        // =====================================================================
//        // Tabbed stuff
//        // =====================================================================
//        
//        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
//        
//        tabs.setup();
//
//        ByBussTabs tabBar = new ByBussTabs(tabs, getResources());
//        
//        tabBar.addTab(getString(R.string.tab_bussorakel), R.drawable.ic_tab_bussorakel, R.id.tab1);
//        tabBar.addTab(getString(R.string.tab_history), R.drawable.ic_tab_history, R.id.tab2);
//        tabBar.addTab(getString(R.string.tab_map), R.drawable.ic_tab_history, R.id.tab4);
//        
//        
//        tabs.setOnTabChangedListener(new OnTabChangeListener() {
//			@Override
//			public void onTabChanged(String tabId) {
//				imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
//			}
//        });
//
//        
//        
//        
//        
//        // =====================================================================
//        // History List fillup
//        // =====================================================================
//        
//        
//        db 				= new DBHelper(this);
//        c 				= db.getAllHistoryRows();
//        historyUsed 	= false;
//        
//        if (c != null) {
//        	c.moveToFirst();
//            history_list = new ArrayList<String>();
//            
//            for (int i = 0; i < c.getCount(); i++) {
//            	history_list.add(c.getString(1));
//            	c.moveToNext();
//            }
//            
//            
//            list = (ListView) findViewById(R.id.history_list);
//            listAdapter = new ArrayAdapter<String>(this, R.layout.history_listview, history_list);
//            list.setAdapter(listAdapter);
//            
//            listAdapter.notifyDataSetChanged();
//
//            list.setTextFilterEnabled(true);
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
//            
//            list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//				
//				@Override
//				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//					menu.setHeaderTitle("Redigér");
//					menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Slett");					
//				}
//			});
//            
//            
//            
//        }
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        // =====================================================================
//        // Searchbar
//        // =====================================================================
//
//        buss = new AtbBussorakel();
//        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        
//        searchbar 		= (EditText)	findViewById(R.id.entry);
//        askButton 		= (Button)		findViewById(R.id.send);
//        answerField 	= (TextView)	findViewById(R.id.answer);
//        
//        searchbar.setMaxWidth(searchbar.getWidth());
//
//        searchbar.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (searchbar.getText().toString().equals(getString(R.string.search_field))) {
//					searchbar.setText("");
//				}
//			}
//			
//		});
//        
//        
//        searchbar.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//					doSearch();
//				}
//				return false;
//			}
//			
//        });        
//        
//        
//        askButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				doSearch();
//			}
//			
//		});
//
//       
//
//        
//        
//        
//        
//        // =====================================================================
//        // kart
//        // =====================================================================
//        
//		mapView 			= (MapView) findViewById(R.id.mapView);
//		mapOverlays 		= mapView.getOverlays();
//		
//		drawable 			= this.getResources().getDrawable(R.drawable.gps_marker);
//		itemOverlay 		= new ByBussOverlay(drawable, this, searchbar);
//		
//		// last inn alle busholdeplasser
//		new mapFillLoadThread().execute();
//
//        
//    }
//    
//    
//    
//    
//    
//    
//    
//    // =====================================================================
//    // Context menu
//    // =====================================================================
//    
//    @Override
//	public boolean onContextItemSelected(MenuItem item) {
//    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//    	switch (item.getItemId()) {
//    	case CONTEXTMENU_DELETEITEM:
//    		if (db.deleteHistoryRow(db.getHistoryItemId(list.getItemAtPosition((int)info.id).toString())) > 0) {
//    			Toast.makeText(getApplicationContext(), "Slettet", Toast.LENGTH_SHORT).show();    	
//    			new ListUpdateThread(2, (int)info.id).execute();
//    		} else {
//    			Toast.makeText(getApplicationContext(), "En feil oppstod. Du kan rapportere feilen ved å sende mail til mail@trimn.net. Takk!", Toast.LENGTH_LONG).show(); 
//    		}
//    		
//    		return true;
////    		case R.id.edit:
////    			editNote(info.id);
////    			return true;
////    		case R.id.delete:
////    			deleteNote(info.id);
////    			return true;
//    		default:
//    			return super.onContextItemSelected(item);
//      }
//    }
//    
//    // ============= END Context menu stuff
//    
//    public boolean isOnline() {
//    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//    	
//    	if ( cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ) {
//    		return true;
//    	}
//    	else if ( cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||  cm.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED   ) {
//    		//text.setText("Look your not online");           
//    	}
//    	
////    	if (cm.getAllNetworkInfo() != null && cm.getAllNetworkInfo(). && cm.getAllNetworkInfo().isConnected()) {
////    		return true;
////    	}
//    	return false;
//    }
//
//    public void doSearch() {
//
//    		answerField.setText("Venter på svar fra bussorakelet...");
//    		
//    		if (searchbar.getText().length() <= 0) {
//    			answerField.setText("Søkefeltet er tomt.");
//    		} else {
//    			imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
//    			buss.setQuestion(searchbar.getText().toString().trim());
//    			
//    			new AtbThreadTest().execute();
//    		}
//    }
//    
//
//    
//    
//    
//    /**
//     * Create custom Title bar
//     */
//    protected void requestCustomTitleBar() {
//    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//	}
//    
//    //set custom title bar
//    protected void setCustomTitle(String msg) {
//	    
//	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
//	
//	    TextView tv = (TextView) getWindow().findViewById(R.id.headerTitleTextVw);
//	    tv.setText(msg);
//    }
//    
//    
//    
//    
//    // Option Menu
//
//
//	public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
//	
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//        case R.id.quit:
//        	finish();
//            return true;
//        default:
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//
//
//
//
//	@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//    // =====================================================================
//    //
//    // THREADS
//    //
//    // =====================================================================
//    
//	/**
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
//			
//		
//    	
//    }
//    
//    /**
//     * Threading for search query
//     * 
//     * @author tmn
//     *
//     */
//    class AtbThreadTest extends AsyncTask<Void, Void, Void> {
//    	
//    	@Override
//    	protected Void doInBackground(Void... params) {
//    		buss.ask();
//    		return null;
//    	}
//    	
//    	@Override
//    	protected void onPostExecute(Void unused) {    		
//    		if (buss.getAnswer().trim().equals("No question supplied.")) {
//    			answerField.setText(getString(R.string.answer_field));
//    		} else {
//    			if (!history_list.contains(searchbar.getText().toString().trim()) && !historyUsed) {
//    				new ListUpdateThread(1, -1).execute();    				
//    			}
//    			answerField.setText(buss.getAnswer());
//    		}
//    		historyUsed = false;
//    	}
//    	
//    }
//    
//    
//    /**
//     * Thread for updating list
//     * 
//     * @author tmn
//     *
//     */
//    class ListUpdateThread extends AsyncTask<Void, Void, Void> {
//    	
//    	private int mode = -1;
//    	private int item = -1;
//    	
//    	public ListUpdateThread(int mode, int item) {
//			this.mode = mode;
//			this.item = item;
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			if (mode == 1) {
//				db.createHistoryItem(searchbar.getText().toString().trim(), buss.getAnswer());				
//			}
//			return null;
//		}
//		
//		@Override
//		protected void onPostExecute(Void unused) {
//			if (mode == 1) {
//				((ArrayAdapter<String>)listAdapter).add(searchbar.getText().toString().trim());				
//			} else if (mode == 2) {
//				((ArrayAdapter<String>)listAdapter).remove(history_list.get(item));
//			}
//		}
//    	
//    }
//    
//
//
//}
//
//
//

