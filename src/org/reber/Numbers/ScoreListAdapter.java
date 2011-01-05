/*
 * Copyright (C) 2011 Brian Reber
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Brian Reber.  
 * THIS SOFTWARE IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.reber.Numbers;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoreListAdapter extends ArrayAdapter<Score> {
	private List<Score> items;

	/**
	 * Creates a new ScoreListAdapter with the given context, textViewResourceId and items
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 * The items to show in the ListView
	 */
	public ScoreListAdapter(Context context, int textViewResourceId, List<Score> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout v = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.rows, parent, false);

		TextView rowNum  = (TextView) v.findViewById(R.id.row_num);
		TextView userName  = (TextView) v.findViewById(R.id.user_name);

		rowNum.setText((position + 1) + ".");
		userName.setText(items.get(position).toString());

		return v;
	}
}