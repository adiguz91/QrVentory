package com.example.is2.test2qrventory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.epson.lwprint.sdk.formdata.PlistParser;
import com.example.is2.test2qrventory.CustomCheckAdapter;
import com.example.is2.test2qrventory.printer.CustomCheckData;
import com.example.is2.test2qrventory.printer.LWPrintSampleUtil;
import com.example.is2.test2qrventory.printer.Logger;

public class SelectXmlActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = getClass().getSimpleName();

	private static final String KEY_FORMDATA = "formdata";
	private static final String FORM_DATA_LIST_FILE_NAME = "FormDataList.plist";

	private CustomCheckAdapter mAdapter;
	ListView listView = null;
	List<CustomCheckData> list = null;
	private boolean checkMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_xml);

		Button buttonAll = (Button) findViewById(R.id.select_xml_all_button);
		buttonAll.setOnClickListener(this);
		Button buttonDone = (Button) findViewById(R.id.select_xml_ok_button);
		buttonDone.setOnClickListener(this);

		list = new ArrayList<CustomCheckData>();
		AssetManager assets = getResources().getAssets();
		InputStream is = null;
		ArrayList<String> formData = null;
		if (LWPrintSampleUtil.SAVE_VALUES_MODE) {
			formData = LWPrintSampleUtil.loadValues(KEY_FORMDATA, SelectXmlActivity.this);
		} else {
			Intent intent = getIntent();
			if (intent != null){
				formData = intent.getStringArrayListExtra(KEY_FORMDATA);
			}
		}

		try {
			is = assets.open(FORM_DATA_LIST_FILE_NAME);
			Object obj = PlistParser.parse(is, true);
			if (obj instanceof List<?>) {
				List<?> formDataList = (List<?>)obj;
				String[] fileList = formDataList.toArray(new String[0]);
				for (int i = 0; i < fileList.length; i++) {
					boolean isChecked = false;
					if (formData != null) {
						isChecked = formData.contains(fileList[i]);
					}
					list.add(new CustomCheckData(fileList[i], isChecked));
				}
			}
		} catch (IOException e) {
			Logger.e(TAG, "", e);
		}

		listView = (ListView) findViewById(R.id.select_xml_list_view);

		mAdapter = new CustomCheckAdapter(this, list);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CheckBox chk = (CheckBox) view.findViewById(R.id.checkBox);
		chk.setChecked(!chk.isChecked());
		CustomCheckData data = mAdapter.getItem(position);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_xml_all_button:
			if (list != null) {
				checkMode = !checkMode;
				mAdapter.setNotifyOnChange(false);
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setChecked(checkMode);
				}
				mAdapter.setNotifyOnChange(true);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.select_xml_ok_button:
			ArrayList<String> formNames = new ArrayList<String>();
			int count = mAdapter.getCount();
			for (int i = 0; i < count; i++) {
				CustomCheckData data = mAdapter.getItem(i);
				if (data.getChecked()) {
					formNames.add(data.getText());
				}
			}
			if (formNames.size() == 0) {
				// default
				formNames.add(mAdapter.getItem(0).getText());
			}
			if (LWPrintSampleUtil.SAVE_VALUES_MODE) {
				LWPrintSampleUtil.saveValues(formNames, KEY_FORMDATA, this);
			}
			Intent intent = getIntent();
			intent.putStringArrayListExtra(KEY_FORMDATA, formNames);
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
	}

}
