package net.trimn.bybuss.derped;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class ByBussOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	
	private String reiseFra = null;
	private String reiseTil = null;
	private EditText searchbar;
	
	public ByBussOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public ByBussOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	public ByBussOverlay(Drawable defaultMarker, Context context, EditText searchbar) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		this.searchbar = searchbar;
	}

	@Override
	protected boolean onTap(int index) {
		final OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
		dialog.setTitle(item.getTitle());
		// dialog.setMessage(item.getSnippet());
		if (reiseFra == null) {
			dialog.setMessage("Reise fra " + item.getTitle());
		} else {
			dialog.setMessage("Reise til " + item.getTitle());
		}
		
		
		dialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (reiseFra == null) {
					reiseFra = item.getTitle();
				} else {
					reiseTil = item.getTitle();
					searchbar.setText(reiseFra + " til " + reiseTil);
					reiseFra = reiseTil = null;
				}
				return;
			}
		});
		
		dialog.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		
		dialog.show();
		return true;
	}
	
	public void addOverlayItem(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int item) {
		return overlays.get(item);
	}

	@Override
	public int size() {
		return overlays.size();
	}

}
