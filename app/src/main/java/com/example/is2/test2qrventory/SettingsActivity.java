package com.example.is2.test2qrventory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.example.is2.test2qrventory.R;
import com.example.is2.test2qrventory.printer.LWPrintSampleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends Activity {

    private static final int REQUEST_ACTIVITY_PRINT_SETTINGS = 1;
    private static final int REQUEST_ACTIVITY_CONNECTION_SETTINGS = 2;

    public static SettingsActivity activity_instance = null;
    //private Activity baseActivity;

    ListView listview;
    List<String> settings_list = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    Map<String, Object> _printSettings = null;
    Map<String, String> _printerInfo = null;

    public Map<String, Object> getPrintSettings() { return _printSettings; }
    public Map<String, String> getPrinterInfo() { return _printerInfo; }

    public static synchronized SettingsActivity getInstance() {
        return activity_instance;
    }

    /*public Activity getbaseActivity()
    {
        return baseActivity;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listview = (ListView) findViewById(R.id.setting_type_list_view);

        settings_list.add("Print Settings");
        settings_list.add("Printer Device Settings");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settings_list);

        listview.setAdapter(adapter);

        setPrintSettingsValues();

        if (activity_instance == null) {
            activity_instance = this;
        }

        _printerInfo = activity_instance.getPrinterInfo();
        _printSettings = activity_instance.getPrintSettings();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView lv = (ListView) parent;
                String item = (String) lv.getItemAtPosition(position);

                Intent intent = null;

                if ("Print Settings".equals(item)) {
                    intent = new Intent(view.getContext(), PrintSettingsActivity.class);
                    startActivityForResult(intent, REQUEST_ACTIVITY_PRINT_SETTINGS);
                } else if ("Printer Device Settings".equals(item)) {
                    intent = new Intent(view.getContext(), SearchActivity.class);
                    startActivityForResult(intent, REQUEST_ACTIVITY_CONNECTION_SETTINGS);
                }
                /*Intent intent = getIntent();
                intent.putExtra("Barcode Type", item);

                setResult(RESULT_OK, intent);
                finish();*/
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACTIVITY_PRINT_SETTINGS:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        _printSettings.put(LWPrintParameterKey.Copies,
                                extras.getInt(LWPrintParameterKey.Copies));
                        _printSettings.put(LWPrintParameterKey.TapeCut,
                                extras.getInt(LWPrintParameterKey.TapeCut));
                        _printSettings.put(LWPrintParameterKey.HalfCut,
                                extras.getBoolean(LWPrintParameterKey.HalfCut));
                        _printSettings.put(LWPrintParameterKey.PrintSpeed,
                                extras.getBoolean(LWPrintParameterKey.PrintSpeed));
                        _printSettings.put(LWPrintParameterKey.Density,
                                extras.getInt(LWPrintParameterKey.Density));
                    }
                }
                break;
            case REQUEST_ACTIVITY_CONNECTION_SETTINGS:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        if (_printerInfo != null) {
                            _printerInfo.clear();
                            _printerInfo = null;
                        }
                        _printerInfo = new HashMap<String, String>();
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_NAME,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_NAME));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_HOST,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_HOST));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_PORT,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PORT));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS));
                        /*buttonSearch.setText(getResources().getString(
                                R.string.button_search)
                                + extras.getString("name"));*/
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setPrintSettingsValues() {
		int copies = LWPrintSampleUtil.DEFAULT_COPIES_SETTING;
		int tapeCut = LWPrintSampleUtil.DEFAULT_TAPE_CUT_SETTING;
		boolean halfCut = LWPrintSampleUtil.DEFAULT_HALF_CUT_SETTING;
		boolean printSpeed = LWPrintSampleUtil.DEFAULT_LOW_SPEED_SETTING;
		int density = LWPrintSampleUtil.DEFAULT_DENSITY_SETTING;

		_printSettings = new HashMap<String, Object>();
		_printSettings.put(LWPrintParameterKey.Copies, copies);
		_printSettings.put(LWPrintParameterKey.TapeCut, tapeCut);
		_printSettings.put(LWPrintParameterKey.HalfCut, halfCut);
		_printSettings.put(LWPrintParameterKey.PrintSpeed, printSpeed);
		_printSettings.put(LWPrintParameterKey.Density, density);

		SharedPreferences pref = getSharedPreferences(
				LWPrintSampleUtil.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(LWPrintParameterKey.Copies, copies);
		editor.putInt(LWPrintParameterKey.TapeCut, tapeCut);
		editor.putBoolean(LWPrintParameterKey.HalfCut, halfCut);
		editor.putBoolean(LWPrintParameterKey.PrintSpeed, printSpeed);
		editor.putInt(LWPrintParameterKey.Density, density);
		editor.commit();
	}



}
