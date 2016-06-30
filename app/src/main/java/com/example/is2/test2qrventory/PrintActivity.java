package com.example.is2.test2qrventory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintStatusError;
import com.epson.lwprint.sdk.LWPrintTapeCut;
import com.epson.lwprint.sdk.LWPrintConnectionStatus;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDataProvider;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;
import com.example.is2.test2qrventory.printer.LWPrintSampleUtil;
import com.example.is2.test2qrventory.printer.Logger;
import com.example.is2.test2qrventory.printer.PrinterStatus;
import com.example.is2.test2qrventory.printer.SampleDataProvider;


public class PrintActivity extends Activity implements OnClickListener {

	/*public enum FormType {
		String,
		QRCode,
		Barcode;
	}*/

	private final String TAG = getClass().getSimpleName();

	private static final int NOTIFICATION_ID = 1;

	private static final int REQUEST_ENABLE_BT = 0;
	private static final int REQUEST_ACTIVITY_SEARCH = 1;
	private static final int REQUEST_ACTIVITY_SELECT_XML = 2; //new

	private static final int REQUEST_ACTIVITY_CHOOSE_BARCODE_TYPE = 3;

	private static final String KEY_FORMDATA = "formdata"; //new
	private static final String SUFFIX = ".plist"; //new

	LWPrint lwprint;
	PrintCallback printListener;

	SampleDataProvider sampleDataProvider;

	private ArrayList<String> _formNames = null;
	private Button buttonSelectXml;
	private Button buttonDiscoverPrinter;
	private Button buttonPrint;
	private EditText editTextInputData;
	private TextView textResult;
	private TextView textPrintingPage;
	private TextView textDiscoverPrinter;
	private CheckBox checkBoxText;

	private boolean processing = false;
	private ProgressDialog progressDialog;
	private ProgressDialog waitDialog;
	private int _jobNumber = 0;
	Timer timer;

	Boolean printTextOnly = false;

	Map<String, String> printerInfo = null;
	Map<String, Object> printSettings = null;
	Map<String, Integer> lwStatus = null;

	User user = null;
	Item singleItem = null;
	Event associatedEvent = null;

	android.os.Handler handler = new android.os.Handler();
	/**
	 * You'll need this in your class to cache the helper in the class.
	 */
	//private DatabaseHelper databaseHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);

		// Run Database Manager
		//DbManager dbManager = new DbManager();

		// get our dao
		/*Dao<User, Integer> simpleDao = null;
		try {
			simpleDao = getHelper().getSimpleDataDao();
			// query for all of the data objects in the database
			List<User> list = simpleDao.queryForAll();

			for(User user : list) {
				Log.d(TAG, user.getEmail());
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		// --------------------------------------------------------------------

		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		lwprint = new LWPrint(this);
		// Sets the callback
		lwprint.setCallback(printListener = new PrintCallback());

		sampleDataProvider = new SampleDataProvider(this);

		buttonDiscoverPrinter = (Button) findViewById(R.id.button_discover_printer);
		buttonDiscoverPrinter.setOnClickListener(this);

		buttonSelectXml = (Button) findViewById(R.id.select_xml_button); //new
		buttonSelectXml.setOnClickListener(this); //new

		//textDiscoverPrinter = (TextView) findViewById(R.id.discover_printer_text_view);
		if (SettingsActivity.getInstance() != null) {
			printerInfo = SettingsActivity.getInstance().getPrinterInfo();
			if (printerInfo != null) {
				//textDiscoverPrinter = (TextView) findViewById(R.id.discover_printer_text_view);
				/*textDiscoverPrinter.setText(getResources().getString(
						R.string.button_search)
						+ printerInfo.get("name"));*/
				//buttonDiscoverPrinter.setOnClickListener(this);
				buttonDiscoverPrinter.setText(getResources().getString(
						R.string.button_search)
						+ printerInfo.get("name"));
			}
		}

		editTextInputData = (EditText) findViewById(R.id.edittext_input_data);
		//editTextInputData.setEnabled(false);
		editTextInputData.clearFocus();
		editTextInputData.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					onEnter();
					return true;
				}
				return false;
			}
		});

		associatedEvent = getIntent().getParcelableExtra("event");
		user = getIntent().getParcelableExtra("user");
		singleItem = getIntent().getParcelableExtra("item");
		editTextInputData.setText(singleItem.getName());

		buttonPrint = (Button) findViewById(R.id.button_print);
		buttonPrint.setOnClickListener(this);

		textResult = (TextView) findViewById(R.id.result_text_view);
		textPrintingPage = (TextView) findViewById(R.id.printingpage_text_view);

		// ProgressDialog for printing
		createProgressDialogForPrinting();

		// ProgressDialog
		waitDialog = new ProgressDialog(this);
		waitDialog.setMessage("Now processing...");
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDialog.setCancelable(false);

		// Form Data
		if (LWPrintSampleUtil.SAVE_VALUES_MODE) {
			ArrayList<String> savedFormData = LWPrintSampleUtil.loadValues(
					KEY_FORMDATA, this);
			if (savedFormData.size() > 0) {
				_formNames = savedFormData;
				buttonSelectXml.setText("Select: "
						+ "["
						+ savedFormData.get(_jobNumber)
						+ "] "
						+ (_jobNumber + 1) + " / " + savedFormData.size());
			}
		} else {
			buttonSelectXml.setText(getResources().getString(R.string.button_select_xml_no));
		}

		// Bluetooth
		try {
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
			if (btAdapter == null) {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available.", Toast.LENGTH_SHORT).show();
			} else {
				if (!btAdapter.isEnabled()) {
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(intent, REQUEST_ENABLE_BT);
				}
			}
		} catch (Exception e) {
			Logger.w(TAG, "", e);
		}

		checkBoxText = (CheckBox) findViewById(R.id.checkBox_text);
		checkBoxText.setChecked(false);

		checkBoxText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				String inputData = editTextInputData.getText().toString();
				if ( isChecked )
				{
					printTextOnly = true;
					_formNames.add("String");
				}
				else
				{
					printTextOnly = false;
					_formNames.remove("String");
				}

			}
		});

		/*if (SettingsActivity.getInstance() != null) {
			printerInfo = SettingsActivity.getInstance().getPrinterInfo();
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
						notifyPrinterStatus(status.getDeviceError());
						processing = false;
						waitDialog.dismiss();
					}
				}.execute();
			}
		}*/

	}

	/**
	 * You'll need this in your class to get the helper from the manager once per class.
	 */
	/*private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(getApplicationContext());
		}
		return databaseHelper;
	}*/

	private void onEnter(){
		setInputDataToDataProvider();

		editTextInputData.clearFocus();

		hideKeyboard();
	}

	private void setInputDataToDataProvider() {
		String inputData = editTextInputData.getText().toString();

		boolean atleastOneAlpha = inputData.matches(".*[a-zA-Z]+.*");
		if (!inputData.isEmpty() && atleastOneAlpha) {
			sampleDataProvider.setStringData(inputData);
		}
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void createProgressDialogForPrinting() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Now printing...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			progressDialog.setMax(100);
			progressDialog.incrementProgressBy(0);
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Logger.d(TAG, "Cancel onClick()");
							progressDialog.setProgress(0);
							dialog.cancel();
						}
					});
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							Logger.d(TAG, "Cancel onCancel()");
							doCancel();
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;

		_jobNumber = 0;

		setInputDataToDataProvider();

		editTextInputData.clearFocus();

		hideKeyboard();

		switch (v.getId()) {
		case R.id.button_discover_printer:
			intent = new Intent(this, com.example.is2.test2qrventory.SearchActivity.class);
			startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH);
			break;
		case R.id.select_xml_button:
			intent = new Intent(this, SelectXmlActivity.class);
			if (_formNames != null) {
				intent.putStringArrayListExtra(KEY_FORMDATA, _formNames);
			}
			startActivityForResult(intent, REQUEST_ACTIVITY_SELECT_XML);
		break;
		case R.id.button_print:
			if (SettingsActivity.getInstance() != null) {
				printerInfo = SettingsActivity.getInstance().getPrinterInfo();
			}
			if (printerInfo == null) {
				Toast.makeText(getApplicationContext(),
						"Device is not selected.", Toast.LENGTH_SHORT).show();
			} else if (_formNames == null) {
				Toast.makeText(getApplicationContext(),
						"Form data is not selected.", Toast.LENGTH_SHORT)
						.show();
			} else {
				setProcessing(true);
				_jobNumber = 0; //new
				performPrint();

			}
			break;
		default:
			break;
		}
	}

	/*private void notifyPrinterStatus(final int deviceError) {
		handler.postDelayed(new Runnable() {
			public void run() {
				//textPrinterStatus.setText(Integer.toHexString(deviceError));
				//textTapeLabel.setText(getTapeWidthStringFromTapeWidhCode(tape));
				if (lwStatus.isEmpty() || (deviceError == LWPrintStatusError.ConnectionFailed)) {
					textDiscoverPrinter.setText("Status: Device is not selected");
				}
			}
		}, 1);
	}*/

	/*private String getTapeWidthStringFromTapeWidhCode(int tapeWidth) {
		Map<Integer, String> tapeStrings = new HashMap<Integer, String>();
		tapeStrings.put(LWPrintTapeWidth.None, "None");
		tapeStrings.put(LWPrintTapeWidth.Normal_4mm, "4mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_6mm, "6mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_9mm, "9mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_12mm, "12mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_18mm, "18mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_24mm, "24mm");
		tapeStrings.put(LWPrintTapeWidth.Normal_36mm, "36mm");
		tapeStrings.put(LWPrintTapeWidth.Unknown, "Unknown");

		return tapeStrings.get(tapeWidth);
	}*/

	public static String rightPadZeros(String str, int num) {
		return String.format("%1$-" + num + "s", str).replace(' ', '0');
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(final Boolean mode) {
		processing = mode;

		handler.postDelayed(new Runnable() {
			public void run() {
				buttonDiscoverPrinter.setEnabled(!mode);
				buttonSelectXml.setEnabled(!mode);
				editTextInputData.setEnabled(!mode);
				buttonPrint.setEnabled(!mode);
			}
		}, 1);
	}

	private void doCancel() {
		lwprint.cancelPrint();
	}

	private void performPrint() {
		if (_formNames == null) {
			return;
		}
		if (_formNames.size() <= _jobNumber) {
			printComplete(LWPrintConnectionStatus.NoError, LWPrintStatusError.NoError, false);
			setProcessing(false);
			return;
		}
		if (printerInfo == null) {
			setProcessing(false);
			return;
		}

		updateFormButton();

		adjustButtons(false);

		Message msg = new Message();
		msg.obj = "";
		resultHandler.sendMessage(msg);

		handler.postDelayed(new Runnable() {
			public void run() {
				if (progressDialog != null) {
					progressDialog.show();
				}
			}
		}, 1);

		new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				// Set printing information
				lwprint.setPrinterInformation(printerInfo);

				/*// Obtain printing status
				lwStatus = lwprint.fetchPrinterStatus();
				int deviceError = lwprint.getDeviceErrorFromStatus(lwStatus);
				if (lwStatus.isEmpty() || (deviceError == LWPrintStatusError.ConnectionFailed)) {
					return false;
				}*/

				/*if (lwStatus == null) {
					lwStatus = lwprint.fetchPrinterStatus();
				}*/
				lwStatus = lwprint.fetchPrinterStatus();
				int deviceError = lwprint.getDeviceErrorFromStatus(lwStatus);
				//notifyPrinterStatus(deviceError);
				if (lwStatus.isEmpty() || (deviceError == LWPrintStatusError.ConnectionFailed)) {
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					return false;
				}

				//print additional text
				if (_formNames.get(_jobNumber).equals("QRCode")) {
					sampleDataProvider.setFormName(_formNames.get(_jobNumber) + SUFFIX);
					int itemID = (int) singleItem.getId();
					//int eventID = (int) associatedEvent.getId();
					//String concatenatedCode = Integer.toString(eventID) + "," + Integer.toString(itemID);
					sampleDataProvider.setQrCodeData(Integer.toString(itemID)); //set Item Identifier for QRCode Print
				} else if (_formNames.get(_jobNumber).equals("Barcode")) {
					sampleDataProvider.setFormName(_formNames.get(_jobNumber) + SUFFIX);
					int itemID = (int) singleItem.getId();
					//int eventID = (int) associatedEvent.getId();

					String barcodeType = sampleDataProvider.getBarcodeType();
					//String concatenatedCode = null;
					String itemIDPadded = null;
					if ("Code-EAN8".equals(barcodeType)) {
						//String itemIDPadded = String.format("%04d", itemID);
						//String eventIDPadded = String.format("%03d", eventID);
						itemIDPadded = String.format("%07d", itemID);
						//concatenatedCode = eventIDPadded + itemIDPadded;
					} else if ("Code-EAN13".equals(barcodeType)) {
						//String itemIDPadded = String.format("%07d", itemID);
						//String eventIDPadded = String.format("%05d", eventID);
						itemIDPadded = String.format("%012d", itemID);
						//concatenatedCode = eventIDPadded + itemIDPadded;
					}

					sampleDataProvider.setBarcodeData(itemIDPadded); //set Item Identifier for Barcode Print
				} else if (_formNames.get(_jobNumber).equals("String")) {
					sampleDataProvider.setFormName("String" + SUFFIX);
				}

				// Make a print parameter
				int tapeWidth = lwprint.getTapeWidthFromStatus(lwStatus);

				//notifyPrinterStatus(deviceError, tapeWidth);

				if (SettingsActivity.getInstance() != null) {
					printSettings = SettingsActivity.getInstance().getPrintSettings();
				} else {
					printSettings = new HashMap<String, Object>();
					// Number of copies(1 ... 99)
					printSettings.put(LWPrintParameterKey.Copies, 1);
					// Tape cut method(LWPrintTapeCut)
					Object put = printSettings.put(LWPrintParameterKey.TapeCut,
							LWPrintTapeCut.EachLabel);
					// Set half cut (true:half cut on)
					printSettings.put(LWPrintParameterKey.HalfCut,
							true);
					// Low speed print setting (true:low speed print on)
					printSettings.put(LWPrintParameterKey.PrintSpeed,
							false);
					// Print density(-5 ... 5)
					printSettings.put(LWPrintParameterKey.Density,
							0);
					// Tape width(LWPrintTapeWidth)
					printSettings.put(LWPrintParameterKey.TapeWidth, tapeWidth);
				}

				// Carry out printing
				lwprint.doPrint(sampleDataProvider, printSettings);

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result == false) {
					setProcessing(false);

					String message = "Connection failed. Turn on label printer.";
					alertAbortOperation("Error", message);
				}
			}
		}.execute();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		TimerTask task = (new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						float progress = lwprint.getProgressOfPrint();
						if (progressDialog != null) {
							progressDialog.setProgress((int) (progress * 100));
						}
						int printingPage = lwprint.getPageNumberOfPrinting();
						textPrintingPage.setText(String.valueOf(printingPage));
					}
				});
			}
		});
		timer.schedule(task, 1000, 1000);

	}

	@Override
	public void onDestroy() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		sampleDataProvider.closeStreams();

		// Keep screen off
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();

		/*
		 * You'll need this in your class to release the helper when done.
		 */
		/*if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}*/
	}

	private void updateFormButton() {
		handler.postDelayed(new Runnable() {
			public void run() {
				if (_formNames != null) {
					buttonSelectXml.setText("Select: "
							+ "["
							+ _formNames.get(_jobNumber)
							+ "] "
							+ (_jobNumber + 1) + " / " + _formNames.size());
				} else {
					buttonSelectXml.setText("Select"
							+ "[Nothing]");
				}
			}
		}, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode != RESULT_OK) {
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not enabled.", Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_ACTIVITY_SEARCH:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					if (printerInfo != null) {
						printerInfo.clear();
						printerInfo = null;
					}
					printerInfo = new HashMap<String, String>();
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_NAME,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_NAME));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_HOST,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_HOST));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_PORT,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PORT));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS));
					printerInfo
							.put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS,
									extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS));
					buttonDiscoverPrinter.setText(getResources().getString(
							R.string.button_search)
							+ extras.getString("name"));
				}
			}
			break;
		case REQUEST_ACTIVITY_SELECT_XML:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					_formNames = extras.getStringArrayList(KEY_FORMDATA);
					String name = _formNames.get(_jobNumber);
					buttonSelectXml.setText("Select: "
							+ "["
							+ _formNames.get(_jobNumber)
							+ "] "
							+ (_jobNumber + 1) + " / " + _formNames.size());

					if ("Barcode".equals(name)) {
						Intent intent = null;
						intent = new Intent(this, ChooseBarcodeTypeActivity.class);
						startActivityForResult(intent, REQUEST_ACTIVITY_CHOOSE_BARCODE_TYPE);
					}
				}
			}
			break;
		case REQUEST_ACTIVITY_CHOOSE_BARCODE_TYPE:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					String barcode_type = extras.getString("Barcode Type");
					sampleDataProvider.setBarcodeType(barcode_type);
				}
			}
			break;
		default:
			break;
		}
	}

	private Handler resultHandler = new Handler() {
		public void handleMessage(Message msg) {
			textResult.setText((String) msg.obj);
		}
	};

	public void printComplete(int connectionStatus, int status, boolean suspend) {
		String msg = "";
		if (connectionStatus == LWPrintConnectionStatus.NoError && status == LWPrintStatusError.NoError) {
			msg = "Print Complete.";
		} else {
			if (suspend) {
				msg = "Print Error Re-Print [" + Integer.toHexString(status)
						+ "].";
			} else {
				msg = "Print Error [" + Integer.toHexString(status) + "].";
			}
		}

		String appName = getResources().getString(R.string.app_name);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Notification messages";
		long when = System.currentTimeMillis();
		/*Notification notification = new Notification(icon, tickerText, when);
		CharSequence contentTitle = appName;
		CharSequence contentText = msg;
		Intent intent = new Intent(this, getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);*/
	}

	public void alertAbortOperation(final String title, final String message) {
		handler.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						PrintActivity.this);
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

	public void alertSuspendPrintOperation(final String title,
			final String message) {
		handler.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						PrintActivity.this);
				alert.setTitle(title);
				alert.setMessage(message);
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (progressDialog != null) {
									progressDialog.show();
								}
								lwprint.resumeOfPrint();
							}
						});
				alert.setNegativeButton("Cancel",
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

	private void adjustButtons(final boolean enabled) {
		handler.postDelayed(new Runnable() {
			public void run() {
				buttonPrint.setEnabled(enabled);
			}
		}, 1);
	}

	private void runProgressDialogForPrinting() {
		handler.post(new Runnable() {
			public void run() {
				createProgressDialogForPrinting();
			}
		});
	}

	public class PrintCallback implements LWPrintCallback {

		public void onChangePrintOperationPhase(LWPrint lWPrint, int phase) {
			// Report the change of a printing phase
			Logger.d("onChangePrintOperationPhase: phase=" + phase);
			waitDialog.dismiss();
			String jobPhase = "";
			switch (phase) {
				case LWPrintPrintingPhase.Prepare:
					jobPhase = "PrintingPhasePrepare";
					break;
				case LWPrintPrintingPhase.Processing:
					jobPhase = "PrintingPhaseProcessing";
					break;
				case LWPrintPrintingPhase.WaitingForPrint:
					jobPhase = "PrintingPhaseWaitingForPrint";
					break;
				case LWPrintPrintingPhase.Complete:
					jobPhase = "PrintingPhaseComplete";
					//printComplete(LWPrintConnectionStatus.NoError, LWPrintStatusError.NoError, false);
					//setProcessing(false);
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					if (progressDialog != null) {
						progressDialog.setProgress(0);
						progressDialog.dismiss();
						progressDialog = null;
					}
					runProgressDialogForPrinting();
					adjustButtons(true);

					_jobNumber++;
					performPrint();
					break;
				default:
					//setProcessing(false);
					if (progressDialog != null) {
						progressDialog.setProgress(0);
						progressDialog.dismiss();
						progressDialog = null;
					}
					runProgressDialogForPrinting();
					adjustButtons(true);
					break;
			}
			Logger.d("phase=" + jobPhase);

			Message msg = new Message();
			msg.obj = jobPhase;
			resultHandler.sendMessage(msg);
		}

		public void onChangeTapeFeedOperationPhase(LWPrint lWPrint, int phase) {
			// Called when tape feed and tape cutting state transitions
			Logger.d("onChangeTapeFeedOperationPhase: phase=" + phase);
			String jobPhase = "";
			switch (phase) {
				case LWPrintPrintingPhase.Prepare:
					jobPhase = "PrintingPhasePrepare";
					break;
				case LWPrintPrintingPhase.Processing:
					jobPhase = "PrintingPhaseProcessing";
					break;
				case LWPrintPrintingPhase.WaitingForPrint:
					jobPhase = "PrintingPhaseWaitingForPrint";
					break;
				case LWPrintPrintingPhase.Complete:
					jobPhase = "PrintingPhaseComplete";
					waitDialog.dismiss();
					adjustButtons(true);
					setProcessing(false);
					break;
				default:
					waitDialog.dismiss();
					adjustButtons(true);
					break;
			}
			Message msg = new Message();
			msg.obj = jobPhase;
			resultHandler.sendMessage(msg);
		}

		public void onAbortPrintOperation(LWPrint lWPrint, int errorStatus,
										  int deviceStatus) {
			// It is called when undergoing a transition to the printing cancel operation due to a printing error
			Logger.d("onAbortPrintOperation: errorStatus=" + errorStatus
					+ ", deviceStatus=" + deviceStatus);

			waitDialog.dismiss();
			printComplete(errorStatus, deviceStatus, false);

			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			if (progressDialog != null) {
				progressDialog.setProgress(0);
				progressDialog.dismiss();
				progressDialog = null;
			}
			runProgressDialogForPrinting();
			setProcessing(false);

			String message = "Error Status : " + errorStatus
					+ "\nDevice Status : " + Integer.toHexString(deviceStatus);

			Message msg = new Message();
			msg.obj = message;
			resultHandler.sendMessage(msg);

			alertAbortOperation("Print Error!", message);
		}

		public void onSuspendPrintOperation(LWPrint lWPrint, int errorStatus,
											int deviceStatus) {
			// It is called when undergoing a transition to the printing restart operation due to a printing error
			Logger.d("onSuspendPrintOperation: errorStatus=" + errorStatus
					+ ", deviceStatus=" + deviceStatus);

			waitDialog.dismiss();
			printComplete(errorStatus, deviceStatus, true);
			if (progressDialog != null) {
				progressDialog.setProgress(0);
				progressDialog.dismiss();
				progressDialog = null;
			}
			runProgressDialogForPrinting();

			String message = "Error Status : " + errorStatus
					+ "\nDevice Status : " + Integer.toHexString(deviceStatus);

			Message msg = new Message();
			msg.obj = message;
			resultHandler.sendMessage(msg);

			alertSuspendPrintOperation("Print Error! re-print ?", message);
		}

		public void onAbortTapeFeedOperation(LWPrint lWPrint, int errorStatus,
											 int deviceStatus) {
			// Called when tape feed and tape cutting stops due to an error
			Logger.d("errorStatus=" + errorStatus + ", deviceStatus=" + deviceStatus);

			adjustButtons(true);

			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			waitDialog.dismiss();
			setProcessing(false);

			String message = "Error Status : " + errorStatus
					+ "\nDevice Status : " + Integer.toHexString(deviceStatus);
			Message msg = new Message();
			msg.obj = message;
			resultHandler.sendMessage(msg);

			alertAbortOperation("Tape Feed Error!", message);
		}
	}






}
