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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @author Ruben Bermudez
 * @author Santiago Lopez
 * @author Isaac Morely
 * 
 * @brief Class to manage the event of showing the score to the player when a game ends
 *
 */
public class ScoreRegistrer extends FragmentActivity {
	/** Thresholds are used to show one of the messages below depending on score*/
	private static final int MIN_SCORE = 0;
	private static final int SCORE_THRESHOLD_1 = 100;
	private static final int SCORE_THRESHOLD_2 = 300;
	private static final int SCORE_THRESHOLD_3 = 500;
	private static final int SCORE_THRESHOLD_4 = 1000;
	
	private static final int CONST_TIME_SLEEP = 1500; //< Time to wait before play sounds and music
	private int score;	//< Score obtained
	private AdaptadorBD managerBD;	//< Score DataBase manager
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
            	finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };
	
    /**
     * @brief Creates the score menu when a game ends
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_registrer);
		ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
		managerBD = new AdaptadorBD(this);
		
		/** Opening the DB and showing all previous score records */
		try {
			managerBD.open();
			fragment.setList(managerBD.getAllRecords(), null);
			managerBD.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		OurSoundPlayer.initSounds(ScoreRegistrer.this);
		score = getIntent().getExtras()
				.getInt(getString(R.string.score));
		((TextView) findViewById(R.id.scoreNumberText))
			.setText(Integer.toString(score));
		
		/** Playing a sound and showing a certain text depending on the score obtained */
		if(score <= MIN_SCORE){
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.worst_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.W1);
    	         } 
    	    }, CONST_TIME_SLEEP); 
		}
		else if(score <= SCORE_THRESHOLD_1){
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.bad_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.R2);
    	         } 
    	    }, CONST_TIME_SLEEP);
		}
		else if(score <= SCORE_THRESHOLD_2){
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.normal_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.APPLAUSE);
    	         } 
    	    }, CONST_TIME_SLEEP);
		}
		else if(score <= SCORE_THRESHOLD_3){
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.good_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.CHEER);
    	         } 
    	    }, CONST_TIME_SLEEP);
		}
		else if(score <= SCORE_THRESHOLD_4){
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.great_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playMusic(ScoreRegistrer.this, OurSoundPlayer.VICTORY_MUSIC);
    	         } 
    	    }, CONST_TIME_SLEEP);
		}
		else{
			((TextView) findViewById(R.id.titleText))
			.setText(getString(R.string.awesome_score));
			Handler handler = new Handler(); 
    	    handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.APPLAUSE);
    	        	 OurSoundPlayer.playSound(ScoreRegistrer.this, OurSoundPlayer.CHEER);
    	        	 OurSoundPlayer.playMusic(ScoreRegistrer.this, OurSoundPlayer.VICTORY_MUSIC);
    	         } 
    	    }, CONST_TIME_SLEEP);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_registrer, menu);
		return true;
	}
	
	/**
	 * @name onKeyDown
	 * @brief Overrides the activity method so if the users press the back button 
	 * the game will ask him if he wants to exit without saving his score
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK)){
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setCancelable(false);
	    	builder.setMessage("Do you want to exit without saving your score?")
	            .setPositiveButton("Yes", dialogClickListener)
	                .setNegativeButton("No", dialogClickListener).show();
	    	
	    	return true;
	    }
	    else
	    	return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * @brief Done button functionality. It saves the score obtained, the name introduced by the player and the date
	 * @param view Current view 
	 */
	@SuppressLint("SimpleDateFormat")
	public void buttonDone(View view){
		String name = ((EditText) findViewById(R.id.nameText)).getText().toString();
		
		if(!name.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String currentDate = sdf.format(new Date());
	
			try {
				managerBD.open();
				managerBD.addPoints(name, currentDate, score);
				managerBD.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			finish();
		}
		else
			Toast.makeText(this, "Write your name.", Toast.LENGTH_SHORT).show();
	}
}
