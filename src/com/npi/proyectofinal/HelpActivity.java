/**
 *   Copyright 2013 Ruben Bermudez, Santiago Lopez and Isaac Morely
 *  
 *   This file is part of ProyectoFinal.
 *
 *   ProyectoFinal is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ProyectoFinal is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ProyectoFinal.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.npi.proyectofinal;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * 
 * @author Ruben Bermudez
 * @author Santiago Lopez
 * @author Isaac Morely
 * 
 * @brief  Activity to manage the help menu 
 *
 */
public class HelpActivity extends Activity {
	
	private WebView webView;
	
	/**
	 * @brief Creates the help menu as a web page
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		webView = (WebView) findViewById(R.id.helpWebView);
		webView.loadUrl("file:///android_res/raw/ayudahtml.html");
	}
}
