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

public class RecordScore {
	private static final String NAME_WORD = "Name: ";
	private static final String DATE_WORD = "Date: ";
	private static final String SCORE_WORD = "Score: ";
	private String name;
	private String date;
	private int score;
	
	RecordScore(String nName, String nDate, int nScore){
		name = nName;
		date = nDate;
		score = nScore;
	}
	
	public String getName(){
		return name;
	}
	
	public int getScore(){
		return score;
	}
	
	public String getDate(){
		return date;
	}
	
	public String toString(){
		return NAME_WORD + name + "\n" 
				+ SCORE_WORD + Integer.toString(score) + "\n" 
				+ DATE_WORD +  date; 
	}
}
