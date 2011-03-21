package net.trimn.bybuss;

import android.content.res.Resources;
import android.widget.TabHost;

public class ByBussTabs {
	private TabHost tabs;
	private TabHost.TabSpec spec;
	private Resources res;
	
	public ByBussTabs(TabHost tabs, Resources res) {
		this.tabs = tabs;
		this.res = res;
	}
	
	public void addTab(String name, int draw, int tabId) {
		spec = tabs.newTabSpec(name.toLowerCase()).setIndicator(name, res.getDrawable(draw));
		spec.setContent(tabId);
		tabs.addTab(spec);
	}
	

}
