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

import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFragment extends Fragment {
	View rootView;
	AdapterView.OnItemClickListener listener;
	List<RecordScore> lista;
	
	void setList(List<RecordScore> newLista, AdapterView.OnItemClickListener newListener){
		lista =  newLista;
		listener = newListener;
		
		final ListView listview = (ListView) getView().findViewById(R.id.listview);
		final ArrayAdapter<RecordScore> adapter = new ArrayAdapter<RecordScore>(getView().getContext(),
				        android.R.layout.simple_list_item_1, lista);
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(listener);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){		
		rootView = inflater.inflate(R.layout.list_fragment, container, false);
		
		return rootView;
	}
}
