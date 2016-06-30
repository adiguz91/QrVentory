package com.example.is2.test2qrventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, VolleyResponseListener {

    private int PICK_IMAGE_REQUEST = 1;
    private Button loadImage;
    private FloatingActionButton fab_save;
    private EditText textEditName;
    private EditText textEditDescription;
    private Bitmap image_upload = null;
    private Toast toast;

    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;
    private EditText textStartDate;
    private EditText textEndDate;
    private EditText textStartTime;
    private EditText textEndTime;
    //private int year, month, day;

    private User user = null;
    private long domain_id = 0;
    private Event event_to_save = null;
    private Event event_response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        textEditName = (EditText) findViewById(R.id.name_event);
        textEditDescription = (EditText) findViewById(R.id.description_event);

        textStartDate = (EditText) findViewById(R.id.textEditStartDate);
        textEndDate = (EditText) findViewById(R.id.textEditEndDate);
        textStartTime = (EditText) findViewById(R.id.textEditStartTime);
        textEndTime = (EditText) findViewById(R.id.textEditEndTime);

        textStartDate.setInputType(InputType.TYPE_NULL);
        textEndDate.setInputType(InputType.TYPE_NULL);

        textStartTime.setInputType(InputType.TYPE_NULL);
        textEndTime.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String current_time = timeFormatter.format(Calendar.getInstance().getTime());

        textStartTime.setText(current_time);
        textEndTime.setText(current_time);

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save_event);
        fab_save.setOnClickListener(onSaveEventHandler);

        user = getIntent().getParcelableExtra("user");

        domain_id = getIntent().getLongExtra("domain_id", 0);
        //category_parent_id = getIntent().getIntExtra("category_parent_id", 0);

        loadImage = (Button) findViewById(R.id.buttonLoadImage_event);
        loadImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickLoadImage();
            }
        });


        setDateField();
        setTimeField();
    }

    /* datepicker --------------------------------------------------------------------------------*/

    private void setDateField() {
        textStartDate.setOnClickListener(this);
        textEndDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                textStartDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                textEndDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setTimeField() {
        textStartTime.setOnClickListener(this);
        textEndTime.setOnClickListener(this);

        Calendar newTime = Calendar.getInstance();
        startTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + "00";
                textStartTime.setText(time);
            }
        },newTime.get(Calendar.HOUR), newTime.get(Calendar.MINUTE), true);

        endTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + "00";
                textEndTime.setText(time);
            }

        },newTime.get(Calendar.HOUR), newTime.get(Calendar.MINUTE), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == textStartDate) {
            startDatePickerDialog.show();
        } else if(view == textEndDate) {
            endDatePickerDialog.show();
        }

        if(view == textStartTime) {
            startTimePickerDialog.show();
        } else if(view == textEndTime) {
            endTimePickerDialog.show();
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    View.OnClickListener onSaveEventHandler = new View.OnClickListener() {
        public void onClick(View v) {
            /*if(textStartDate.getText().toString() != null && textStartDate.getText().toString() != "" &&
                    textStartTime.getText().toString() != null && textStartTime.getText().toString() != "" &&
                    textEndDate.getText().toString() != null && textEndDate.getText().toString() != "" &&
                    textEndTime.getText().toString() != null && textEndTime.getText().toString() != "") {
                saveEvent();
            } else {
                ShowToastMessage("Please select Datetime");
            }*/
            saveEvent();
        }
    };

    private void saveEvent() {
        // save into database
        String name = textEditName.getText().toString();
        String description = textEditDescription.getText().toString();
        int status = 0; // 0 == inaktive == not started

        event_to_save = new Event();

        String start_datetime = textStartDate.getText().toString() + " " + textStartTime.getText().toString();
        String end_datetime = textEndDate.getText().toString() + " " + textEndTime.getText().toString();

        event_to_save.setName(name);
        event_to_save.setDescription(description);
        event_to_save.setAutoStart(false);
        event_to_save.setIdDomain(domain_id);
        event_to_save.setStatus(status);
        event_to_save.setStartDate(event_to_save.StringtoDateParser(start_datetime));
        event_to_save.setEndDate(event_to_save.StringtoDateParser(end_datetime));

        if(image_upload != null) {
            event_to_save.setImageURL(image_upload.toString());
        }

        if(domain_id != 0) {
            EventAccess eventAccess = new EventAccess(user.getApiKey());
            eventAccess.addEvent(this, event_to_save);
        }
    }

    public void onButtonClickLoadImage()  {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                image_upload = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {
        String message = "Failed to Save";
        if(response != null) {
            event_response = (Event) response;
            message = "Saved";
        }
        ShowToastMessage(message);
    }

    private void ShowToastMessage(String message) {
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
