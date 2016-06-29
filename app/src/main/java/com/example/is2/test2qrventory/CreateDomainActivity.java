package com.example.is2.test2qrventory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.is2.test2qrventory.connection.DomainAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;

public class CreateDomainActivity extends AppCompatActivity implements VolleyResponseListener {

    private int PICK_IMAGE_REQUEST = 1;
    private Button loadImage;
    private FloatingActionButton fab_save;
    private EditText textEditName;
    private EditText textEditDescription;
    private Bitmap image_upload = null;

    private Domain domain_to_save = null;
    private Domain domain_response = null;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_domain);

        textEditName = (EditText) findViewById(R.id.name);
        textEditDescription = (EditText) findViewById(R.id.description);

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setOnClickListener(onSaveDomainHandler);

        user = getIntent().getParcelableExtra("user");

        loadImage = (Button) findViewById(R.id.buttonLoadImage);
        loadImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickLoadImage();
            }
        });
    }

    View.OnClickListener onSaveDomainHandler = new View.OnClickListener() {
        public void onClick(View v) {
            saveDomain();
        }
    };

    private void saveDomain() {
        // save into database
        String name = textEditName.getText().toString();
        String description = textEditDescription.getText().toString();

        domain_to_save = new Domain();
        domain_to_save.setName(name);
        domain_to_save.setDescription(description);

        if(image_upload != null) {
            domain_to_save.setImageURL(image_upload.toString());
        }

        DomainAccess domainAccess = new DomainAccess(user.getApiKey());
        domainAccess.addDomain(this, domain_to_save);
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
        if(response != null) {
            domain_response = (Domain) response;
        }

        // toast notification

    }

    /* Optional getURL at/up API 19
    /* private void getUri() {
        Uri uri = data.getData();
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        //Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();
    }*/
}
