package net.trimn.bybuss.derped;

import java.util.ArrayList;

import net.trimn.bybuss.AtbBussorakel;
import net.trimn.bybuss.DBHelper;
import net.trimn.bybuss.R;
import net.trimn.bybuss.R.id;
import net.trimn.bybuss.R.layout;
import net.trimn.bybuss.R.string;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ByBussBussorakel extends Activity  {
	
	
	private AtbBussorakel buss;
	private EditText searchbar;
	private TextView answerField;
	private Button askButton;
	private InputMethodManager imm;
	private DBHelper db;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> history_list;
	
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	
	
	public void hideLol() {
		imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bussorakel);
        
        buss 			= new AtbBussorakel();
        imm 			= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        searchbar 		= (EditText)	findViewById(R.id.entry);
        askButton 		= (Button)		findViewById(R.id.send);
        answerField 	= (TextView)	findViewById(R.id.answer);
        
        searchbar.setMaxWidth(searchbar.getWidth());

        searchbar.setOnClickListener(new SearchbarClickListener());
        searchbar.setOnKeyListener(new SearchbarKeyListener());        
        askButton.setOnClickListener(new AskButtonClickListener());
        
        
        searchbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideLol();
                }
            }
        });
        
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bundle = this.getIntent().getExtras();
		String search = bundle.getString("search");
		searchbar.setText(search);
		doSearch();
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
	 * Search the DB for the same String
	 * @param context
	 * @param search
	 * @return
	 */
	public static boolean isInDatabase(Context context, String search) {
		DBHelper db = new DBHelper(context);
		Cursor c = db.getAllHistoryRows();  
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
	
	public static void insertIntoDb(Context context, String search, String answer) {
		DBHelper db = new DBHelper(context);
		db.createHistoryItem(search.trim(), answer);
	}
	
	
	
	/**
	 * 
	 * @author tmn
	 *
	 */
	private final class AskButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			doSearch();
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
	private final class SearchbarClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (searchbar.getText().toString().equals(getString(R.string.search_field))) {
				searchbar.setText("");
			}
		}
	}

	/**
     * Threading for search query
     * 
     * @author tmn
     *
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
//    			if (!history_list.contains(searchbar.getText().toString().trim()) && !historyUsed) {
//    				new ListUpdateThread(1, -1).execute();    				
//    			}
    			if (!isInDatabase(context, searchbar.getText().toString().trim())) {
    				Toast.makeText(context, "Yoyo", Toast.LENGTH_SHORT).show();  
    				insertIntoDb(context, searchbar.getText().toString().trim(), buss.getAnswer());
    			}
    			Toast.makeText(context, "YOYOYO", Toast.LENGTH_SHORT).show();  
    			
    			answerField.setText(buss.getAnswer());
    		}
    	}
    	
    }
    
    /**
     * Thread for updating list
     * 
     * @author tmn
     *
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
	
}
