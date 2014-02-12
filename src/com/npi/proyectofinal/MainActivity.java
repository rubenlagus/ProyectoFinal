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

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     *	@brief Close this activity and begins the game.
     *	@note  It's used when the button "Play" is pressed in the UI.
     */
    public void goPlay(View v){
    	Intent intent = new Intent(this, GameActivity.class);
    	
		startActivity(intent);
    }
    
    /**
     * 	@brief Close this activity and starts the activity that shows the scores.
     *	@note  It's used when the button "Scores" is pressed in the UI.
     */
    public void goScores(View v){
    	Intent intent = new Intent(this, ScoresActivity.class);
    	
    	startActivity(intent);
    }
    
    /**
     * 	@brief Close this activity and starts the activity that shows the help.
     *	@note  It's used when the button "Help" is pressed in the UI.
     */
    public void goHelp(View v){
    	Intent intent = new Intent(this, HelpActivity.class);
    	
    	startActivity(intent);
    }
    
    /**
     *	@brief Close the app.
     *	@note  It's used when the button "Exit" is pressed in the UI. 
     */
    public void exit(View v){
    	finish();
    }
}
