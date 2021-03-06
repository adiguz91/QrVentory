package com.example.is2.test2qrventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinterCallback;
import com.epson.lwprint.sdk.LWPrintStatusError;
import com.example.is2.test2qrventory.printer.DeviceInfo;
import com.example.is2.test2qrventory.printer.PrinterStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity {

	private static final String SEP = System.getProperty("line.separator");
	private String type = "_pdl-datastream._tcp.local.";

	ServiceCallback listener;
	LWPrintDiscoverPrinter lpPrintDiscoverPrinter;

	android.os.Handler handler = new android.os.Handler();

	ListView listView;
	List<String> dataList = new ArrayList<String>();
	List<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();
	ArrayAdapter<String> adapter;

	Map<String, String> printerInfo = null;
	private ProgressDialog waitDialog;
	LWPrint lwprint;
	Map<String, Integer> lwStatus = null;
	Boolean connectionFound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		listView = (ListView) findViewById(R.id.listview_search);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView lv = (ListView) parent;
				String item = (String) lv.getItemAtPosition(position);
				Intent intent = getIntent();
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_NAME,
						deviceList.get(position).getName() != null ? deviceList.get(position).getName() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT,
						deviceList.get(position).getProduct() != null ? deviceList.get(position).getProduct() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL,
						deviceList.get(position).getUsbmdl() != null ? deviceList.get(position).getUsbmdl() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_HOST,
						deviceList.get(position).getHost() != null ? deviceList.get(position).getHost() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_PORT,
						deviceList.get(position).getPort() != null ? deviceList.get(position).getPort() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE,
						deviceList.get(position).getType() != null ? deviceList.get(position).getType() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN,
						deviceList.get(position).getDomain() != null ? deviceList.get(position).getDomain() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER,
						deviceList.get(position).getMacaddress() != null ? deviceList.get(position).getMacaddress() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS,
						deviceList.get(position).getDeviceClass() != null ? deviceList.get(position).getDeviceClass() : "");
				intent.putExtra(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS,
						deviceList.get(position).getDeviceStatus() != null ? deviceList.get(position).getDeviceStatus() : "");
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		// Create LWPrintDiscoverPrinter
		List<String> typeList = new ArrayList<String>();
		typeList.add(type);

		// Discover printer
		// (1) Search all connection
		lpPrintDiscoverPrinter = new LWPrintDiscoverPrinter(typeList);
		// (2) Search network connection
		//List<String> modelNames = new ArrayList<String>(Arrays.asList("(EPSON LW-1000P)"));
		//EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet.of(LWPrintDiscoverConnectionType.ConnectionTypeNetwork);
		//lpPrintDiscoverPrinter = new LWPrintDiscoverPrinter(typeList, modelNames, flag);
		// (3) Search bluetooth connection
		//EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
		//lpPrintDiscoverPrinter = new LWPrintDiscoverPrinter(null, null, flag);

		// Sets the callback
		lpPrintDiscoverPrinter.setCallback(listener = new ServiceCallback());

		// ProgressDialog
		waitDialog = new ProgressDialog(this);
		waitDialog.setMessage("Search for printers...");
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDialog.setCancelable(false);

		lwprint = new LWPrint(this);

		getTemporaryPrinterInfo();
		if (printerInfo != null) {
			waitDialog.show();

			new AsyncTask<Object, Object, PrinterStatus>() {
				@Override
				protected PrinterStatus doInBackground(Object... params) {
					lwprint.setPrinterInformation(printerInfo);
					lwStatus = lwprint.fetchPrinterStatus();
					PrinterStatus status = new PrinterStatus();
					status.setDeviceError(lwprint
							.getDeviceErrorFromStatus(lwStatus));
					return status;
				}

				@Override
				protected void onPostExecute(PrinterStatus status) {
					//notifyPrinterStatus(status.getDeviceError());

					if (status.getDeviceError() == LWPrintStatusError.ConnectionFailed || lwStatus.isEmpty()) {
						connectionFound = false;
						String message = "Connection failed. Turn on label printer.";
						alertAbortOperation("Error", message);
					} else {
						connectionFound = true;
					}
					waitDialog.dismiss();
				}
			}.execute();
		}

		lpPrintDiscoverPrinter.startDiscover(this);
	}

	private void getTemporaryPrinterInfo() {
		printerInfo = new HashMap<String, String>();
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_NAME, "LW-600P");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT, "LW-600P");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL, "LW-600P");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_HOST, "");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_PORT, "");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE, "_pdl-datastream._bluetooth.");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN, "local.");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER, "E4:7F:B2:6A:36:39");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS, "PRINTER");
		printerInfo.put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS, "");

	}

	class ServiceCallback implements LWPrintDiscoverPrinterCallback {

		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter discoverPrinter,
				Map<String, String> printer) {
			// Called when printers are detected

            for (DeviceInfo info : deviceList) {
            	if (info.getName().equals(printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME))
                 && info.getHost().equals(printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_HOST))
            	 && info.getMacaddress().equals(printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER))) {
            		return;
            	}
            }

			String type = (String) printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE);
			String status = (String) printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS);

			DeviceInfo obj = new DeviceInfo();
			obj.setName((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME));
			obj.setProduct((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT));
			obj.setUsbmdl((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL));
			obj.setHost((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_HOST));
			obj.setPort((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_PORT));
			obj.setType(type);
			obj.setDomain((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN));
			obj.setMacaddress((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER));
			obj.setDeviceClass((String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS));
			obj.setDeviceStatus(status);

			deviceList.add(obj);

			if (TextUtils.isEmpty(obj.getMacaddress())) {
				// Wi-Fi
				notifyAdd((String) printer
						.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME)
						+ SEP
						+ (String) printer
							.get(LWPrintDiscoverPrinter.PRINTER_INFO_HOST)
						+ SEP
						+ (String) printer
							.get(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE));
			} else {
				if (TextUtils.isEmpty(status)) {
					// Bluetooth
					notifyAdd((String) printer
							.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME)
							+ SEP
							+ (String) printer
								.get(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER)
							+ SEP
							+ (String) printer
								.get(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS));
				} else {
					// Wi-Fi Direct
					int deviceStatus = -1;
					try {
						deviceStatus = Integer.parseInt(status);
					} catch (NumberFormatException e) {
					}
					notifyAdd((String) printer
							.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME)
							+ SEP
							+ (String) printer
								.get(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER)
							+ SEP
							+ getDeviceStatusForWifiDirect(deviceStatus));
				}
			}
		}

		private String getDeviceStatusForWifiDirect(int deviceStatus) {
			switch (deviceStatus) {
			case 0:
				return "Connected";
			case 1:
				return "Invited";
			case 2:
				return "Failed";
			case 3:
				return "Available";
			case 4:
				return "Unavailable";
			default:
				return "Unknown";
			}
		}

		@Override
		public void onRemovePrinter(LWPrintDiscoverPrinter discoverPrinter,
				Map<String, String> printer) {
			// Called when printers have been deleted

			String name = (String) printer
					.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME);
			int index = -1;
			for (int i = 0; i < deviceList.size(); i++) {
				DeviceInfo info = deviceList.get(i);
				if (name.equals(info.getName())) {
					index = i;
					break;
				}
			}
			if (index >= 0) {
				notifyRemove(index);
				deviceList.remove(index);
			}
		}

	}

	private void notifyAdd(final String name) {
		handler.postDelayed(new Runnable() {
			public void run() {
				if (connectionFound) {
					dataList.add(name);
					adapter.notifyDataSetChanged();
				}
			}
		}, 4000);
	}

	private void notifyUpdate(final int index, final String name) {
		handler.postDelayed(new Runnable() {
			public void run() {
				dataList.set(index, name);
				adapter.notifyDataSetChanged();
			}
		}, 1);
	}

	private void notifyRemove(final int index) {
		handler.postDelayed(new Runnable() {
			public void run() {
				dataList.remove(index);
				adapter.notifyDataSetChanged();
			}
		}, 1);
	}

	@Override
	public void onDestroy() {
		if (lpPrintDiscoverPrinter != null) {
			// Stops discovery
			lpPrintDiscoverPrinter.stopDiscover();
			lpPrintDiscoverPrinter = null;
		}
		super.onDestroy();
	}

	public void alertAbortOperation(final String title, final String message) {
		handler.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						SearchActivity.this);
				alert.setTitle(title);
				alert.setMessage(message);
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								lwprint.cancelPrint();
							}
						});
				AlertDialog alertDialog = alert.create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();
			}
		}, 1);
	}

}
