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
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

public class ScoreRegistrer extends FragmentActivity {
	private static final int MIN_SCORE = 0;
	private static final int SCORE_THRESHOLD_1 = 100;
	private static final int SCORE_THRESHOLD_2 = 300;
	private static final int SCORE_THRESHOLD_3 = 500;
	private static final int SCORE_THRESHOLD_4 = 1000;
	private static final String MIN_SCORE_WORD = "You are not good at this...";
	private static final String SCORE_THRESHOLD_1_WORD = "Try again";
	private static final String SCORE_THRESHOLD_2_WORD = "Not bad";
	private static final String SCORE_THRESHOLD_3_WORD = "Good job!";
	private static final String SCORE_THRESHOLD_4_WORD = "Great!";
	private static final String SCORE_THRESHOLD_5_WORD = "You are awesome!";
	private int score;
	private AdaptadorBD managerBD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_registrer);
		ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
		managerBD = new AdaptadorBD(this);
		
		try {
			managerBD.open();
			fragment.setList(managerBD.getAllRecords(), null);
			managerBD.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		OurSoundPlayer.initSounds(this);
		score = getIntent().getExtras()
				.getInt(getString(R.string.score));
		((TextView) findViewById(R.id.scoreNumberText))
			.setText(Integer.toString(score));
		
		if(score <= MIN_SCORE){
			((TextView) findViewById(R.id.titleText))
			.setText(MIN_SCORE_WORD);
			OurSoundPlayer.playSound(this, OurSoundPlayer.W1);
		}
		else if(score <= SCORE_THRESHOLD_1){
			((TextView) findViewById(R.id.titleText))
			.setText(SCORE_THRESHOLD_1_WORD);
			OurSoundPlayer.playSound(this, OurSoundPlayer.R2);
		}
		else if(score <= SCORE_THRESHOLD_2){
			((TextView) findViewById(R.id.titleText))
			.setText(SCORE_THRESHOLD_2_WORD);
			OurSoundPlayer.playSound(this, OurSoundPlayer.APPLAUSE);
		}
		else if(score <= SCORE_THRESHOLD_3){
			((TextView) findViewById(R.id.titleText))
			.setText(SCORE_THRESHOLD_3_WORD);
			OurSoundPlayer.playSound(this, OurSoundPlayer.CHEER);
		}
		else if(score <= SCORE_THRESHOLD_4){
			((TextView) findViewById(R.id.titleText))
			.setText(SCORE_THRESHOLD_4_WORD);
			OurSoundPlayer.playMusic(this, OurSoundPlayer.VICTORY_MUSIC);
		}
		else{
			((TextView) findViewById(R.id.titleText))
			.setText(SCORE_THRESHOLD_5_WORD);
			OurSoundPlayer.playSound(this, OurSoundPlayer.APPLAUSE);
			OurSoundPlayer.playSound(this, OurSoundPlayer.CHEER);
			OurSoundPlayer.playMusic(this, OurSoundPlayer.VICTORY_MUSIC);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_registrer, menu);
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public void buttonDone(View view){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = sdf.format(new Date());
		String name = ((EditText) findViewById(R.id.nameText)).getText().toString();

		try {
			managerBD.open();
			managerBD.addPoints(name, currentDate, score);
			managerBD.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finish();
	}
}
