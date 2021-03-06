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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 
 * @author Ruben Bermudez
 * @author Santiago Tapia
 * @author Isaac Morely
 * 
 * @brief
 *  This class (extended from SQLiteOpenHelper) allows manipulate the database
 *  
 * @see android.database.sqlite.SQLiteOpenHelper;
 */
public class DatabaseHelper extends SQLiteOpenHelper{
	public static final String KEY_IDFILA = "_id"; //< Name of the first attribute of our table
	public static final String KEY_NOMBRE = "name"; //< Name of the second attribute of our table
	public static final String KEY_DATE = "date"; //< Name of the third attribute of our table
	public static final String KEY_POINTS = "points"; //< Name of the third attribute of our table
	private static final String NOMBRE_BASEDATOS = "proyectofinal"; //< Name of our table
	private static final String TABLA_BASEDATOS = "marks"; //< Name of our table

	private static final int VERSION_BASEDATOS = 1; //< Version of the database
	private static final String CREAR_BASEDATOS = "create table " + TABLA_BASEDATOS + " (" + KEY_IDFILA + " integer primary key autoincrement, " + KEY_NOMBRE + " text not null, " +  KEY_DATE + " text not null, " + KEY_POINTS + " integer not null );"; //< Creation query
	
	/**
	 * @brief Constructor
	 * @param context Context of the database
	 */
	public DatabaseHelper(Context context) {
		super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
	}

	@Override
	/**
	 * @name onCreate
	 * @brief Method called when creating the view
	 * @note
	 * 	It create the database
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAR_BASEDATOS);
	}

	@Override
	/**
	 * @name onUpgrade
	 * @brief Method called when updating the database
	 * @note
	 * 	The old table is deleted and the database is recreated
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXITST " + TABLA_BASEDATOS);
		onCreate(db);
	}
}