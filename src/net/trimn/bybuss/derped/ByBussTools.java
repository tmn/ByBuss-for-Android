package net.trimn.bybuss.derped;

import android.app.Application;
import android.os.Bundle;

public class ByBussTools extends Application {
	private static ByBussTools app;
	
	public void onCreate(Bundle savedInstanceState) {
		app = this;

	}
}
