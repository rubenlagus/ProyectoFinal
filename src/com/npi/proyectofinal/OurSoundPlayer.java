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

import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class OurSoundPlayer{
	public static final int W1 = 0;
	public static final int W2 = 1;
	public static final int R1 = 2;
	public static final int R2 = 3;
	public static final int APPLAUSE = 4;
	public static final int CHEER = 5;
	public static final int SHAKE = 6;
	public static final int DEFEAT_MUSIC = R.raw.defeat;
	public static final int DEFEAT_MUSIC_DURATION = 10000;
	public static final int VICTORY_MUSIC = R.raw.victory;
	public static final int VICTORY_MUSIC_DURATION = 5000;
	private static SoundPool soundPool;
	private static ArrayList<Integer> soundPoolMap;

	/** Populate the SoundPool*/
	public static void initSounds(Context context) {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new ArrayList<Integer>(6);    
		soundPoolMap.add(soundPool.load(context, R.raw.wrong1, 1));
		soundPoolMap.add(soundPool.load(context, R.raw.wrong2, 2));
		soundPoolMap.add(soundPool.load(context, R.raw.right1, 3));
		soundPoolMap.add(soundPool.load(context, R.raw.right2, 4));
		soundPoolMap.add(soundPool.load(context, R.raw.applause, 5));
		soundPoolMap.add(soundPool.load(context, R.raw.cheer, 6));
		soundPoolMap.add(soundPool.load(context, R.raw.shake, 7));
	}

	public static void playSound(Context context, int soundID) {
		float volume = 1f;
	
		//play sound with same right and left volume, with a priority of 1,
		//zero repeats (i.e play once), and a playback rate of 1f
		soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
	}
	
	/** Play the sound using android.media.MediaPlayer */
	public static void playMusic(Context context, int soundID){      
		MediaPlayer mp = MediaPlayer.create(context, soundID); 
		mp.start();
	}
}
