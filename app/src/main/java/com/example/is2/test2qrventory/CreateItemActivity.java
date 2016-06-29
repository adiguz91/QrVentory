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
import android.widget.Toast;

import com.example.is2.test2qrventory.connection.ItemAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;

public class CreateItemActivity extends AppCompatActivity implements VolleyResponseListener {

    private int PICK_IMAGE_REQUEST = 1;
    private Button loadImage;
    private FloatingActionButton fab_save;
    private EditText textEditName;
    private EditText textEditDescription;
    private Bitmap image_upload = null;
    private Toast toast;

    private User user = null;
    private long domain_id = 0;
    private int category_parent_id = 0;
    private Item item_to_save = null;
    private Item item_response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        textEditName = (EditText) findViewById(R.id.name_item);
        textEditDescription = (EditText) findViewById(R.id.description_item);

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save_item);
        fab_save.setOnClickListener(onSaveItemHandler);

        user = getIntent().getParcelableExtra("user");

        domain_id = getIntent().getLongExtra("domain_id", 0);
        category_parent_id = getIntent().getIntExtra("category_parent_id", 0);

        loadImage = (Button) findViewById(R.id.buttonLoadImage_item);
        loadImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickLoadImage();
            }
        });
    }

    View.OnClickListener onSaveItemHandler = new View.OnClickListener() {
        public void onClick(View v) {
            saveItem();
        }
    };

    private void saveItem() {
        // save into database
        String name = textEditName.getText().toString();
        String description = textEditDescription.getText().toString();

        item_to_save = new Item();
        item_to_save.setName(name);
        item_to_save.setDescription(description);
        item_to_save.setIsQR(true);

        if(image_upload != null) {
            item_to_save.setImageURL(image_upload.toString());
        }

        if(domain_id != 0 && category_parent_id != 0) {
            ItemAccess itemAccess = new ItemAccess(user.getApiKey());
            itemAccess.addItem(this, item_to_save, domain_id, category_parent_id);
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
            item_response = (Item) response;
            message = "Saved";
        }
        ShowToastMessage(message);
    }

    private void ShowToastMessage(String message) {
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
