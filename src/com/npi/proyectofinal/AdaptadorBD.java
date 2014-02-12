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
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 
 * 
 * @author Ruben Bermudez
 * @author Santiago Tapia
 * @author Isaac Morely
 * 
 * @brief
 *  This class contains all the methods needed by our application to access the database.
 *  
 * @see DatabaseHelper
 * @see android.database.sqlite.SQLiteDatabase
 *
 */
public class AdaptadorBD {
	public static final String KEY_IDFILA = "_id"; //< Name of the first attribute of our table
	public static final String KEY_NOMBRE = "name"; //< Name of the second attribute of our table
	public static final String KEY_DATE = "date"; //< Name of the third attribute of our table
	public static final String KEY_POINTS = "points"; //< Name of the third attribute of our table
	private static final String TABLA_BASEDATOS = "marks"; //< Name of our table
	private final Context context; //< Context of the DB
	private DatabaseHelper DBHelper; //< Manager to allow manipulation of the DB
	private SQLiteDatabase db; //<  Instance of a BD
	
	/**
	 * @brief  Constructor
	 * 		Initialise the context and the Manager
	 */
	public AdaptadorBD(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	/**
	 * @brief Open the database 
	 * @return Itself to allow other calls to method after open the db
	 * @throws SQLException If any problem opening the db
	 */
	public AdaptadorBD open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * @brief Close the database
	 */
	public void close(){
		DBHelper.close();
	}
	
	/**
	 * @brief Add a new points values to our database
	 * @param name Name of the player
	 * @param date current Date
	 * @param points Number of points achieved
	 * @return ID of the new registry created when adding
	 */
	public long addPoints(String name, String date, int points) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NOMBRE, name);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_POINTS, points);
		return db.insert(TABLA_BASEDATOS, null, initialValues);
	}
	
	/**
	 * @brief Remove points values of a player
	 * @param name Name of the player to delete
	 * @return True if the records have been removed
	 */
	public boolean removePointsByName(String name){
		return db.delete(TABLA_BASEDATOS, KEY_NOMBRE + " = ?", new String[] { String.valueOf(name)}) > 0;		
	}
	
	/**
	 * @brief Find all name of the players
	 * @return Cursor for the list of names retrieved from the database
	 * @see android.database.Cursor
	 */
	public Cursor getAllNames(){
		String query = "SELECT " + KEY_NOMBRE + " FROM " + TABLA_BASEDATOS;
		Log.e("MyDataBase", query);
		Cursor mCursor = db.rawQuery(query, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public List<RecordScore> getAllRecords(){
		List<RecordScore> allRecords = new ArrayList<RecordScore>();
		String query = "SELECT " + KEY_NOMBRE + ", " + KEY_POINTS + ", " + KEY_DATE + 
				" FROM " + TABLA_BASEDATOS + " ORDER BY " + KEY_POINTS + " DESC";
		
		Cursor mCursor = db.rawQuery(query, null);
		
		if (mCursor != null)
			mCursor.moveToFirst();
		
		while(!mCursor.isAfterLast()){
			allRecords.add(CursorToRecordStore(mCursor));
			
			mCursor.moveToNext();
		}
		
		return allRecords;
	}
	
	/**
	 * @brief Get all records
	 * @return Cursor for the list of contacts.
	 * @see android.database.Cursor
	 */
	public Cursor obtenerTodosLosContactos(){
		return db.query(TABLA_BASEDATOS, new String[] {KEY_IDFILA, KEY_NOMBRE, KEY_DATE, KEY_POINTS}, null, null, null, null, null);
	}
	
	/**
	 * @brief Get a record by its ID
	 * @param idFila ID of the record
	 * @return Cursor for this record
	 * @throws SQLException If the record could not be found
	 * @see android.database.Cursor
	 */
	public Cursor obtenerContacto(long idFila) throws SQLException {
		Cursor mCursor = db.query(true, TABLA_BASEDATOS, new String[] {KEY_IDFILA, KEY_NOMBRE, KEY_DATE, KEY_POINTS}, KEY_IDFILA + "=" + idFila, null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		
		return mCursor;
	}
	
	@SuppressLint("SimpleDateFormat")
	private RecordScore CursorToRecordStore(Cursor cursor){
		return new RecordScore(cursor.getString(0), cursor.getString(2), cursor.getInt(1));
	}
}