package net.trimn.bybuss.derped;

import net.trimn.bybuss.R;
import net.trimn.bybuss.R.layout;
import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class ByBussMap extends MapActivity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_map);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
