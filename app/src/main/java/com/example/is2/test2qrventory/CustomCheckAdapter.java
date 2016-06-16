package com.example.is2.test2qrventory;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.is2.test2qrventory.printer.CustomCheckData;

public class CustomCheckAdapter extends ArrayAdapter<CustomCheckData> {

	LayoutInflater mInflater;

	public CustomCheckAdapter(Context context, List<CustomCheckData> objects) {
		super(context, 0, objects);

		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = this.mInflater.inflate(R.layout.row_check, parent,
					false);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(R.id.textView);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CustomCheckData data = getItem(position);
		holder.textView.setText(data.getText());
		CheckBox chk = holder.checkBox;
		chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				data.setChecked(isChecked);
			}
		});
		holder.checkBox.setChecked(data.getChecked());
		return convertView;
	}

	class ViewHolder {
		TextView textView;
		CheckBox checkBox;
	}

}
