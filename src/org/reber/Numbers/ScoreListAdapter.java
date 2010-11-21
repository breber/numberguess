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